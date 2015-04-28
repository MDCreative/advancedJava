/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author jacko
 */
public class DocTemplate {
    private String theDoc;
    public DocTemplate(String source){
        theDoc = getHtmlStr(source);
    }
    
    public DocTemplate(URL source){
        try {
            theDoc = getHtmlStr(source);
            System.out.println(theDoc);
        } catch (URISyntaxException ex) {
            Logger.getLogger(DocTemplate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DocTemplate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void replacePlaceholder(String placeholder, String html){
        theDoc = theDoc.replace("{{" + placeholder + "}}", html);
    } 
    
    public String getTheDoc(){
        return theDoc;
    }
    
    private String getHtmlStr(String file){
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
    private String getHtmlStr(URL is) throws URISyntaxException, FileNotFoundException, IOException{
        URI in = is.toURI();
        String html = "";
        FileReader file = new FileReader(in.getPath());
        BufferedReader br = new BufferedReader(file);
        String line = br.readLine();
        while(line != null){
            html+= line;
            line = br.readLine();
        }   
        return html;
    }
    
    public static String getHTMLString(URL is) throws URISyntaxException, FileNotFoundException, IOException{
        URI in = is.toURI();
        String html = "";
        FileReader file = new FileReader(in.getPath());
        BufferedReader br = new BufferedReader(file);
        String line = br.readLine();
        while(line != null){
            html+= line;
            line = br.readLine();
        }   
        return html;
    }
}
