package pcw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import pcw.parsers.*;
import pcw.parsins.*;
import pcw.utils.Article;

public class PCW {
	
	public static List<Article> articles;

	public static void main(String[] args) {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			//saxParser.parse(new File("data/dblp.xml"), new Parsing());
			saxParser.parse(new File("data/dblp2.xml"), new ParsingStep2());
			//saxParser.parse(new File("data/testLoad.xml"), new LoadArticles());
			saveArticles();
		}
		catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
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

	
	
	/**
	 * Salva gli articoli nel file data/articles.ser per non dover ricostruire gli articoli dall'xml
	 */
 	public static void saveArticles() {
 		try {
 			FileOutputStream fileOut = new FileOutputStream("data/articles.ser");
 			ObjectOutputStream out = new ObjectOutputStream(fileOut);
 			out.writeObject(articles);
 			out.close();
 			fileOut.close();
 		} catch (IOException e) {
 			e.printStackTrace();
 		}	
 	}
 	/**
 	 * Carica gli articoli serializzati nel file data/articles.ser
 	 */
 	@SuppressWarnings("unchecked")
 	public static void loadArticles() {
 		try {
 			FileInputStream fileIn = new FileInputStream("data/articles.ser");
 			ObjectInputStream in = new ObjectInputStream(fileIn);
 			articles = (ArrayList<Article>) in.readObject();
 			in.close();
 			fileIn.close();
 		} catch (IOException e) {
 	       articles = new ArrayList<Article>();
 	    } catch (ClassNotFoundException e) {
 	         e.printStackTrace();
 	    }
 	}
}