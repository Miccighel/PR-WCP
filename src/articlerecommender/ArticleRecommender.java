package articlerecommender;

import java.io.File;
import java.io.IOException;
import java.util.List; 
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory; 
import org.xml.sax.SAXException;
  
class ArticleRecommender {
 
    public static void main(String[] args) {
        
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        
        try {
            
            SAXParser saxParser = saxParserFactory.newSAXParser();
            Parsing handler = new Parsing();
            saxParser.parse(new File("data/test.xml"), handler);
            
            List<Articolo> articoli = handler.getArticoli();
            
            for(Articolo articolo : articoli){
                System.out.println(articolo.getAnno());
            }
        
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.getMessage();
        }
    }
 
}
