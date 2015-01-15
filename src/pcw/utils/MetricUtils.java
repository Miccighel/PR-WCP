package pcw.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @author manolisa
 * http://code.google.com/p/pubsearch/source/browse/trunk/src/ps/tests/MetricUtils.java?r=7
 * Provides utility functionality for the different ranking evaluation metrics.
 */
public class MetricUtils {
        
        // the max grade
        private static double G_MAX = 5;

        public static void demo() {
                // Rank:
                //Double[] ranking = new Double[] { 3d, 2d, 3d, 0d, 1d, 2d };
        		double[] ranking = new double[] { 0d, 1d, 2d, 3d };

                // LEXICOGRAPHIC ORDERING:
                double lexScore = lex(ranking);
                System.out.println("LEX = " + lexScore);
                
                // DCG TEST:
                double dcgScore = dcg(ranking);
                System.out.println("DCG = " + dcgScore);

                // NDCG TEST:
                double ndcgScore = ndcg(ranking);
                System.out.println("NDCG = " + ndcgScore);
        }

        public static double lex(double[] orgRanking) {
                double[] ranking = new double[orgRanking.length];
                for(int i = 0 ; i < orgRanking.length ; i++){
                        ranking[i] = orgRanking[i]; 
                }
                double feedbackScore = 0;
                for (int i = 9; i >= 0; i--) {
                        int idx = 9 - i;
                        double currScore = Math.pow(2, i) * ranking[idx];
                        feedbackScore += currScore;
                        if (idx == ranking.length - 1) {
                                return feedbackScore;
                        }
                }
                return feedbackScore;
        }
        
        /**
         * Calculates the DCG (Discounted cumulative gain) score for the specified ranking evaluation.
         * 
         * @param ranking
         *            the ranking evaluation
         * @return the DCG score for the ranking
         */
        public static double dcg(double[] ranking) {
                double score = 0;
                for (int i = 1; i < ranking.length; i++) {
                        score += calcDcgForPos(ranking[i], i + 1);
                }
                return ranking[0] + score;
        }

        /**
         * Calculates the DCG (Discounted cumulative gain) score for the specified evaluation score at the specified rank
         * position.
         * 
         * @param score
         *            the evaluation score
         * @param rank
         *            the rank position
         * @return the DCG score
         */
        private static double calcDcgForPos(double score, int rank) {
                double log2Pos = Math.log(rank) / Math.log(2);
                return score / log2Pos;
        }

        /**
         * Calculates the NDCG (Normalized discounted cumulative gain) score for the specified ranking evaluation.
         * 
         * @param ranking
         *            the ranking evaluation
         * @return the NDCG score for the ranking
         */
        public static double ndcg(double[] orgRanking) {
                double[] ranking = new double[orgRanking.length];
                for(int i = 0 ; i < orgRanking.length ; i++){
                        ranking[i] = orgRanking[i]; 
                }
                return dcg(ranking) / dcg(reverseSortDesc(ranking));
        }

        /**
         * Reverse sorts the specified array in descending order based on the evaluation value.
         * 
         * @param ranking
         *            the ranking evaluation
         * @return the sorted array
         */
        public static double[] reverseSortDesc(double[] ranking) {
                double[] reverseSorted = new double[ranking.length];
                Arrays.sort(ranking);
                int arrLen = ranking.length;
                for (int i = arrLen - 1; i >= 0; i--) {
                        int pos = arrLen - 1 - i;
                        reverseSorted[pos] = ranking[i];
                }
                return reverseSorted;
        }
        
        /**
         * Calculates the ERR (Expected Reciprocal Rank).
         * 
         * @param ranking
         *            the ranking evaluation
         * @return the ERR score
         */
        public static double err(double[] orgRanking) {
                Double[] ranking = new Double[orgRanking.length];
                for(int i = 0 ; i < orgRanking.length ; i++){
                        ranking[i] = orgRanking[i]; 
                }
                double p = 1;
                double errScore = 0;
                int n = ranking.length;
                for (int r = 1; r <= n; r++) {
                        double g = ranking[r - 1];
                        double rg = (Math.pow(2, g) - 1) / Math.pow(2, G_MAX);
                        errScore += p * (rg / r);
                        p = p * (1 - rg);
                }
                return errScore;
        }

        
        /**
         * precision = | {relevant documents} intersecato {retrieved documents} |  /  | {retrieved documents} |
         * @return
         */
        public static double precision(Article article, List<Article> retrievedDocuments) {
        	int relevantDocumentsFound = 0;
        	for (Article a : retrievedDocuments)
        		if (article.getCites().contains(a));
        			relevantDocumentsFound++;
        	return relevantDocumentsFound / retrievedDocuments.size();
        }
        
        /**
         * recall = | {relevant documents} intersecato {retrieved documents} |  /  | {relevan documents} |
         * @return
         */
        public static double recall(Article article, List<Article> retrievedDocuments) {
        	int relevantDocumentsFound = 0;
        	for (Article a : retrievedDocuments)
        		if (article.getCites().contains(a));
        			relevantDocumentsFound++;
        	return article.getCitesStringList().size() != 0 ? relevantDocumentsFound / article.getCitesStringList().size() : 0;
        }
        
        /**
         * F-score is a measure of a test's accuracy. It considers both the precision p and the recall r of the test to compute the score. 
         * Can be interpreted as a weighted average of the precision and recall.
         * @return a number between 0 and 1, where 1 is best and 0 is worst
         */
        public static double fScore(Article article, List<Article> retrievedDocuments) {
        	double precision = precision(article, retrievedDocuments);
        	double recall = recall(article, retrievedDocuments);
        	return 2 * (precision * recall) / (precision + recall);
        }
}