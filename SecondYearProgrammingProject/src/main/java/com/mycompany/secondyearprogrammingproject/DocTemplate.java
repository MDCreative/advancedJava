/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.secondyearprogrammingproject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jacko
 */
public class DocTemplate {
    
    public static String buildDoc(String source){
        String header = getHtmlStr(source);
        return header;
    }
    
    public static String replacePlaceholder(String placeholder, String html, String source){
        String doc = buildDoc(source);
        doc.replace("{{" + placeholder + "}}", html);
        return doc;
    } 
    
    public static String getHtmlStr(String file){
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
    
    public static void main(String[] args) {
        System.out.println(replacePlaceholder("TITLE", "Hello", "/header.html"));
    }
}
