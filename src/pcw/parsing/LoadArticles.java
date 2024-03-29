package pcw.parsing;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import pcw.module.Library;
import pcw.utils.Article;

public class LoadArticles extends DefaultHandler {
	
	boolean flagTitle = false, flagAuthor = false, flagEe = false, flagYear = false, flagAbstract = false, flagKps = false, flagCites = false, flagKp = false, flagCite = false;
	Article article = new Article();
	List<Article> articlesList = new ArrayList<Article>();
	List<String> cites = new ArrayList<String>();
	List<String> keyphrases = new ArrayList<String>();
	private Library library;
	
	public LoadArticles(Library library) {
		this.library = library;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		switch (qName) {
			case "author": flagAuthor = true; break;
			case "title": flagTitle = true; break;
			case "year": flagYear = true; break;
			case "ee": flagEe = true; break;
			case "abstract": flagAbstract = true; break;
			case "keyphrases": flagKps = true; break;
			case "kph": flagKp = true; break;
			case "cites": flagCites = true; break;
			case "cite": flagCite = true; break;
		}
	}
	
	public void characters(char ch[], int start, int length) {
		if (flagAuthor) {
			article.setAuthor(new String(ch, start, length));
			flagAuthor = false;
		}
		else if (flagTitle) {
			article.setTitle(new String(ch, start, length));
			flagTitle = false;
		}
		else if (flagYear) {
			article.setYear(new String(ch, start, length));
			flagYear = false;
		}
		else if (flagEe) {
			article.setEe(new String(ch, start, length));
			flagEe = false;
		}
      else if (flagAbstract) {
         article.setAbstract(new String(ch, start, length));
         flagAbstract = false;
      }
		else if (flagKp) {
			article.addKeyphrase((new String(ch, start, length)).toLowerCase());
			flagKp = false;
		}
		else if (flagCite) {
			article.addCite(new String(ch, start, length));
			flagCite = false;
		}
	}
	
	public void endElement(String uri, String localName, String qName) {
		if (qName.equals("article")) {
			articlesList.add(article);
			article = new Article();
		}
		if (qName.equals("dataset"))
			this.library.setArticleList(articlesList);
	}

}
