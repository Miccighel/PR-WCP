package pcw.user;

import java.util.HashSet;
import java.util.Set;

import pcw.module.Library;
import pcw.utils.Article;

public class Bibliography {
	
	private Set<Article> articlesSet;
	boolean[] keyphrasesVector;
	
	public Bibliography() {
		articlesSet = new HashSet<Article>();
		keyphrasesVector = new boolean[Library.getInstance().getKeyphrasesMatrix().length];
	}
	
	private void updateVector(boolean[] v) {
		for (int i=0; i<keyphrasesVector.length; i++)
			if (v[i])
				keyphrasesVector[i] = true;
	}
	
	public void addArticle(Article article) {
		articlesSet.add(article);
		updateVector(article.getKeyphrasesVector());
	}
	
	public void removeArticle(Article article) {
		articlesSet.remove(article);
		for (Article a : articlesSet)
			updateVector(a.getKeyphrasesVector());
	}

}
