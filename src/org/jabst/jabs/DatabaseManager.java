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
    private static final String[] SQL_TABLES = {
        "CREATE TABLE CREDENTIALS ( USERNAME VARCHAR(20), PASSWORD VARBINARY(32), USERTYPE VARCHAR(1), PRIMARY KEY(USERNAME))",
        "CREATE TABLE CUSTOMERS ( USERNAME VARCHAR(20), NAME VARCHAR(40), ADDRESS VARCHAR(255), PHONE VARCHAR(10), PRIMARY KEY(USERNAME), FOREIGN KEY (USERNAME) REFERENCES CREDENTIALS(USERNAME));"
    };
    public static final String dbDefaultFileName = "db/jabs_database";
    private Connection connection;
    
    /** Creates a new DatabaseManager
     * Always open the DatabaseManager at program start (call the constructor),
     * and close it at program finish ( see: close() )
     * @param The name of the file to open
     * @throws HsqlException, SQLException
     */
    public DatabaseManager(String dbfile) throws HsqlException, SQLException {
         try {
             this.connection = DriverManager.getConnection(dbfilePrefix+dbfile+";ifexists=true", "sa", "");
         } catch (HsqlException hse) {
             System.err.println("HqlException conecting to database: Doesn't exist");
         }

         catch (SQLException se) {
            try {
                connection = DriverManager.getConnection(dbfilePrefix+dbfile, "sa", "");
            } catch (SQLException sqle) {
                System.err.println(
                    "DriverManager: Error: Cannot connect to database file (SQL error) (when trying to open new)"
                );
            }
            if (!createTables()) {
                System.err.println(
                    "DriverManager: Error: Cannot create tables in new database"
                 );
            }
         }
    }
    
    /** Creates the database tables
     *  in case the database is being created for the first time
     *  @return whether the tables could be successfully created
     */
    private boolean createTables() {
        boolean success = false;
        for (String currTable : SQL_TABLES) {
            Statement statement = null;
            try {
                statement = connection.createStatement();
            } catch (SQLException se) {
                System.err.println("Error creating statement for tables");
                return false;
            }
            
            try {
                // Statement.execute returns false if no results were returned,
                // including for CREATE statements
                statement.execute(currTable);
                System.out.println("Successfully created tables");
                success = true;
            } catch (SQLException se) {
                System.out.println("Did not successfully create tables");
                return false;   
            }
        }
        return success;
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
            System.err.println(
                "DatabaseManager: Error closing database properly. Continuing."
            );
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
            "SELECT * from CREDENTIALS WHERE USERNAME='"+username+"'"
        );

        ResultSet rs = statement.executeQuery(); 
        while (rs.next()) {
            String result_username = rs.getString("username");
            byte[] result_password = rs.getBytes("password");
            System.out.format("Input: username,password = %s,%s\n",
                username, digestToHexString(password_hash)
            );
            System.out.format("Result:username,password = %s,%s\n",
                result_username, digestToHexString(result_password)
            );
            
            for (int i = 0; i < result_password.length; ++i) {
                if (result_password[i] != password_hash[i]) {
                    success = false;
                    break;
                }
                success = true;
            }
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
            "INSERT INTO CREDENTIALS VALUES (?, ?, 'C')"
        );

        statement.setString(1, username);
        statement.setBytes(2, password_hash);

        System.out.println("About to execute adding user...");
        statement.execute();

        statement.close();
        // After adding a user, they need to be able to log in again
        connection.commit();
    }

    public void addUser(String username, String password,
        String name, String address, String phone) throws SQLException
    {
        // Add to credentials table
        addUser(username, password);
        
        // Now add to customers table
        PreparedStatement statement = connection.prepareStatement(
            // USERNAME, NAME, ADDRESS, PHONE
            "INSERT INTO CUSTOMERS VALUES (?, ?, ?, ?)"
        );
        statement.setString(1, username);
        statement.setString(2, name);
        statement.setString(3, address);
        statement.setString(4, phone);
        
        statement.execute();
        statement.close();
    }
    
    private void scannerAddUser(Scanner sc) {
        String username, password, name, address, phone;
        byte[] digest;
        
        System.out.print("Enter username: ");
        username = sc.next();
        System.out.println();
        
        System.out.print("Enter password: ");
        password = sc.next();
        System.out.println();
        
        System.out.print("Next up: name, address, phone");
        name = sc.next(); System.out.println();
        address = sc.next(); System.out.println();
        phone = sc.next(); System.out.println();
        
        boolean success = false;
        try {
            addUser(username, password, name, address, phone);
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
    
    public String checkUserType(String username) {
        // NYI: Check if in Business(name)
        return "Customer";
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
            success = checkUser(username, password);
        } catch (SQLException se) {
                System.err.println("checkUser failed...");
                se.printStackTrace(System.err);
        }
        
        System.out.println(
            success ?
                "Found a user with that username (NYI: and password" :
                "Didn't find a user with that username"
        );
    }
    /**
      * This main exists to interactively test the code in DatabaseManager. It is
      * not the entry point to the actual application
      */
    public static void main (String[] args) throws SQLException, HsqlException{
        DatabaseManager dbm = new DatabaseManager(dbDefaultFileName);
        System.out.println("Connecting to database: "+dbDefaultFileName);
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
