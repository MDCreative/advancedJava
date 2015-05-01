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
    private String original;

    public DocTemplate(String source) {
        original = getHtmlStr(source);
    }

    public DocTemplate(URL source) {
        try {
            original = getHtmlStr(source);
        } catch (URISyntaxException ex) {
            Logger.getLogger(DocTemplate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DocTemplate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void prepareNewDoc() {
        theDoc = new String(original);
    }

    public void replacePlaceholder(String placeholder, String html) {
        if (theDoc == null) {
            throw new IllegalStateException("Must call prepareNewDoc(), prior to setting placeholder values.");
        }
        theDoc = theDoc.replace("{{" + placeholder + "}}", html);
    }
    
    public static String replacePlaceholder(String placeholder, String html, String target){
        return target.replace("{{" + placeholder + "}}", html);
    }

    public String getTheDoc() {
        String response = new String(theDoc);
        theDoc = null;
        return response;
    }

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
