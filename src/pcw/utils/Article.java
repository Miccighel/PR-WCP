package pcw.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Article implements Serializable {

	private static final long serialVersionUID = 2751605655130789015L;
	private String author, title, year, ee, abs, type, id;
	private List<String> cites, keyphrases;
	private boolean[] keyphrasesVector;
	
	public Article() {
		author=""; title=""; year=""; ee=""; abs = ""; type=""; id="";
		cites = new ArrayList<String>();
		keyphrases = new ArrayList<String>();
	}
	
	public void setType(String type){
	    this.type= type;
	}
	public void setAuthor(String author){
	    this.author= author;
	}
	public void setTitle(String title){
	    this.title= title;
	}
	public void setYear(String year){
	    this.year= year;
	}
	public void setEe(String ee){
	    this.ee= ee;
	}
	public void setAbstract(String abs) {
		this.abs = abs;
	}
	public void addKeyphrase(String keyphrase) {
		this.keyphrases.add(keyphrase);
	}
	public void addCite(String cite) {
		this.cites.add(cite);
	}
	public void setId(String i) {
		this.id = i;
	}
	public void setKeyphrasesVector(boolean[] v) {
		this.keyphrasesVector = v;
	}
	
	public String getType(){
	    return type;
	}
	public String getAuthor(){
	    return author;
	}
	public String getTitle(){
	    return title;
	}
	public String getYear(){
	    return year;
	}
	public String getEe(){
	    return ee;
	}
	public String getAbstract() {
		return abs;
	}
	public List<String> getKeyphrases() {
		return keyphrases;
	}
	public List<String> getCites() {
		return cites;
	}
	public String getId() {
		return id;
	}
	public boolean[] getKeyphrasesVector() {
		return this.keyphrasesVector;
	}
	
	@Override
	public String toString() {
		String out = "Author: " + author + "\nTitle: " + title + "\nYear: " + year + "\nLink: " + ee + "\nAbstract: " + abs + "\nKeyphrases:";
		for (String s : keyphrases)
			out += "\n- " + s;
		out += "\nCites:";
		for (String s : cites)
			out += "\n- " + s;
		return out + "\nType: "+type+"\nID "+id;
	}
}