/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package articlerecommender;

/**
 *
 * @author Michael
 */
public class Articolo {
    
    public String anno;
    public String nome;
    public String autore;
    public String[] keyphrase;
    public String[] bibliografia;
    
    public Articolo(String anno, String nome, String autore){
        this.anno = anno;
        this.nome = nome;
        this.autore = autore;
    }
    
    public Articolo(){
        
    }
    
    public void setAnno(String anno){
        this.anno= anno;
    }
    
    public String getAnno(){
        return anno;
    }
}
