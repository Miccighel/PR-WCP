package articlerecommender;

import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import java.net.URL;
  
class ArticleRecommender {
 
    public static void main(String[] args) throws Exception{
        
        URL url = new URL("http://link.springer.com/article/10.1007/s10639-014-9371-3");
        String text = DefaultExtractor.INSTANCE.getText(url);
        String[] split = text.split("\n");
        int i = 0;
        while(!split[i].equals("Abstract")){
            i++;
        }
        
        String text2 = ArticleExtractor.INSTANCE.getText(url);
        String[] split2 = text2.split("\n");
        
        i = 0;
        while(!split2[i].contains("References")){
            i++;
        }

        String cit = split2[4];
        String title = "";
        boolean flag = true;
        int h=0;
        while(cit.charAt(h)!=')' || cit.charAt(h+1)!='.'){
            h++;
        }
        
        for (int k=h+3;k<cit.length();k++) {
            if (cit.charAt(k)!=',' && flag) {
                title+=cit.charAt(k);
            }
            else if(flag==true){
                flag=false;
            }
            
        }
        
        System.out.println(title);
    }
 
}
