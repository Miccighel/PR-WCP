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
public class ACMParser {
    
    public String baseUrl;
    
    public ACMParser(String url){
        baseUrl = url + "&preflayout=flat";
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
            webPage = Jsoup.connect(baseUrl).userAgent("Chrome").timeout(50000).get();
        } catch (IOException e){
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
        parsedText = webPage.select("div[style="+"display:inline"+"]:first-child");
        
        for(Element tag : parsedText){
            finalText+=System.lineSeparator()+tag.text();
        }
        
        finalText = finalText.trim();
        
        return finalText;
    }
    
    public String[] getCitations(){
        
        Document webPage = new Document("");
        Elements parsedText;
        String[] text;
        
        try {
            webPage = Jsoup.connect(baseUrl).userAgent("Chrome").timeout(50000).get();
        } catch (IOException e){
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
        
        parsedText = webPage.select("p.abstract + table tbody tr td a[href]");
        
        text = new String[parsedText.size()];
        
        for(int i=0;i<text.length;i++){
            text[i] = parsedText.get(i).text();
            text[i] = text[i].trim();
        }
        
        return text;
    }
    
    public void setUrl(String url){
        this.baseUrl=url;
    }
}