package articlerecommender;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class Parsing extends DefaultHandler {
 
    private List<Articolo> articoli = new ArrayList<Articolo>();
    private Articolo articolo;
 
    boolean flagAnno = false;
    boolean flagNome = false;
    boolean flagAutore = false;
 
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {     
        if (qName.equalsIgnoreCase("year")) {
            flagAnno=true;
        }         
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("year")) {
            articoli.add(articolo);
        }
    }
 
    public void characters(char ch[], int start, int length) throws SAXException {         
        if (flagAnno) {
            String tempAnno = new String(ch, start, length); 
            if(tempAnno.equals("2008")){
                articolo = new Articolo();
                articolo.setAnno(tempAnno);                
            }   
            flagAnno=false;
        }
    }
    
    public List<Articolo> getArticoli(){
        return articoli;
    }
}
