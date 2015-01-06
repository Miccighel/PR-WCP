package pcw.parsins;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pcw.utils.Extractor;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.DefaultExtractor;

public class Parsing extends DefaultHandler {

	XMLOutputFactory factory = XMLOutputFactory.newInstance();
	XMLStreamWriter writer;

	HashMap keyphrasesExtracted;
	String[] keyphrases;

	boolean flagAnno = false;
	boolean flagArticolo = false;
	boolean flagInproc = false;
	boolean flagNome = false;
	boolean flagAutore = false;
	boolean flagEe = false;
	boolean flagElement = false;

	private String nome = "", autore = "", anno = "", ee = "", abstrac = "", kps[], cites[];
	private String idArticle = "";
	int artCont = 0;
	int artContMax = 20;
	int badAbstract = 0;
	int badReferences = 0;
	int badUri = 0;
	int numCiteTot = 0;

	//conto le citazioni
	int citeGood = 0;
	int citeTot = 0;
	// conto quanti inproc e art ci sono
	int numArt = 0;
	int numInproc = 0;

	// Caratteri minimi per identificare un'abstract
	final int maxChar = 200;

	//Lista id dei articoli inseriti, usata per non inserire piu volte stesso articolo
	List<String> idArt = new ArrayList<String>();
	boolean idBool = false;

	public Parsing() throws IOException {
		this.idArt = idArt;
		try {
			writer = factory.createXMLStreamWriter(new FileOutputStream("data/dblp2.xml"), "UTF-8");
			writer.writeStartDocument();
			writer.writeStartElement("dblp");
		}
		catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	//tira fuori e salva articoli di un certo anno
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (artCont < artContMax) {
			if (qName.equalsIgnoreCase("inproceedings"))
				flagInproc = true;
			else if (qName.equalsIgnoreCase("article"))
				flagArticolo = true;
		}
		if (qName.equalsIgnoreCase("inproceedings") || qName.equalsIgnoreCase("article")) {
			idArticle = attributes.getValue(1);
		}

		if (flagArticolo || flagInproc) {
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

	public void characters(char ch[], int start, int length) throws SAXException {
		if ((flagArticolo || flagInproc) && flagAnno) {
			flagAnno = false;
			anno = new String(ch, start, length);
		}
		if ((flagArticolo || flagInproc) && flagNome) {
			nome = new String(ch, start, length);
			flagNome = false;
		}
		if ((flagArticolo || flagInproc) && flagAutore) {
			autore = new String(ch, start, length);
			flagAutore = false;
		}
		if ((flagArticolo || flagInproc) && flagEe) {
			ee = new String(ch, start, length);
			flagEe = false;
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("inproceedings") || qName.equalsIgnoreCase("article")) {
			if (anno.equals("2014") && ee.startsWith("http://dx.doi.org/10.1007")
			/*&& ee.startsWith("http://dx.doi.org/10.1109") &&
			ee.startsWith("http://www.online-journals.org") && 
			ee.startsWith("http://www.jtaer.com/")&& 
			ee.startsWith("http://doi.acm.org")*/) {
				try {
					writeFile();
				}
				catch (XMLStreamException e) {
					e.printStackTrace();
				}
			}
			flagArticolo = false;
			flagInproc = false;
			idBool = false;
			anno = "";
			nome = "";
			autore = "";
			ee = "";
			abstrac = "";
			idArticle = "";
		}
		if (qName.equalsIgnoreCase("dblp") /* || artCont >= artContMax*/) {
			try {
				writer.writeEndDocument();
				writer.close();
				System.out.println("Citazioni buone totali: " + citeGood);
				System.out.println("Citazioni totali: " + citeTot);
				System.out.println("Art tot: " + numArt);
				System.out.println("Inproc tot: " + numInproc);
				//throw new DoneParsingException();
			}
			catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeFile() throws XMLStreamException {
		boolean canWrite = false;
		boolean goodUri = true;
		try {
			canWrite = getFields(ee);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (BoilerpipeProcessingException e) {
			goodUri = false;
			badUri++;
		}
		//controllo articolo gia presente
		if (idArt != null) {
			for (String iList : idArt) {
				if (iList.trim().equalsIgnoreCase(idArticle.trim())) {
					idBool = true;
				}
			}
		}
		if (canWrite && !idBool) {
			idArt.add(idArticle);
			writer.writeStartElement("article");
			writer.writeStartElement("author");
			writer.writeCharacters(autore);
			writer.writeEndElement();
			writer.writeStartElement("title");
			writer.writeCharacters(nome);
			writer.writeEndElement();
			writer.writeStartElement("year");
			writer.writeCharacters(anno);
			writer.writeEndElement();
			writer.writeStartElement("ee");
			writer.writeCharacters(ee);
			writer.writeEndElement();
			writer.writeStartElement("abstract");
			writer.writeCharacters(abstrac);
			writer.writeEndElement();
			writer.writeStartElement("keyphrases");
			for (int j = 0; j < kps.length; j++) {
				if (!kps[j].equals("")) {
					writer.writeStartElement("kph");
					writer.writeCharacters(kps[j]);
					writer.writeEndElement();
				}
			}
			writer.writeEndElement();
			writer.writeStartElement("cites");
			for (int j = 0; j < numCiteTot; j++) {
				if (!cites[j].equals("")) {
					writer.writeStartElement("cite");
					writer.writeCharacters(cites[j]);
					writer.writeEndElement();
				}
			}
			writer.writeEndElement();
			writer.writeEndElement();
			artCont++;
			if (flagArticolo)
				numArt++;
			else
				numInproc++;
		}
		else if (goodUri)
			badAbstract++;
	}

	public boolean getFields(String link) throws BoilerpipeProcessingException, MalformedURLException {

		URL url = new URL(link);
		String text = DefaultExtractor.INSTANCE.getText(url);
		String[] split = text.split("\n");
		int i = 0;
		boolean flagAbstract = false;
		boolean flagPage = false;
		// Inizio acquisizione Abstract
		while (i < split.length && !flagAbstract && !flagPage) {
			if (split[i].matches("Abstract.*"))
				flagAbstract = true;
			else if (split[i].matches("Page.*"))
				flagPage = true;
			i++;
		}

		/* Prende l'Abstract se trova la sezione "Abstract" oppure se prima della sezione "Page" trova un testo lungo
		 almeno 200 caratteri.*/

		if (flagAbstract)
			abstrac += split[i];
		else if (flagPage && split[i - 2].length() >= maxChar)
			abstrac += split[i - 2];
		else
			return false;
		// Fine acquisizione Abstract

		// Inizio acquisizione Keyphrases
		keyphrasesExtracted = Extractor.extract(abstrac);
		keyphrases = Extractor.keyphrasesToArray(keyphrasesExtracted);

		kps = new String[10];
		for (int j = 0; j < kps.length; j++)
			kps[j] = "";

		if (keyphrases.length <= 10) {
			for (int k = 0; k < keyphrases.length; k++)
				kps[k] = keyphrases[k];
		}
		// Fine acquisizione Keyphrases

		// Inizio estrazione Refences
		i = 0;
		while (i < split.length && !split[i].matches("References.*")) {
			i++;
		}
		numCiteTot = 0;
		if (i >= split.length) {
			badReferences++;
			return false;
		}

		// estraggo quante citazioni ha l'articolo
		String numRef = split[i].substring(12, split[i].length() - 1);
		numCiteTot = Integer.parseInt(numRef);
		int numCiteGood = 0;

		// inizializzo il vettore per le citazioni
		i++; // pos della prima citazione
		cites = new String[numCiteTot];
		for (int j = 0; j < numCiteTot; j++)
			cites[j] = "";
		String dirtyCite = "";
		int charPos = 0;
		int startTitle = 0;

		// Questo per estrarre articoli da springer
		if (flagArticolo) {
			boolean firstBracket = true;
			while (i < split.length && !split[i].matches("About this Article.*")) {
				dirtyCite = split[i];
				while (charPos < dirtyCite.length() && dirtyCite.charAt(charPos) != '.') {
					if (dirtyCite.charAt(charPos) == ')' && firstBracket) {
						startTitle = charPos + 2;
						firstBracket = false;
					}
					charPos++;
				}
				if (startTitle != 0 && charPos < dirtyCite.length()) {
					cites[numCiteGood] = dirtyCite.substring(startTitle, charPos + 1);
					numCiteGood++;
				}
				firstBracket = true;
				charPos = 0;
				startTitle = 0;
				i++;
			}
		}
		else if (flagInproc) {
			boolean dotAfter = false;
			boolean dots = false;
			while (i < split.length && !split[i].matches("About this Article.*")) {
				dirtyCite = split[i];
				while (charPos < dirtyCite.length() && !dotAfter) {
					if (dirtyCite.charAt(charPos) == ':') {
						startTitle = charPos + 2;
						dots = true;
					}
					else if (dirtyCite.charAt(charPos) == '.' && dots) {
						dotAfter = true;
					}
					charPos++;
				}
				if (startTitle != 0 && charPos < dirtyCite.length()) {
					cites[numCiteGood] = dirtyCite.substring(startTitle, charPos + 1);
					numCiteGood++;
				}
				dots = false;
				dotAfter = false;
				charPos = 0;
				startTitle = 0;
				i++;
			}
		}

		citeTot += numCiteTot;
		citeGood += numCiteGood;
		// Fine estrazione References
		return true;
	}

	/*public class DoneParsingException extends SAXException
	{
		private static final long serialVersionUID = 1L;
	}*/

	public List<String> getIdList() {
		return idArt;
	}
}