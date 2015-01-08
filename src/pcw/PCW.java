package pcw;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import pcw.parsers.*;
import pcw.parsins.*;

public class PCW {
	
	public static void main(String[] args) {
		
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			saxParser.parse(new File("data/dblp.xml"), new Parsing());
			saxParser.parse(new File("data/dblp2.xml"), new ParsingStep2());
		}
		catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		
		
		//library = new Library();
		
		//MetricUtils.demo();
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

	
	

}