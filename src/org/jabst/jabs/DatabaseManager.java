package org.jabst.jabs;

// Database imports
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import org.hsqldb.HsqlException;

// MessageDigest for SHA256 hash
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// I/O Imports
import java.io.PrintStream;
import java.io.InputStream;
import java.util.Scanner;

public class DatabaseManager {
    private static final String dbfilePrefix = "jdbc:hsqldb:file:";
    private static final String dbfileTesterName = "../db/jabs_database";
    private Connection connection;
    
    /** Creates a new DatabaseManager
     * Always open the DatabaseManager at program start (call the constructor),
     * and close it at program finish ( see: close() )
     * @param The name of the file to open
     * @throws HsqlException, SQLException
     */
    public DatabaseManager(String dbfile) {
        try {
            connection = DriverManager.getConnection(dbfilePrefix+dbfile, "sa", "");
        } catch (HsqlException hse) {
            System.err.println(
                "DriverManager: Error: Cannot connect to database file (driver error)"
            );
        } catch (SQLException se) {
            System.err.println(
                "DriverManager: Error: Cannot connect to database file (SQL error)"
            );
        }
    }
    
    /** Closes the database connection associated with the manager
        You MUST do this, or data will not be saved on program exit
     */
    public void close() {
        try {
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            // Nah don't bother handling it
        }
    }
    
    public static byte[] sha256(String message) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("No such algorith as SHA-256?");
            e.printStackTrace(System.err);
            return null;
        }
        return md.digest(message.getBytes());
    }
    
    public static void printDigest(byte[] digest, PrintStream ps) {
        for (int i = 0; i < digest.length; ++i) {
            ps.format("%x", digest[i]);
        } ps.println();
    }
    
    /**
     * Not currently used
     * @param digest the digest to convert to a hexadecimal string
     * @return A string representing the byte[] in hexadecimal text
     */
    public static String digestToHexString(byte[] digest) {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < digest.length; ++i) {
            s.append(String.format("%x", digest[i]));
        }
        return s.toString();
    }
    
    /** Asks the database to check if there is a user with the given
      * username and password
      */
    public boolean checkUser (String username, String password)
        throws SQLException {
        byte[] password_hash = sha256(password);
        boolean success = false;

        PreparedStatement statement = connection.prepareStatement(
            "SELECT * from CREDENTIALS WHERE USERNAME='?'"
        );
        
        statement.setString(1, username);
        ResultSet rs = statement.executeQuery(); 
        while (rs.next()) {
            if (
                   (rs.getString("username").equals(username))
                && (rs.getString("password").equals(password))
               ) { success = true; }
        }

        rs.close();
        statement.close();

        return success;
    }
    
    /** Adds a user with the username and password to the database
     *  @param username The username of the user
     *  @param password The password of the user, to be hashed with sha256
     *  before being stored
     *  @return Nothing, check for a SQLException. If it was a
     * SQLIntegrityConstraintViolationException, give a message about the
     * username already existing
     */
    public void addUser(String username, String password)
        throws SQLException {

        byte[] password_hash = sha256(password);
        PreparedStatement statement = null;

        statement = connection.prepareStatement(
            "INSERT INTO CREDENTIALS VALUES (?, ?)"
        );

        statement.setString(1, username);
        statement.setBytes(2, password_hash);

        System.out.println("About to execute adding user...");
        statement.execute();

        statement.close();
        
        // TODO: Always clean up! Leaving this here until we can set it
        // to act correctly on program shutdown
    }
    
    private void scannerAddUser(Scanner sc) {
        String username, password;
        byte[] digest;
        
        System.out.print("Enter username: ");
        username = sc.next();
        System.out.println();
        
        System.out.print("Enter password: ");
        password = sc.next();
        System.out.println();
        
        boolean success = false;
        try {
            addUser(username, password);
            success = true;
        } catch (SQLException se) {
                
            if (se instanceof SQLIntegrityConstraintViolationException) {
                System.err.println(
                    "Adding user failed: Already a user with that username"
                );
            }
            
            else {
                System.err.println("addUser failed...");
                se.printStackTrace(System.err);
            }
        }
        
        System.out.println(
            success ? "Added user successfully" : "Didn't add user"
        );
    }
    
    private void scannerCheckUser(Scanner sc) {
        String username, password;
        byte[] digest;
        
        System.out.print("Enter username: ");
        username = sc.next();
        System.out.println();
        
        System.out.print("Enter password: ");
        password = sc.next();
        System.out.println();
        
        boolean success = false;
        try {
            checkUser(username, password);
            success = true;
        } catch (SQLException se) {
                
            if (se instanceof SQLIntegrityConstraintViolationException) {
                System.err.println(
                    "Checking user failed: Already a user with that username"
                );
            }
            else {
                System.err.println("checkUser failed...");
                se.printStackTrace(System.err);
            }
        }
        
        System.out.println(
            success ? "Added user successfully" : "Didn't add user"
        );
    }
    
    public static void main (String[] args) {
        DatabaseManager dbm = new DatabaseManager(dbfileTesterName);
        Scanner sc = new Scanner(System.in);
        //sc.useDelimiter("\n");
        String input;
        while(true) {
            System.out.print("> ");
            input = sc.next();
            sc.nextLine();
            switch (input) {
                case "add":
                    dbm.scannerAddUser(sc);
                    break;
                case "check":
                    dbm.scannerCheckUser(sc);
                    break;
                case "quit":
                    dbm.close();
                    return;
                default:
                    System.out.println("Not a valid command. Received:"+input);
            }
        }
    }
}
