package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
    private static final String JDBC_URL = "jdbc:sqlserver://YASHASWINI:1433;databaseName=EasySplitDB;integratedSecurity=true;encrypt=false;";
    private static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER_CLASS); // Load the JDBC driver
            return DriverManager.getConnection(JDBC_URL); // Establish the connection
        } catch (ClassNotFoundException e) {
            System.out.println("SQL Server JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
        return null; // Return null if connection fails
    }
}

