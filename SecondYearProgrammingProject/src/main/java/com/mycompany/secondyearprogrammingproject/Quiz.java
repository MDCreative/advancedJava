package com.mycompany.secondyearprogrammingproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * The quiz servlet, which generates randomly distributed distinct questions.
 * 
 * @author Benjamin Williams <eeu222@bangor.ac.uk>
*/
public class Quiz extends HttpServlet
{    
    private Random rand;
    
    public Quiz()
    {
        rand = new Random();
        
        if(SimpleDataSource.isInitialized())
            return;
        
        try
        {            
            //Try and initialize the database from the properties file.
            SimpleDataSource.init("/database.properties");
        }
        catch(IOException | ClassNotFoundException e)
        {
            System.out.println("[error] Couldn't initialize the database properties file.");
            System.out.println(e.getMessage());
        }
    }
    
    private String join(String[] array)
    {
        String returnValue = "";
        
        for(int i = 0; i < array.length; i++)
            returnValue += array[i] + ((i < array.length - 1) ? (", ") : (""));
        
        return returnValue;
    }
    
    /**
     * Updates incorrect and correct answer count in the database.
     * 
     * @param correct The string of correct question ids.
     * @param incorrect The string of incorrect question ids.
     * @throws SQLException 
     */
    private void updateCorrectQuestions(String correct, String incorrect) throws SQLException
    {
        //Split by space, join with comma
        String correctAnswers = join(correct.split(" "));
        String incorrectAnswers = join(incorrect.split(" "));
        
        //Get connection and statement
        Connection connection = SimpleDataSource.getConnection();
        Statement statement = connection.createStatement();
        
        //Execute updates
        String query = "UPDATE `word` SET `correct_ans` = (`correct_ans` + 1) WHERE `id` IN (" + correctAnswers + ");";
        statement.executeUpdate(query);
        
        query = "UPDATE `word` SET `incorrect_ans` = (`correct_ans` + 1) WHERE `id` IN (" + incorrectAnswers + ");";
        statement.executeUpdate(query);
    }
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        String quizData = request.getParameter("data");
        String incorrectQuestions = request.getParameter("incorrectQuestions");
        String correctQuestions = request.getParameter("correctQuestions");
        
        int score = Integer.parseInt(request.getParameter("score"));
            
        HttpSession session = request.getSession();
        
        int id = 123;

        if(session != null)
        {
            String strid = (String)session.getAttribute("id");
            
            if(strid != null)
                id = Integer.parseInt(strid);
        }
        
        Connection connection = null;
        PreparedStatement statement = null;
        
        try
        {
            connection = SimpleDataSource.getConnection();
            
            statement = connection.prepareStatement("INSERT INTO `test_history` (`user_id`, `score`, `data`) VALUES (?, ?, ?);");
            
            statement.setInt(1, id);
            statement.setInt(2, score);
            statement.setString(3, quizData);
            
            statement.executeUpdate();       
            
            updateCorrectQuestions(correctQuestions, incorrectQuestions);
        }
        catch(SQLException e)
        {
            System.out.println("[error] Failed to insert quiz data into the database: ");
            System.out.println(e.getMessage());
        }
        finally
        {
            try
            {
                connection.close();
                statement.close();
            }
            catch(SQLException e)
            {
                System.out.println("[error] Failed to close connection or statement objects.");
            }
        }
    }
    
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
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        Question[] returnValues = new Question[amount];
        
        try
        {
            //Get a connection and a new statement to work with
            connection = SimpleDataSource.getConnection();
            statement = connection.createStatement();

            //Select 20 random words to question upon (distinct)
            rs = statement.executeQuery("SELECT DISTINCT `id`, `translation`, `word`, `gender` FROM `word` ORDER BY RAND() LIMIT " + amount + ";");

            //The index to add questions into the array with
            int idx = 0;

            while(rs.next())
            {
                //While there are more results, grab it and each column
                String english = rs.getString("translation");
                String german  = rs.getString("word");
                int gender     = rs.getInt("gender");
                int id         = rs.getInt("id");
                
                //Generate a random question type
                int questionType = rand.nextInt(3);

                //And add into the array depending on the question type.
                if(questionType == Question.TYPE_ENGLISH_TO_GERMAN)
                    returnValues[idx++] = new Question(id, questionType, english, german);

                else if(questionType == Question.TYPE_GERMAN_TO_ENGLISH)
                    returnValues[idx++] = new Question(id, questionType, german, english);

                else if(questionType == Question.TYPE_GENDER)
                    returnValues[idx++] = new Question(id, questionType, german, getGenderWord(gender));

            }
        }
        finally
        {
            //Finishing up, and returning the values
            rs.close();
            statement.close();
            connection.close();
        }
        
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
        private int id;
        
        /**
         * Constructs a new question with the given parameters.
         * 
         * @param id The question's unique ID.
         * @param questionType The question type to ask.
         * @param word The word to quiz on.
         * @param answer The answer to the question.
         */
        public Question(int id, int questionType, String word, String answer)
        {
            this.id = id;
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
            returnString += "{\"type\":"    + questionType  + ",";
            returnString += "\"id\":"       + id            + ",";
            returnString += "\"word\":\""   + word.trim()   + "\",";
            returnString += "\"answer\":\"" + answer.trim() + "\"}";
            
            return returnString;
        }
    }
}
