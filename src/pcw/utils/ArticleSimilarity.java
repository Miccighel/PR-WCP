/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pcw.utils;

/**
 *
 * @author s109478
 */
public class ArticleSimilarity {
   
   Article article;
   double similarity;
   
   public ArticleSimilarity(Article a, double sim) {
      this.article = a;
      this.similarity = sim;
   }
   
   public Article getArticle() {
      return this.article;
   }
   
   public double getSimilarity() {
      return this.similarity;
   }
   
}
