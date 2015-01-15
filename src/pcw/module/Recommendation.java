package pcw.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pcw.utils.Article;

public class Recommendation {
	
	/**
	 * Restituisce gli articoli piu' simili a quelli passati in ingresso ordinati per somiglianza
	 */
	public static List<Article> getNeighbours(boolean[] userVector, int numOfNeighbours) {
		double[] similarities = new double[userVector.length];
		
		for (int i=0; i<Library.getInstance().getKeyphrasesMatrix().length; i++)
			similarities[i] = similarity(userVector, Library.getInstance().getKeyphrasesMatrix()[i]);
		
		List<Article> out = new ArrayList<Article>();
		for (int i=0; i<numOfNeighbours; i++) {
			int index = indexOfMax(similarities);
			out.add(Library.getInstance().getArticleList().get(index));
			similarities[index] = -1;
		}
		
		return out;
	}
	/**
	 * Restituisce gli articoli piu' simili a quelli passati in ingresso ordinati per somiglianza
	 */
	public static List<Article> getArticleNeighbours(Article article, int numOfNeighbours) {
		double[] similarities = new double[article.getKeyphrasesVector().length];
		
		for (int i=0; i<Library.getInstance().getKeyphrasesMatrix().length; i++)
			similarities[i] = similarity(article.getKeyphrasesVector(), Library.getInstance().getKeyphrasesMatrix()[i]);
		
		similarities[Library.getInstance().getIndexFromArticle(article)] = -2; // evita che dia in output se stesso
		
		List<Article> out = new ArrayList<Article>();
		for (int i=0; i<numOfNeighbours; i++) {
			int index = indexOfMax(similarities);
			out.add(Library.getInstance().getArticleList().get(index));
			similarities[index] = -1;
		}
		
		return out;
	}
	
	/**
	 * Restituisce un array ordinato di valori di similarita', serve per la dcg e ndcg
	 * @param userVector Il vettore di cui cerco i vettori simili
	 * @param numOfNeighbours Il numero di risultati che cerco
	 * @return Un array ordinato di double compresi tra 0 e 1 lungo numOfNeighbours
	 */
	public static double[] getSortedNeighboursSimilarity(boolean[] userVector, int numOfNeighbours) {
		ArrayList<Double> similarities = new ArrayList<Double>();
		
		for (int i=0; i<numOfNeighbours; i++)
			similarities.add(similarity(userVector, Library.getInstance().getKeyphrasesMatrix()[i]));
		Collections.sort(similarities);
		Collections.reverse(similarities);
		
		double[] out = new double[similarities.size()]; // stupido java
		for (int i=0; i<out.length; i++)
			out[i] = similarities.get(i);
		return out;
	}
	/**
	 * Restituisce un array ordinato di valori di similarita', serve per la dcg e ndcg
	 * @param userVector Il vettore di cui cerco i vettori simili
	 * @param numOfNeighbours Il numero di risultati che cerco
	 * @return Un array ordinato di double compresi tra 0 e 1 lungo numOfNeighbours
	 */
	public static double[] getArticleSortedNeighboursSimilarity(Article article, int numOfNeighbours) {
		ArrayList<Double> similarities = new ArrayList<Double>();
		
		for (int i=0; i<numOfNeighbours; i++)
			similarities.add(similarity(article.getKeyphrasesVector(), Library.getInstance().getKeyphrasesMatrix()[i]));
		
		similarities.set(Library.getInstance().getIndexFromArticle(article), -2D);
		Collections.sort(similarities);
		Collections.reverse(similarities);
		
		double[] out = new double[similarities.size()]; // stupido java
		for (int i=0; i<out.length; i++)
			out[i] = similarities.get(i);
		return out;
	}
	
	/**
	 * Cosine Similarity: cosine(theta) = A . B / ||A|| ||B||
	 */
	public static double similarity(boolean[] v, boolean[] s) {
		return vectorProduct(v, s) / (norm(v) * norm(s));
	}
	
	/**
	 * Partendo da un articolo cerca numOfResults articoli ordinati per somiglianza, e conta quanti sono presenti nelle citazioni
	 * @return
	 */
	public static int evaluateRelevanceBasedOnReference(Article article, int numOfResults) {
		List<Article> list = getNeighbours(article.getKeyphrasesVector(), numOfResults);
		int common = 0;
		for (Article a : list)
			if (article.getCites().contains(a))
				common++;
		return common;
	}

	
	private static int indexOfMax(double[] a) {
		int index = -1;
		double max = -1;
		for (int i=0; i<a.length; i++)
			if (a[i] > max) {
				index = i;
				max = a[i];
			}
		return index;
	}
	/**
	 * Norma di un vettore booleano, e' semplificata rispetto alla norma normale
	 */
	private static double norm(boolean[] v) {
		int occurence = 0;
		for (boolean b : v)
			if (b)
				occurence++;
		return Math.sqrt(occurence);
	}
	/**
	 * Prodotto fra vettori booleani, e' semplificato rispetto al normale prodotto fra vettori
	 */
	private static double vectorProduct(boolean[] v1, boolean[] v2) {
		int c = 0;
		for (int i=0; i<v1.length; i++)
			if (v1[i] && v2[i])
				c++;
		return c;
	}
}
