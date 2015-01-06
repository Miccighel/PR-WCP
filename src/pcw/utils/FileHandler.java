package pcw.utils;

import java.io.*;

/**
 * Does all the operations to be done on a file.
 * @author Michael Soprano
 */
public class FileHandler {  
    /**
     * File to be handled.
     */
    public File file;
    /**
     * Handler of the read operations.
     */
    private BufferedReader reader;
    /**
     * Handler of the write operations.
     */
    private BufferedWriter writer;

    /**
     * Loads the file to be used.
     * @param nF File path.
     */
    public FileHandler(String nF) {
        file = new File(nF);
    }
    
    /**
     * Gives the file path.
     * @return File path.
     */
    public String getPath(){
        return file.getPath();
    }   
    
    /**
     * Gives the file name.
     * @return File name.
     */
    public String getName(){
        return file.getName();
    } 
    
    /**
     * Reads a line from the file.
     * @return A line of the file.
     * @throws IOException if the are troubles with the filesystem.
     */
    public String readLine() throws IOException {
        return reader.readLine();
    }
    
    /**
     * Creates the folders for the path specified in the constructor.
     * @return True if the folders have been made, false if not. 
     */
    public boolean makeDir(){
        return file.mkdirs();
    }
        
    /**
     * Write a line in the file.
     * @param s String to be written.
     * @throws IOException if the are troubles with the filesystem. 
     */
    public void writeLine(String s) throws IOException {
        writer.write(s);
    }
       
    /**
     * Checks if the file exists.
     * @return True if the file exists, false if not.
     */
    public boolean exists(){
        return file.exists();
    }

    /**
     * Opens the read handler.
     * @throws IOException if the are troubles with the filesystem. 
     */
    public void openReader() throws IOException {
        reader = new BufferedReader(new FileReader(file));	
    }
      
    /**
     * Opens the write handler in append mode.
     * @throws IOException if the are troubles with the filesystem. 
     */
    public void openWriterAppend() throws IOException {
        writer = new BufferedWriter(new FileWriter(file,true));
    }
    
    /**
     * Opens the write handler.
     * @throws IOException if the are troubles with the filesystem. 
     */
    public void openWriter() throws IOException {
        writer = new BufferedWriter(new FileWriter(file));
    }

    /**
     * Closes the read handler.
     * @throws IOException if the are troubles with the filesystem. 
     */
    public void closeReadingFlow() throws IOException {
        reader.close();
    }        
        
    /**
     * Closes the write handler.
     * @throws IOException if the are troubles with the filesystem. 
     */
    public void closeWritingFlow() throws IOException {
        writer.close();
    }
        
    /**
     * Gives the number of entries in the file.
     * @return Number of file entries.
     * @throws IOException if the are troubles with the filesystem. 
     */
    public int entries() throws IOException{           
            
        int counter=0;            
        openReader();
        
        reader.readLine();
            
        while(reader.readLine() != null) {
            counter++;
        }
        
        return counter;
    }
}
