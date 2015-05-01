package com.mycompany.secondyearprogrammingproject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.servlet.SessionTrackingMode.URL;

/**
 * The DocTemplate servlet,which sets and returns the HTML document
 * 
 * @author Benjamin Williams <eeu222@bangor.ac.uk>
 * @author Jason Hall <eeu23e@bangor.ac.uk>
 * @author Liam Chapman <eeu239@bangor.ac.uk>
 * @author James Jackson <eeu203@bangor.ac.uk>
*/
public class DocTemplate {

    private String theDoc;
    private String original;

    public DocTemplate(String source) {
        original = getHtmlStr(source);
    }

     /**
     * Get HTML string from source
     * 
     * @param source URL
     */
    public DocTemplate(URL source) {
        try {
            original = getHtmlStr(source);
        } catch (URISyntaxException ex) {
            Logger.getLogger(DocTemplate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DocTemplate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Set theDoc from String original HTML source
     */
    public void prepareNewDoc() {
        theDoc = new String(original);
    }

    /**
     * Replace placeholder in the document with String
     * 
     * @param placeholder holds position for insert of String
     * @param html the String too be inserted into placeholder
     */
    public void replacePlaceholder(String placeholder, String html) {
        if (theDoc == null) {
            throw new IllegalStateException("Must call prepareNewDoc(), prior to setting placeholder values.");
        }
        theDoc = theDoc.replace("{{" + placeholder + "}}", html);
    }
    
    /**
     * Static method to replace placeholder in the document with String
     * 
     * @param placeholder holds position for insert of String
     * @param html the String to be inserted into placeholder
     * @param target the location where the placeholder is found
     * 
     * @return Updated String
     */
    public static String replacePlaceholder(String placeholder, String html, String target){
        return target.replace("{{" + placeholder + "}}", html);
    }
    
    /**
     * Returns the document in String format
     * 
     * @return response the doc in String format
     */
    public String getTheDoc() {
        String response = new String(theDoc);
        theDoc = null;
        return response;
    }

    /**
     * Gets the html in String format
     * 
     * @param file to encode 
     * 
     * @return theString 
     */
    private String getHtmlStr(String file) {
        try {
            byte[] encoded;
            encoded = Files.readAllBytes(Paths.get(file));
            String theString = new String(encoded);
            return theString;
        } catch (IOException ex) {
            Logger.getLogger(DocTemplate.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Converts contents of URL to a String
     * 
     * @param is URL to be converted to a String
     * 
     * @return html 
     */
    private String getHtmlStr(URL is) throws URISyntaxException, FileNotFoundException, IOException {
        URI in = is.toURI();
        String html = "";
        FileReader file = new FileReader(in.getPath());
        BufferedReader br = new BufferedReader(file);
        String line = br.readLine();
        while (line != null) {
            html += line;
            line = br.readLine();
        }
        file.close();
        br.close();
        return html;
    }

    /**
     * Static method which Converts contents of URL to a String
     * 
     * @param is URL to be converted to a String
     * 
     * @return html 
     */
    public static String getHTMLString(URL is) throws URISyntaxException, FileNotFoundException, IOException {
        URI in = is.toURI();
        String html = "";
        FileReader file = new FileReader(in.getPath());
        BufferedReader br = new BufferedReader(file);
        String line = br.readLine();
        while (line != null) {
            html += line;
            line = br.readLine();
        }
        file.close();
        br.close();
        return html;

    }
}
