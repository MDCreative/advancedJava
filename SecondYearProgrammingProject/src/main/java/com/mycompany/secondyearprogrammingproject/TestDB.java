package com.mycompany.secondyearprogrammingproject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDB {

    public static void main(String[] args) throws Exception {
        
        SimpleDataSource.init("/database.properties");
        Connection conn = SimpleDataSource.getConnection();

        try {
            Statement stat = conn.createStatement();
            stat.execute("CREATE TABLE Test (Name VARCHAR(20))");
            stat.execute("INSERT INTO Test VALUES ('Romeo')");
            ResultSet result
                    = stat.executeQuery("SELECT * FROM Test");
            result.next();
        } finally {
            conn.close();
        }
    }
}
