package pcw.parsins;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pcw.utils.Article;

class ParsingStep2par2 extends DefaultHandler {

	private Article art = new Article();
	boolean flagNome = false;
	boolean flagAutore = false;
	boolean flagEe = false;
	boolean flagAnno = false;
	boolean flagType = false;
	boolean flagType2 = false;

	boolean fileTrovato = false;
	String titolo = "";
	int cont = 0;

	public ParsingStep2par2() throws IOException, ParserConfigurationException, SAXException {
		//saxParser.parse(new File("data/dblp2.xml"), handler);
		//System.out.println(handler.myCite());
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("dblp")) {
			fileTrovato = false;
		}
		if (!fileTrovato && (qName.equalsIgnoreCase("article") || flagType && qName.equalsIgnoreCase("inproceedings"))) {
			//System.out.println(cont++);
			art = new Article();
			flagType = true;
			flagType2 = true;
			art.setId(attributes.getValue(1));
		}

		if (flagType == true) {
			if (qName.equalsIgnoreCase("year")) {
				flagAnno = true;
			}
			if (qName.equalsIgnoreCase("title")) {
				flagNome = true;
			}
			if (qName.equalsIgnoreCase("author")) {
				flagAutore = true;
			}
			if (qName.equalsIgnoreCase("ee")) {
				flagEe = true;
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (flagType && (qName.equalsIgnoreCase("article") || flagType && qName.equalsIgnoreCase("inproceedings"))) {
			flagType = false;
			if (!fileTrovato) {
				art.setAuthor("");
				art.setYear("");
				art.setTitle("");
				art.setEe("");
				art.setType("");
				art.setId("");
			}
		}
	}

	public void characters(char ch[], int start, int length) throws SAXException {
		if (flagType2) {
			flagType2 = false;
			art.setType("article");
		}
		if (flagType && flagAnno) {
			flagAnno = false;
			art.setYear(new String(ch, start, length));
		}
		if (flagType && flagNome) {
			art.setTitle(new String(ch, start, length));

			Pattern pat = Pattern.compile("\\w");
			Matcher matcher1 = pat.matcher(art.getTitle());
			Matcher matcher2 = pat.matcher(titolo);
			String tit1 = "", tit2 = "";
			while (matcher1.find())
				tit1 = tit1 + matcher1.group();
			while (matcher2.find())
				tit2 = tit2 + matcher2.group();
			//if(titolo.equalsIgnoreCase(art.title))
			if (tit1.trim().equalsIgnoreCase(tit2.trim())) {
				fileTrovato = true;
			}
			/*else if((titolo.length()-art.title.length()) < 3)
			{
				fileTrovato=false;
			}*/
			else
				fileTrovato = false;
			flagNome = false;
		}
		if (flagType && flagAutore) {
			art.setAuthor(new String(ch, start, length));
			flagAutore = false;
		}
		if (flagType && flagEe) {
			art.setEe(new String(ch, start, length));
			flagEe = false;
		}
	}

	public Article myCite() {
		//System.out.println("Title :" + art.title + " ; Author :" + art.author);
		return art;
	}

	public Boolean trovato() {
		return fileTrovato;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
}