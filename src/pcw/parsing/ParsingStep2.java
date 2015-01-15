package pcw.parsing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pcw.utils.Article;

public class ParsingStep2 extends DefaultHandler {

	XMLOutputFactory factory = XMLOutputFactory.newInstance();
	XMLStreamWriter writer;

	private ArrayList<Article> artL = new ArrayList<Article>();
	private Article art = new Article();
	private String nome = "", autore = "", ee = "", keywords = "", cite = "";
	private String titolo = "";
	boolean flagCite = false;

	int contSi = 0, contNo = 0, contArtPres = 0;

	//Lista id dei articoli inseriti, usata per non inserire piu volte stesso articolo
	List<String> idArt;
	boolean idBool = false;

	//parser
	SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	SAXParser saxParser = saxParserFactory.newSAXParser();
	ParsingStep2par2 handler = new ParsingStep2par2();

	public ParsingStep2() throws IOException, ParserConfigurationException, SAXException {

		try {
			writer = factory.createXMLStreamWriter(new FileOutputStream("data/dblp3.xml"), "utf-8");

			writer.writeStartDocument();
		}
		catch (XMLStreamException e) {
			e.printStackTrace();
		}

		//saxParser.parse(new File("data/dblp2.xml"), handler);
		//System.out.println(handler.myCite());
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("cite")) {
			flagCite = true;
			//cont++;
		}
		if (!qName.equalsIgnoreCase("cite")) {
			try {
				writer.writeStartElement(qName);
			}
			catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (flagCite) {
			flagCite = false;
		}
		if (!qName.equalsIgnoreCase("cite")) {
			try {
				writer.writeEndElement();
			}
			catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (qName.equalsIgnoreCase("article") || qName.equalsIgnoreCase("inproceedings")) {
			System.out.println("Fine delle citazioni dell'articolo, provvedo alla scrittura sull'xml...");
			try {
				writeFile();
			}
			catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (qName.equalsIgnoreCase("dblp")) {
			try {
				writer.writeEndDocument();
				writer.flush();
				writer.close();
				System.out.println("Art cit tot presenti: " + contSi);
				System.out.println("Art cit tot non presenti: " + contNo);
				System.out.println("Art cit tot gia presenti: " + contArtPres);
			}
			catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void characters(char ch[], int start, int length) throws SAXException {
		if (flagCite) {
			titolo = new String(ch, start, length);
			handler.setTitolo(titolo);
			//cerca la cite sul altro parser
			try {
				saxParser.parse(new File("data/dblp.xml"), handler);
				if (handler.trovato())
					System.out.print(++contSi + " articoli trovati, ");
				else
					System.out.println(++contNo + " articoli NON trovati");
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (handler.trovato())//se c'è
			{
				try {
					writer.writeStartElement("cite");
					writer.writeCharacters(titolo);
					writer.writeEndElement();

					art = handler.myCite();
					idBool = false;
					if (idArt == null) {
						idArt = new ArrayList<String>();
					}
					for (String iList : idArt) {
						if (iList.trim().equalsIgnoreCase(art.getId().trim())) {
							idBool = true;
							System.out.println("l'articolo " + art.getTitle() + " era già presente, articoli presenti: " + ++contArtPres);
							
						}
					}
					if (!idBool) {
						idArt.add(new String(art.getId()));
						artL.add(art);
						System.out.println("l'articolo " + art.getTitle() + " _NON_ era presente, con questo siamo a " + artL.size());
					}
				}
				catch (XMLStreamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else {
			try {
				writer.writeCharacters(new String(ch, start, length));
			}
			catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	//write all the article from dble using the cite of an article
	//the article are all in the list artL
	private void writeFile() throws XMLStreamException {
		System.out.println("Sto scrivendo sul file " + artL.size() + " articoli:");
		for (Article a : artL)
			System.out.println("- " + a.getTitle());
		
		Iterator<Article> it = artL.iterator();
		while (it.hasNext()) {
			Article obj = it.next();
			//Do something with obj
			writer.writeStartElement(obj.getType());
			writer.writeStartElement("author");
			writer.writeCharacters(obj.getAuthor());
			writer.writeEndElement();
			writer.writeStartElement("title");
			writer.writeCharacters(obj.getTitle());
			writer.writeEndElement();
			writer.writeStartElement("year");
			writer.writeCharacters(obj.getYear());
			writer.writeEndElement();
			writer.writeStartElement("ee");
			writer.writeCharacters(obj.getEe());
			writer.writeEndElement();
			writer.writeEndElement();
		}
		artL.clear();
	}

	public void setIdList(List<String> idArt) {
		this.idArt = idArt;
	}
}