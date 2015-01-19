package pcw;

import java.util.List;

import pcw.module.Library;
import pcw.module.Recommendation;
import pcw.parsers.*;
import pcw.utils.Article;
import pcw.utils.MetricUtils;

public class PCW {
	
	public static final int NUM_OF_RECCOMANDATIONS = 10;
	
	public static void main(String[] args) {
		testEvaluation(Library.getInstance().getArticleList().get(1));
	}

	public void demo() {
		System.out.println("ESEMPIO IEEE");
		IEEEParser parser = new IEEEParser(
				"http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=6736752");
		System.out.println("INIZIO ABSTRACT");
		String abstrac = parser.getAbstract();
		System.out.println(abstrac);
		System.out.println("FINE ABSTRACT");
		System.out.println("INIZIO CITAZIONI (VETTORE)");
		String[] citationsArray = parser.getCitations();
		for (String citation : citationsArray)
			if (citation.length() == 0)
				System.out.println("Questo articolo non ha citazioni.");
			else
				System.out.println(citation);
		System.out.println("FINE CITAZIONI (VETTORE)");
		System.out.println("ESEMPIO ACM");
		ACMParser parser2 = new ACMParser(
				"http://dl.acm.org/citation.cfm?id=1691511&CFID=466656782&CFTOKEN=12379839");
		System.out.println("INIZIO ABSTRACT");
		String abstrac2 = parser2.getAbstract();
		System.out.println(abstrac2);
		System.out.println("FINE ABSTRACT");
		System.out.println("INIZIO CITAZIONI (VETTORE)");
		String[] citationsArray2 = parser2.getCitations();
		for (String citation2 : citationsArray2)
			System.out.println(citation2);
		System.out.println("FINE CITAZIONI (VETTORE)");
	}

	public static void testEvaluation(Article article) {
		List<Article> reccomended = Recommendation.getArticleNeighbours(article, NUM_OF_RECCOMANDATIONS);
		double[] sortedSimilarity = Recommendation.getArticleSortedNeighboursSimilarity(article, NUM_OF_RECCOMANDATIONS);
		
		System.out.println("Articolo in esame:\n\n" + article.toString() +"\n\n----------\n");
		
		System.out.println("dcg: "+MetricUtils.dcg(sortedSimilarity));
		System.out.println("ndcg: "+MetricUtils.ndcg(sortedSimilarity));
		System.out.println("precision: +"+MetricUtils.precision(article, reccomended));
		System.out.println("recall: "+MetricUtils.recall(article, reccomended));
		System.out.println("f-score: "+MetricUtils.fScore(article, reccomended) + "\n\n");
		
		System.out.println("Articoli simili:\n\n");
		
		for (int i=0; i<reccomended.size(); i++)
			System.out.println("Posizione " + (i+1) + ", somiglianza: " + sortedSimilarity[i] + "\n"+reccomended.get(i).toString() + "\n\n");
	}
	

}