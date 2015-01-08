package pcw.utils;

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

import pcw.parsins.LoadArticles;

public class Library {
	
	private List<Article> articles;
	private boolean[][] keyphrasesMatrix;
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
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			try {
				SAXParser saxParser = saxParserFactory.newSAXParser();
				saxParser.parse(new File("data/testLoad.xml"), new LoadArticles()); // TODO cambiare col file del dataset
			}
			catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
			this.saveArticles();
		}
		
		if (!(new File("data/articles.ser")).exists() 
				&& !(new File("data/keyphrasesMatrix.ser")).exists() 
				&& (!(new File("data/citationsMatrix.ser")).exists())) {
			buildKeyphrasesMatrix(articles);
			buildCitationsMatrix(articles);
			this.saveMatrix();
		}
		else
			loadMatrixes();
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
		
		// unisco e ordino tutte le keyphrase
		for (Article article : list)
			for (String keyphrase : article.getKeyphrases())
				keySet.add(keyphrase);
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
		
		for (Article article : list)
			for (String cite : article.getCites())
				citationsSet.add(cite);
		allCitations.addAll(citationsSet);
		Collections.sort(allCitations);
		
		citationsMatrix = new boolean[list.size()][allCitations.size()]; // java inizializza tutta la matrice a false
		
		// costruisco la matrice
		for (int i=0; i<list.size(); i++) {
			Article article = list.get(i);
			List<String> citationsList = article.getCites();
			for (int j=0; j<allCitations.size(); j++)
				citationsMatrix[i][j] = citationsList.contains(allCitations.get(j));
		}
	}


	/**
	 * Restituisce gli articoli piu' simili a quelli passati in ingresso ordinati per somiglianza
	 */
	public List<Article> getNeighbours(boolean[] userVector, int numOfNeighbours) {
		double[] similarities = new double[userVector.length];
		
		for (int i=0; i<this.keyphrasesMatrix.length; i++)
			similarities[i] = similarity(userVector, keyphrasesMatrix[i]);
		
		List<Article> out = new ArrayList<Article>();
		for (int i=0; i<numOfNeighbours+1; i++) {
			int index = indexOfMax(similarities);
			out.add(articles.get(index));
			similarities[index] = 0;
		}
		out.remove(0); // l'articolo piu' simile e' se stesso, quindi lo tolgo
		
		return out;
	}
	
	// Similarity (%) = 100 * (commonItems * 2) / (total item in vector1 + total item in vector2)
	public double similarity(boolean[] v, boolean[] s) {
		int commonItems = 0;
		int itemsInV = 0;
		int itemsInS = 0;
		
		if (v.length != s.length)
			return 0;
		
		for (int i=0; i<v.length; i++) {
			if (v[i])
				itemsInV++;
			if (s[i])
				itemsInS++;
			if (v[i] && s[i])
				commonItems++;
		}
		
		return 100 * ( (2 * commonItems) / (itemsInV + itemsInS));
	}
	public void rankNeighbours(boolean[] userVector) {
		// TODO ?
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
	private int indexOfMax(double[] a) {
		int index = -1;
		double max = -1;
		for (int i=0; i<a.length; i++)
			if (a[i] > max) {
				index = i;
				max = a[i];
			}
		return index;
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
