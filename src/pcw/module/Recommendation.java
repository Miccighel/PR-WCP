package pcw.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pcw.user.Bibliography;

import pcw.utils.Article;
import pcw.utils.ArticleSimilarity;

public class Recommendation {
	
   
   	/**
	 * Restituisce gli articoli piu' simili a quelli passati in ingresso ordinati per somiglianza, rimuovendo
    * quelli gia' presenti nella bibliografia
	 */
	public static List<ArticleSimilarity> getNeighbours(Bibliography bibliography, int numOfNeighbours) {
		double[] similarities = new double[Library.getInstance().getArticleList().size()];
		
		for (int i=0; i<Library.getInstance().getKeyphrasesMatrix().length; i++)
			similarities[i] = similarity(bibliography.getVector(), Library.getInstance().getKeyphrasesMatrix()[i]);
		
		List<ArticleSimilarity> out = new ArrayList<ArticleSimilarity>();
		for (int i=0; i<numOfNeighbours; i++) {
			int index = indexOfMax(similarities);
         Article article = Library.getInstance().getArticleList().get(index);
         if (!bibliography.getArticles().contains(article))
            out.add(new ArticleSimilarity(article, similarities[index]));
			similarities[index] = -1;
		}
		
		return out;
	}
	/**
	 * Restituisce gli articoli piu' simili a quelli passati in ingresso ordinati per somiglianza
	 */
	public static List<ArticleSimilarity> getNeighbours(boolean[] userVector, int numOfNeighbours) {
		double[] similarities = new double[userVector.length];
		
		for (int i=0; i<Library.getInstance().getKeyphrasesMatrix().length; i++)
			similarities[i] = similarity(userVector, Library.getInstance().getKeyphrasesMatrix()[i]);
		
		List<ArticleSimilarity> out = new ArrayList<ArticleSimilarity>();
		for (int i=0; i<numOfNeighbours; i++) {
			int index = indexOfMax(similarities);
         Article article = Library.getInstance().getArticleList().get(index);
         out.add(new ArticleSimilarity(article, similarities[index]));
			similarities[index] = -1;
		}
		
		return out;
	}
	/**
	 * Restituisce gli articoli piu' simili a quelli passati in ingresso ordinati per somiglianza
	 */
	public static List<ArticleSimilarity> getNeighbours(Article article, int numOfNeighbours) {
		double[] similarities = new double[article.getKeyphrasesVector().length];
		
		for (int i=0; i<Library.getInstance().getKeyphrasesMatrix().length; i++)
			similarities[i] = similarity(article.getKeyphrasesVector(), Library.getInstance().getKeyphrasesMatrix()[i]);
		
		similarities[Library.getInstance().getIndexFromArticle(article)] = -2; // evita che dia in output se stesso
		
		List<ArticleSimilarity> out = new ArrayList<ArticleSimilarity>();
		for (int i=0; i<numOfNeighbours; i++) {
			int index = indexOfMax(similarities);
         Article a = Library.getInstance().getArticleList().get(index);
         out.add(new ArticleSimilarity(a, similarities[index]));
			similarities[index] = -1;
		}
		
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
		List<ArticleSimilarity> list = getNeighbours(article.getKeyphrasesVector(), numOfResults);
		int common = 0;
		for (ArticleSimilarity a : list)
			if (article.getCites().contains(a.getArticle()))
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
