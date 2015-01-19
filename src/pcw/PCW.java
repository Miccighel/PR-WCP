package pcw;

import java.util.List;

import pcw.module.Library;
import pcw.module.Recommendation;
import pcw.parsers.*;
import pcw.user.User;
import pcw.utils.Article;
import pcw.utils.ArticleSimilarity;
import pcw.utils.MetricUtils;

public class PCW {
	
	public static final int NUM_OF_RECOMMENDATIONS = 10;
	
	public static void main(String[] args) {
      //Library.getInstance().getArticleList().get(0);
      System.out.println("kp: "+Library.getInstance().getKeyphrases().size());
      System.out.println("articoli: "+Library.getInstance().getArticleList().size());
      //testEvaluation(Library.getInstance().getArticleList().get(4));
      
      int q = 0;
      for (Article a : Library.getInstance().getArticleList())
         if (a.getCites().size() == 0)
            q++;
      System.out.println("su " + Library.getInstance().getArticleList().size()+ " articoli, " +q+" non hanno citazioni");
      
      q = 0;
      for (Article a : Library.getInstance().getArticleList())
         if (a.getKeyphrases().size() == 1)
            q++;
      System.out.println("su " + Library.getInstance().getArticleList().size()+ " articoli, " +q+" hanno una sola keyphrase");
      q = 0;
      for (Article a : Library.getInstance().getArticleList())
         if (a.getKeyphrases().size() == 2)
            q++;
      System.out.println("su " + Library.getInstance().getArticleList().size()+ " articoli, " +q+" hanno 2 keyphrase");
      q = 0;
      for (Article a : Library.getInstance().getArticleList())
         if (a.getKeyphrases().size() == 3)
            q++;
      System.out.println("su " + Library.getInstance().getArticleList().size()+ " articoli, " +q+" hanno una 3 keyphrase");
      q = 0;
      for (Article a : Library.getInstance().getArticleList())
         if (a.getKeyphrases().size() == 4)
            q++;
      System.out.println("su " + Library.getInstance().getArticleList().size()+ " articoli, " +q+" hanno 4 keyphrase");
      q = 0;
      for (Article a : Library.getInstance().getArticleList())
         if (a.getKeyphrases().size() > 4)
            q++;
      System.out.println("su " + Library.getInstance().getArticleList().size()+ " articoli, " +q+" hanno 5 o più keyphrase");
      
      double u = 0;
      for (int i=0; i<Library.getInstance().getKeyphrasesMatrix().length; i++)
    	  for (int j=0; j<Library.getInstance().getKeyphrasesMatrix()[0].length; j++)
    		  if (Library.getInstance().getKeyphrasesMatrix()[i][j])
    			  u++;
      System.out.println("La matrice delle citazioni è " +Library.getInstance().getKeyphrasesMatrix().length + "x" + Library.getInstance().getKeyphrasesMatrix()[0].length + " e ha " + u +" 1, la densità è " +
    			  (u / (Library.getInstance().getKeyphrasesMatrix().length * Library.getInstance().getKeyphrasesMatrix()[0].length)) + "");
      
      q = 0;
      int max=0;
      int indexOfMax = 0;
      for (int i=0; i<Library.getInstance().getKeyphrasesMatrix().length; i++) {
    	  for (int j=0; j<Library.getInstance().getKeyphrasesMatrix()[0].length; j++)
    		  if (Library.getInstance().getKeyphrasesMatrix()[i][j])
    			  q++;
    	  if (q>max) {
    		  max = q;
    		  indexOfMax = i;
    	  }
    	  q=0;
      }
      System.out.println("La keyphrase più comune è " + Library.getInstance().getKeyphrases().get(indexOfMax) + " che compare in " + max + " articoli");
      
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
		List<ArticleSimilarity> recommended = Recommendation.getNeighbours(article, NUM_OF_RECOMMENDATIONS);
		double[] sortedSimilarity = new double[NUM_OF_RECOMMENDATIONS];
      for (int i=0; i<NUM_OF_RECOMMENDATIONS; i++)
         sortedSimilarity[i] = recommended.get(i).getSimilarity();
		
		System.out.println("Articolo in esame:\n\n" + article.toString() +"\n\n----------\n");
		
		System.out.println("dcg: "+MetricUtils.dcg(sortedSimilarity));
		System.out.println("ndcg: "+MetricUtils.ndcg(sortedSimilarity));
		System.out.println("precision: +"+MetricUtils.precision(article, recommended));
		System.out.println("recall: "+MetricUtils.recall(article, recommended));
		System.out.println("f-score: "+MetricUtils.fScore(article, recommended) + "\n\n");
		
		System.out.println("Articoli simili:\n\n");
		
		printResult(recommended);
	}
   
   public static void printResult(List<ArticleSimilarity> l) {
      for (int i=0; i<l.size(); i++)
			System.out.println("Posizione " + (i+1) + ", somiglianza: " + l.get(i).getSimilarity() + "\n"+l.get(i).getArticle().toString() + "\n\n");
   }
	

}