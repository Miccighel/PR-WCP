package pcw.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Dado
 */
public class Extractor {

    // Extractor Service index
    private static String targetURL = "http://158.110.145.61:8080/ExtractorService/";
    // Parameters for the extraction
    private static String urlParameters = "extract?text=\"";
    
    /* 
     * Serve se si volesse processare da un file
     * 
    public static String preprocessText(String filePath){
        String text="";
        String line;        
        FileHandler handler = new FileHandler("data/prova.txt");
        try{
            handler.openReader();
            while((line= handler.readLine()) != null){
                text += line;
            }
            handler.closeReadingFlow();
        } catch(IOException ecc){
            System.out.println("File not found");
        }        
        return text.replace(" ", "%20");
    }*/
    
    public static String[] keyphrasesToArray(HashMap keyphrasesExtracted){
        
        Iterator iterator =  keyphrasesExtracted.entrySet().iterator();
        String[] keyphraseArray = new String[keyphrasesExtracted.entrySet().size()];
        Map.Entry<String,Double> keyphrase;
        int i=0;
        
        while(iterator.hasNext()){
            keyphrase = (Map.Entry<String,Double>)iterator.next();
            keyphraseArray[i] = keyphrase.getKey();
            i++;
        }
        
        return keyphraseArray;
    }
    

    // connection to the amazing Extractor Service
    public static HashMap<String, Double> extract(String text) {
        //System.out.print(text + "\n\n");
        String currentPar = urlParameters + text + "\"";
        // JSON parser
        JSONParser parser = new JSONParser();
        HashMap<String, Double> output = new HashMap<>();
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
        	// Risolto il problema mettendo al posto degli " " "%20"
        	url = new URL (targetURL + currentPar.replace(" ", "%20"));
            HttpURLConnection request1 = (HttpURLConnection) url.openConnection();
            request1.setRequestMethod("GET");
            request1.connect();
            InputStream is = request1.getInputStream();

            // Get Response
            // looks like this:
            /*
             {"result":{
             extracted":[
             {"relevance":0.5753968253968254,"keyphrase":"request"},
             {"relevance":0.3296703296703297,"keyphrase":"HTTP Server"},
             {"relevance":0.315934065934066,"keyphrase":"Server Monitor"},
             {"relevance":0.26098901098901106,"keyphrase":"replay HTTP requests"}
             ]}
             } 
             */
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            //System.out.println(reader.toString());
            Object obj = parser.parse(reader);
            JSONObject queryblock = (JSONObject) obj;
            JSONObject pagesBlock = (JSONObject) queryblock.get("result");
            JSONArray keyList = (JSONArray) pagesBlock.get("extracted");

            if (keyList != null) {
                Iterator<JSONObject> iterator = keyList.iterator();
                while (iterator.hasNext()) {
                    JSONObject key = (iterator.next());
                    String keyphrase = (String) key.get("keyphrase");
                    Double value = (Double) key.get("relevance");
                    output.put(keyphrase, value);
                }
            }
            return output;

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}