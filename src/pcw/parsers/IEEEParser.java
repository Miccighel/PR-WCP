package pcw.parsers;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Michael
 */
public class IEEEParser {
    
    public String abstractUrl;
    public String citationsUrl;
    
    public IEEEParser(String url){
        this.abstractUrl=url;
        citationsUrl = abstractUrl.replace("articleDetails", "abstractCitations");
    }
    
    /**
     * Prendere tutto il testo dei tag html individuati dal selettore.
     * @param selector selettore da cui estrarre il testo.
     * @return testo estratto.
     */
    public String getAbstract(){
        
        Document webPage = new Document("");
        Elements parsedText;
        String finalText="";
        
        try {
            webPage = Jsoup.connect(abstractUrl).userAgent("Chrome").timeout(50000).get();
        } catch (IOException e){
            System.out.println("Invalid URL");
        } catch (IllegalArgumentException e){
            System.out.println("Invalid URL");
        }
        parsedText = webPage.select(".article p");
        
        for(Element tag : parsedText){
            finalText+=System.lineSeparator()+tag.ownText();
        }
        
        finalText = finalText.trim();
        
        return finalText;
    }
    
    public String[] getCitations(){
        
        Document webPage = new Document("");
        Elements parsedText;
        String[] text;
        
        try {
            webPage = Jsoup.connect(citationsUrl).userAgent("Chrome").timeout(50000).get();
        } catch (IOException e){
            System.out.println("Invalid URL");
        } catch (IllegalArgumentException e){
            System.out.println("Invalid URL");
        }
        
        parsedText = webPage.select("div#abstractCitations ol#Ieee_citations");
        
        text = new String[parsedText.size()];
        
        for(int i=0;i<text.length;i++){
            text[i] = parsedText.get(i).text();
            text[i] = text[i].trim();
        }
        
        return text;
    }
    
    public void setUrl(String url){
        this.abstractUrl=url;
    }
}