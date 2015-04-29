package com.mycompany.secondyearprogrammingproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The quiz servlet, which generates randomly distributed distinct questions.
 * 
 * @author Benjamin Williams <eeu222@bangor.ac.uk>
*/
public class Quiz extends HttpServlet
{    
    private Random rand = new Random();
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        //Set up the writer for printing out JSON, as well as the content type header
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        
        try
        {
            //Generate 20 questions
            Question[] questions = generateQuestions(20);
            
            //And print out the JSON
            out.print("[");
            
            for(int i = 0; i < questions.length; i++)
            {
                if(i < questions.length - 1)
                    out.print(questions[i] + ",");
                else
                    out.print(questions[i]);
            }
            
            out.print("]");
        }
        catch(SQLException e)
        {
            out.println(e.getMessage());
        }
    }
    
    /**
     * Generates an array of random questions.
     * 
     * @param amount The amount of random questions to generate.
     * @return An array of random questions.
     * @throws SQLException 
     */
    private Question[] generateQuestions(int amount) throws SQLException
    {
        try
        {
            //Try and initialize the database from the properties file.
            SimpleDataSource.init("/database.properties");
        }
        catch(IOException | ClassNotFoundException e)
        {
            System.out.println("[error] Couldn't initialize the database properties file.");
            System.out.println(e.getMessage());
            return null;
        }
        
        //Get a connection and a new statement to work with
        Connection connection = SimpleDataSource.getConnection();
        Statement statement = connection.createStatement();
        
        //Select 20 random words to question upon (distinct)
        ResultSet rs = statement.executeQuery("SELECT DISTINCT `translation`, `word`, `gender` FROM `word` ORDER BY RAND() LIMIT " + amount + ";");
       
        //Generate a new allocated array space for the return values
        Question[] returnValues = new Question[amount];
        
        //The index to add questions into the array with
        int idx = 0;
        
        while(rs.next())
        {
            //While there are more results, grab it and each column
            String english = rs.getString("translation");
            String german  = rs.getString("word");
            int gender     = rs.getInt("gender");
            
            //Generate a random question type
            int questionType = rand.nextInt(3);
                    
            //And add into the array depending on the question type.
            if(questionType == Question.TYPE_ENGLISH_TO_GERMAN)
                returnValues[idx++] = new Question(questionType, english, german);
            
            else if(questionType == Question.TYPE_GERMAN_TO_ENGLISH)
                returnValues[idx++] = new Question(questionType, german, english);
            
            else if(questionType == Question.TYPE_GENDER)
                returnValues[idx++] = new Question(questionType, german, getGenderWord(gender));
            
        }
        
        //Finishing up, and returning the values
        statement.close();
        connection.close();
        
        return returnValues;
    }
    
    /**
     * Returns a textual representation for a gender specified by an integer.
     * 
     * @param gender The gender, as an integer.
     * @return The textual representation of the gender.
     */
    private String getGenderWord(int gender)
    {
        String[] genders =
        {
            "none", "masculine", "feminine", "neuter", "plural"
        };
        
        //If it's out of bounds, return "none", otherwise return whatever index it is into the array.
        return (gender > 0 && gender < genders.length) ? (genders[gender]) : ("none");
    }
    
    /**
     * A class to represent a random question given throughout the quiz.
     */
    private class Question
    {
        //Question types - german -> english, etc.
        public final static int TYPE_GERMAN_TO_ENGLISH = 0;
        public final static int TYPE_ENGLISH_TO_GERMAN = 1;
        public final static int TYPE_GENDER            = 2;
                
        //This question type, the word to quiz on and the correct answer.
        private int questionType;
        private String word;
        private String answer;
        
        /**
         * Constructs a new question with the given parameters.
         * 
         * @param questionType The question type to ask.
         * @param word The word to quiz on.
         * @param answer The answer to the question.
         */
        public Question(int questionType, String word, String answer)
        {
            this.questionType = questionType;
            this.word = word;
            this.answer = answer;
        }
        
        /**
         * Converts the question into a JSON readable format.
         * 
         * @return The question, represented as a JSON object.
         */
        @Override
        public String toString()
        {
            //The return string to work with.
            String returnString = "";
            
            //Simply add each property to the return string and return it
            returnString += "{\"type\":"   + questionType   + ",";
            returnString += "\"word\":\""   + word.trim()   + "\",";
            returnString += "\"answer\":\"" + answer.trim() + "\"}";
            
            return returnString;
        }
    }
}
