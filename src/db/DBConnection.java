package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * DBConnection
 * Database connection manager class for the Helpdesk Ticketing System.
 * Connects to MySQL database 'helpdesk_db' using JDBC driver (com.mysql.cj.jdbc.Driver).
 */
public class DBConnection {

    // MySQL Database Configuration Constants
    private static final String URL = "jdbc:mysql://localhost:3306/helpdesk_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Set your MySQL password here if applicable

    private static Connection connection = null;

    /**
     * Obtains a new database connection or validates existing connection.
     * @return Connection object to MySQL database
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Register JDBC Driver for MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Establish connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found. Please add mysql-connector-j JAR to your project library.");
            JOptionPane.showMessageDialog(null, 
                "MySQL JDBC Driver missing!\nPlease ensure mysql-connector-j JAR is added to the project classpath.", 
                "Database Driver Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error: Unable to connect to MySQL database at " + URL);
            JOptionPane.showMessageDialog(null, 
                "Database Connection Failed!\nCheck if MySQL is running on port 3306 and 'helpdesk_db' exists.\nError: " + e.getMessage(), 
                "Database Connection Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Closes the active database connection safely.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
