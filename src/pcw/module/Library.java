package pcw.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import pcw.parsing.LoadArticles;
import pcw.utils.Article;

public class Library {
	
	private List<Article> articles;
	private boolean[][] keyphrasesMatrix; // Term-Document Frequency Matrix
	private boolean[][] citationsMatrix;
	private static Library instance;
	ArrayList<String> allKeyphrases;
	
	public static Library getInstance() {
		if (instance == null)
			instance = new Library();
		return instance;
	}
	
	private Library() {
		
		if ((new File("data/articles.ser")).exists())
			this.loadArticles();
		else {
			System.out.println("data/articles.ser non trovato, costruisco la lista degli articoli...");
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			try {
				SAXParser saxParser = saxParserFactory.newSAXParser();
				saxParser.parse(new File("data/dataset3final.xml"), new LoadArticles(this)); // TODO cambiare col file del dataset
			}
			catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
			System.out.println("Completato, con "+this.articles.size()+" articoli");
		}
		
		if (!(new File("data/keyphrasesMatrix.ser")).exists() 
				&& (!(new File("data/citationsMatrix.ser")).exists())) {
			System.out.println("Matrici non trovate, costruisco le matrici delle keyphrase e delle citazioni...");
			buildKeyphrasesMatrix(articles);
			System.out.println("Matrice delle keyphrase costruita");
			buildCitationsMatrix(articles);
			System.out.println("Matrice delle citazioni costruita");
			this.saveMatrix();
         System.out.println("Salvataggio matrici completato");
			this.saveArticles();
         System.out.println("Salvataggio articoli completato");
		}
		else
			loadMatrixes();
	}
	
	private Article getArticleFromTitle(String title) {
		for (Article article : this.articles)
			if (article.getTitle().equalsIgnoreCase(title))
				return article;
		return null;
	}

	public void setArticleList(List<Article> list) {
		this.articles = list;
	}
	public List<Article> getArticleList() {
		return this.articles;
	}
	public boolean[][] getKeyphrasesMatrix() {
		return this.keyphrasesMatrix;
	}
	public boolean[][] getCitationsMatrix() {
		return this.citationsMatrix;
	}
	public List<String> getKeyphrases() {
		return this.allKeyphrases;
	}
	
	/**
	 * Usato per forzare l'update delle matrici da una certa lista
	 */
	public void updateMatrixes(List<Article> list) {
		buildKeyphrasesMatrix(list);
		buildCitationsMatrix(list);
		saveMatrix();
	}
	
	/*
	 *               article1 article2 article3
	 * kph1           0           1           1
	 * kph2           1           0           1
	 * kph3           0           1           0
	 * kph4           1           1           0
	 */
	private void buildKeyphrasesMatrix(List<Article> list) {
		allKeyphrases = new ArrayList<String>();
		Set<String> keySet = new HashSet<String>();
		int count = 0;
		// unisco e ordino tutte le keyphrase
		for (Article article : list)
			for (String keyphrase : article.getKeyphrases())
				if (!keySet.add(keyphrase)) count++;
		System.out.println("keyphrases in comune: " + count);
		allKeyphrases.addAll(keySet);
		Collections.sort(allKeyphrases);
		
		keyphrasesMatrix = new boolean[list.size()][allKeyphrases.size()]; // java inizializza tutta la matrice a false
		
		// costruisco la matrice
		for (int i=0; i<list.size(); i++) {
			Article article = list.get(i);
			List<String> articleKeyphrasesList = article.getKeyphrases();
			for (int j=0; j<allKeyphrases.size(); j++)
				keyphrasesMatrix[i][j] = articleKeyphrasesList.contains(allKeyphrases.get(j));
			article.setKeyphrasesVector(keyphrasesMatrix[i]); // superfluo?
		}
	}
	
	/*
	 * NOTA: questa matrice non e' quadrata perche' non tutti gli articoli citati sono presenti nel dataset
	 *               article1 article2 article3
	 * cite1           0            1            1
	 * cite2           1            0            1
	 * cite3           0            1            0
	 * cite4           1            0            0
	 */
	private void buildCitationsMatrix(List<Article> list) {
		ArrayList<String> allCitations = new ArrayList<String>();
		Set<String> citationsSet = new HashSet<String>();
		int count = 0;
      this.citationsMatrix = new boolean[list.size()][list.size()];
		
      System.out.println(list.get(0).toString());
      
      for (int i=0; i<list.size(); i++) {
         Article article = this.articles.get(i);
         for (String cite : article.getCitesStringList()) {
            Article a = this.getArticleFromTitle(cite);
            if (a != null) {
               this.citationsMatrix[i][this.getIndexFromArticle(a)] = true;
               this.citationsMatrix[this.getIndexFromArticle(a)][i] = true;
               article.addCite(a);
               count++;
            }
         }
      }
      
      System.out.println("metodo completato, " + count + " citazioni in comune");
      
      int q = 0;
      for (Article a : this.articles)
         if (a.getCites().size() == 0)
            q++;
      System.out.println(q+" articoli non hanno citazioni");
	}


	/**
	 * Restituisce l'indice nella lista dell'articolo cercato, -1 se non trova l'articolo
	 */
	public int getIndexFromArticle(Article article) {
		int index = 0;
		for (Article a : this.articles)
			if (a.equals(article))
				return index;
			else
				index++;
		return -1;
	}
	
	
	public void saveArticles() {
		try {
 			FileOutputStream fileOut = new FileOutputStream("data/articles.ser");
 			ObjectOutputStream out = new ObjectOutputStream(fileOut);
 			out.writeObject(articles);
 			out.close();
 			fileOut.close();
		}
		catch (IOException e) {
 			e.printStackTrace();
 		}	
	}
	/**
	 * Salva le matrici in data/
	 */
 	public void saveMatrix() {
 		try {
 			FileOutputStream fileOut = new FileOutputStream("data/keyphrasesMatrix.ser");
 			ObjectOutputStream out = new ObjectOutputStream(fileOut);
 			out.writeObject(keyphrasesMatrix);
 			out.close();
 			fileOut.close();
 			fileOut = new FileOutputStream("data/citationsMatrix.ser");
 			out = new ObjectOutputStream(fileOut);
 			out.writeObject(citationsMatrix);
 			out.close();
 			fileOut.close();
 			fileOut = new FileOutputStream("data/keyphrasesList.ser");
 			out = new ObjectOutputStream(fileOut);
 			out.writeObject(allKeyphrases);
 			out.close();
 			fileOut.close();
 		}
 		catch (IOException e) {
 			e.printStackTrace();
 		}	
 	}
 	@SuppressWarnings("unchecked")
	public void loadArticles() {
 		try {
 			FileInputStream fileIn = new FileInputStream("data/articles.ser");
 			ObjectInputStream in = new ObjectInputStream(fileIn);
 			articles = (ArrayList<Article>) in.readObject();
 			in.close();
 			fileIn.close();
 		}
 		catch (IOException | ClassNotFoundException e) {
	         e.printStackTrace();
	    }
 	}
 	/**
 	 * Carica le matrici serializzate in data/
 	 */
	@SuppressWarnings("unchecked")
	public void loadMatrixes() {
 		try {
 			FileInputStream fileIn = new FileInputStream("data/keyphrasesMatrix.ser");
 			ObjectInputStream in = new ObjectInputStream(fileIn);
 			keyphrasesMatrix = (boolean[][]) in.readObject();
 			in.close();
 			fileIn.close();
 			fileIn = new FileInputStream("data/citationsMatrix.ser");
 			in = new ObjectInputStream(fileIn);
 			citationsMatrix = (boolean[][]) in.readObject();
 			in.close();
 			fileIn.close();
 			fileIn = new FileInputStream("data/keyphrasesList.ser");
 			in = new ObjectInputStream(fileIn);
 			allKeyphrases = (ArrayList<String>) in.readObject();
 			in.close();
 			fileIn.close();
 		}
 		catch (IOException | ClassNotFoundException e) {
 	         e.printStackTrace();
 	    }
 	}
}
