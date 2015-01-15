package pcw.user;

import pcw.module.Library;
import pcw.utils.Article;

public class User {

	private Bibliography bibliography;
	/**
	 * 0 nessun feedback
	 * 1 non utile
	 * 2 utile
	 * 3 in bilbiografia
	 */
	private byte[] feedback;
	
	public User() {
		this.bibliography = new Bibliography();
		this.feedback = new byte[Library.getInstance().getArticleList().size()];
	}
	
	/**
	 * setta il feedback di un certo articolo a un certo rating
	 * 0 nessun feedback
	 * 1 non utile
	 * 2 utile
	 * 3 in bilbiografia
	 */
	public void addFeedback(Article article, int rating) {
		this.feedback[Library.getInstance().getIndexFromArticle(article)] = (byte) rating;
	}
	
	public Bibliography getBibliography() {
		return this.bibliography;
	}
	
}
