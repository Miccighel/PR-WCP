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

import pcw.PCW;

public class ArticlesHandler {
	
	public boolean[][] keyphrasesMatrix;
	public boolean[][] citationsMatrix;
	
	public ArticlesHandler() {
		if (!(new File("data/keyphrasesMatrix.ser")).exists() && (!(new File("data/citationsMatrix.ser")).exists())) {
			buildKeyphrasesMatrix(PCW.articles);
			buildCitationsMatrix(PCW.articles);
			saveMatrixes();
		}
		else
			loadMatrixes();
	}
	
	/**
	 * Usato per forzare l'update delle matrici da una certa lista
	 */
	public void updateMatrixes(List<Article> list) {
		buildKeyphrasesMatrix(list);
		buildCitationsMatrix(list);
		saveMatrixes();
	}
	
	/*
	 *               kp1 kp2 kp3
	 * article1    0     1    1
	 * article2    1     0    1
	 * article3    0     1    0
	 */
	public void buildKeyphrasesMatrix(List<Article> list) {
		ArrayList<String> allKeyphrases = new ArrayList<String>();
		Set<String> keySet = new HashSet<String>();
		
		// unisco e ordino tutte le keyphrase
		for (Article article : list)
			for (String keyphrase : article.getKeyphrases())
				keySet.add(keyphrase);
		allKeyphrases.addAll(keySet);
		Collections.sort(allKeyphrases);
		
		keyphrasesMatrix = new boolean[allKeyphrases.size()][list.size()]; // java inizializza tutta la matrice a false
		
		// costruisco la matrice
		for (int i=0; i<list.size(); i++) {
			Article article = list.get(i);
			List<String> keyphrasesList = article.getKeyphrases();
			for (int j=0; j<allKeyphrases.size(); j++)
				keyphrasesMatrix[i][j] = keyphrasesList.contains(allKeyphrases.get(j));
		}
	}
	
	/*
	 * NOTA: questa matrice non è quadrata perché non tutti gli articoli citati sono presenti nel dataset
	 *               article1 article2 article3 article4
	 * article1       0            1            1          0
	 * article2       1            0            1          0
	 * article3       0            1            0          1
	 */
	public void buildCitationsMatrix(List<Article> list) {
		ArrayList<String> allCitations = new ArrayList<String>();
		Set<String> citationsSet = new HashSet<String>();
		
		// unisco e ordino tutte le keyphrase
		for (Article article : list)
			for (String cite : article.getCites())
				citationsSet.add(cite);
		allCitations.addAll(citationsSet);
		Collections.sort(allCitations);
		
		keyphrasesMatrix = new boolean[allCitations.size()][list.size()]; // java inizializza tutta la matrice a false
		
		// costruisco la matrice
		for (int i=0; i<list.size(); i++) {
			Article article = list.get(i);
			List<String> citationsList = article.getCites();
			for (int j=0; j<allCitations.size(); j++)
				keyphrasesMatrix[i][j] = citationsList.contains(allCitations.get(j));
		}
	}
	
	
	
	/**
	 * Salva le matrici in data/
	 */
 	public void saveMatrixes() {
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
 		} catch (IOException e) {
 			e.printStackTrace();
 		}	
 	}
 	/**
 	 * Carica le matrici serializzate in data/
 	 */
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
 		} catch (IOException | ClassNotFoundException e) {
 	         e.printStackTrace();
 	    }
 	}
}
