package com.mycompany.secondyearprogrammingproject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Security servlet,which is responsible for the hashing of passwords to and from the DB
 * 
 * @author Benjamin Williams <eeu222@bangor.ac.uk>
 * @author Jason Hall <eeu23e@bangor.ac.uk>
 * @author Liam Chapman <eeu239@bangor.ac.uk>
 * @author James Jackson <eeu203@bangor.ac.uk>
*/
public class Security {
    
    //http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
    /**
     * Converts bytes to hex as a hashing operation
     * 
     * @param bytes the values to hash
     * @return hexChars as String which is the new hash code
     */
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    /**
     * Takes a String to be hashed
     * 
     * @param toHash the String password to be hashed
     * @return 
     */
    public static String getHash(String toHash){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            md.update(toHash.getBytes("UTF-8")); // Change this to "UTF-16" if needed
            byte[] digest = md.digest();
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
