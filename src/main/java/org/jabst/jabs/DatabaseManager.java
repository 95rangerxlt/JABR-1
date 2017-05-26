package org.jabst.jabs;

// Database imports
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import org.hsqldb.HsqlException;

// I/O Imports
import java.io.PrintStream;
import java.io.InputStream;
import java.io.File;
import java.util.Scanner;
import java.util.Date;

// Logging
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;

// For SHA-256 hashing
import org.jabst.jabs.util.Digest;

import org.jabst.jabs.util.DayOfWeekConversion;

// For returning result sets as native objects
import java.util.ArrayList;
import java.util.ListIterator;

// Time imports
import java.util.Calendar;
import java.util.Calendar.Builder;
import java.util.TimeZone;
import java.time.DateTimeException;
import java.time.DayOfWeek;

public class DatabaseManager {
    /** The string that all database file connections start with */
    private static final String dbfilePrefix = "jdbc:hsqldb:file:";
    /** The SQL tables and data that are in the general database by default.
      * Used for testing without manual insertion.
      */
    private static final String[] SQL_TABLES_GENERAL = {
        "CREATE TABLE CREDENTIALS ("
            +"USERNAME VARCHAR(20),"
            +"PASSWORD VARBINARY(32) NOT NULL,"
            +"BUSINESS VARCHAR (40)," 
            +"PRIMARY KEY(USERNAME)"
        +");",
        "CREATE TABLE BUSINESS ("
            +"USERNAME VARCHAR (40),"
            +"BUSINESS_NAME VARCHAR(40) NOT NULL,"
            +"OWNER_NAME VARCHAR(40) NOT NULL,"
            +"ADDRESS VARCHAR(255) NOT NULL,"
            +"PHONE VARCHAR(10) NOT NULL,"
            +"ICONFILE VARCHAR(255),"
            +"PRIMARY KEY (USERNAME),"
            +"FOREIGN KEY (USERNAME) REFERENCES CREDENTIALS(USERNAME)"
        +");",

        "CREATE TABLE SUPERUSER ("
            +"USERNAME VARCHAR(20),"
            +"PRIMARY KEY (USERNAME),"
            +"FOREIGN KEY (USERNAME) REFERENCES CREDENTIALS(USERNAME)"
        +");",
        // Default data
        // passwords = default
        "INSERT INTO CREDENTIALS VALUES('default_business','37a8eec1ce19687d132fe29051dca629d164e2c4958ba141d5f4133a33f0688f', 'default_business')",
        "INSERT INTO CREDENTIALS VALUES('default_customer','37a8eec1ce19687d132fe29051dca629d164e2c4958ba141d5f4133a33f0688f', 'default_business')",
        "INSERT INTO CREDENTIALS VALUES ('root', '37a8eec1ce19687d132fe29051dca629d164e2c4958ba141d5f4133a33f0688f', NULL)",
        "INSERT INTO BUSINESS"
            +"(USERNAME, BUSINESS_NAME, OWNER_NAME, ADDRESS, PHONE)"
            +"VALUES('default_business', 'default business', 'default_owner', 'default_addr', '0420123456')",
        "INSERT INTO SUPERUSER VALUES ('root')"
    };
    /** The SQL tables and data that are in a business database by default */
    private static final String[] SQL_TABLES_BUSINESS = {
        // Always put customers first
        "CREATE TABLE CUSTOMERS ("
            +"USERNAME VARCHAR(20),"
            +"NAME VARCHAR(40) NOT NULL,"
            +"ADDRESS VARCHAR(255) NOT NULL,"
            +"PHONE VARCHAR(10) NOT NULL,"
            +"PRIMARY KEY(USERNAME)"
        +");",

        "CREATE TABLE EMPLOYEE ("
            +    "EMPL_ID INTEGER GENERATED ALWAYS AS IDENTITY,"
            +    "EMPL_NAME VARCHAR(40) NOT NULL,"
            +    "ADDRESS VARCHAR(255),"
            +    "PHONE VARCHAR(10),"
            +    "PRIMARY KEY(EMPL_ID)"
            +" )",

        "CREATE TABLE AVAILABILITY ("
            +   "EMPLOYEE INTEGER,"
            +   "AVAILABLE_TIME INTEGER,"
            +   "AVAILABLE_DAY INTEGER," // Day of week
            +   "FOREIGN KEY (EMPLOYEE) REFERENCES EMPLOYEE(EMPL_ID),"
            +   "PRIMARY KEY(EMPLOYEE, AVAILABLE_TIME, AVAILABLE_DAY)"
        +")",

        "CREATE TABLE APPOINTMENTTYPE ("
            +   "TYPE_ID INTEGER GENERATED ALWAYS AS IDENTITY,"
            +   "NAME VARCHAR(40) NOT NULL,"
            +   "COST_CENTS INTEGER NOT NULL,"
            +   "PRIMARY KEY (TYPE_ID)"
        +")",

        "CREATE TABLE APPOINTMENT ("
            +   "APT_ID INTEGER GENERATED ALWAYS AS IDENTITY,"
            +   "DATE_AND_TIME DATETIME NOT NULL,"
            +   "APPOINTMENT_TYPE INTEGER NOT NULL,"
            +   "EMPLOYEE INTEGER NOT NULL,"
            +   "CUSTOMER VARCHAR(20) NOT NULL,"
            +   "PRIMARY KEY (APT_ID),"
            +   "FOREIGN KEY (APPOINTMENT_TYPE)"
            +   "    REFERENCES APPOINTMENTTYPE (TYPE_ID),"
            +   "FOREIGN KEY (EMPLOYEE) REFERENCES EMPLOYEE(EMPL_ID)"
        +")",
        // Default data
        "INSERT INTO CUSTOMERS VALUES('default_customer','default customer','default','0420123546');",
        "INSERT INTO EMPLOYEE VALUES (DEFAULT, 'default_employee', 'default', '0420123456');",
        "INSERT INTO APPOINTMENTTYPE VALUES (DEFAULT, 'DEFAULT_APPOINTMENT_TYPE', 99);",
        "INSERT INTO AVAILABILITY VALUES (0, 36000, 1);",
        "INSERT INTO AVAILABILITY VALUES (0, 32400, 1);",
        "INSERT INTO APPOINTMENT VALUES (DEFAULT, %s+INTERVAL '9' HOUR, 0, 0, 'default_customer');",
        "INSERT INTO APPOINTMENT VALUES (DEFAULT, %s+INTERVAL '10' HOUR, 0, 0, 'default_customer');"
    };

    /** The name of the general database */
    public static final String dbDefaultFileName = "db/credentials_db";
    /** The name of the default business' database file */
    public static final String defaultBusinessName = "default_business";
    /** Logger. All output should go through logger instead of System.out */
    private Logger logger;
    /** Sends the logs to stderr */
    private ConsoleHandler ch;
    /** The JDBC connection to the general (user info) database */
    private Connection generalConnection;
    /** The JDBC connection to the business-specific database*/
    private Connection businessConnection;
    /** The current business. Used to inform the rest of the system. */
    private Business currBus;

    /** Creates a new DatabaseManager
     * Always open the DatabaseManager at program start (call the constructor),
     * and close it at program finish ( see: close() )
     * @param The name of the file to open
     * @throws HsqlException, SQLException
     */
    public DatabaseManager(String dbfile) throws HsqlException, SQLException {
        this.logger = Logger.getLogger("org.jabst.jabs.DatabaseManager");
        logger.setLevel(Level.FINEST);
        this.ch = new ConsoleHandler();
        logger.addHandler(ch);
        logger.info("Opened databaseManager logger");
        this.generalConnection = openCreateDatabase(dbfile, SQL_TABLES_GENERAL);
        if (generalConnection == null) {
            throw new SQLException();
        }
        generalConnection.setAutoCommit(false);
    }

    /** Creates the database tables in case the database is being
      * created for the first time
      * @param connection The connection: Either businessConnection or generalConnection
      * @param tables An array of strings which are SQL statements to be executed
      * to set up the database. Not necessarily all CREATE statements.
      * @return whether the tables were successfully created
      */
    private boolean createTables(Connection connection, String[] tables) {
        // Problem: SQL uses dates Sun-Sat, DayOfWeek uses them Mon-Sun,
        // Hackish Solution: Make methods to convert between the two

        // Get the day of Week in Sun-Sat form
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Format cal as a string for HyperSQL
        String dateStr = String.format("DATE '%04d-%02d-%02d'",
                 cal.get(Calendar.YEAR),
                 cal.get(Calendar.MONTH)+1,
                 cal.get(Calendar.DAY_OF_MONTH)
                );

        // Insert the date into these INSERT statements by String.format
        // Doing it from Java is easier.
        SQL_TABLES_BUSINESS[10] = String.format(SQL_TABLES_BUSINESS[10], dateStr);
        SQL_TABLES_BUSINESS[11] = String.format(SQL_TABLES_BUSINESS[11], dateStr);

        // Try to execute each SQL statement in tables
        boolean success = false;
        int i = 0;
        for (String currTable : tables) {
            Statement statement = null;
            try {
                statement = connection.createStatement();
            } catch (SQLException se) {
                logger.severe("Failed to create new statement in createTables");
                return false;
            }

            try {
                // Statement.execute returns false if no results were returned,
                // including for CREATE statements
                statement.execute(currTable);
                logger.info("Successfully created table");
                success = true;
            } catch (SQLException se) {
                logger.severe("Failed to create"
                        +"table:"+tables[i]);
                se.printStackTrace();
                return false;
            }
            ++i;
        }
        return success;
    }

    /** Asks the database to save the data it has now. It should generally do
      * this by itself, but you can use this to be safe.
      */
    public void commit() {
        try {
            generalConnection.commit();
            if (businessConnection != null && !businessConnection.isClosed()) {
                businessConnection.commit();
            }
        } catch (SQLException e) {
            logger.severe("DatabaseManager: Error commiting");
            e.printStackTrace();
        }
    }

    /** Closes the database connections associated with the manager
      * You MUST do this, or data will not be saved on program exit
      */
    public void close() {
        try {
            generalConnection.commit();
            generalConnection.close();
            if (businessConnection != null && !businessConnection.isClosed()) {
                businessConnection.commit();
                businessConnection.close();
            }
        } catch (SQLException e) {
            // Nah don't bother handling it
            logger.warning(
                "DatabaseManager: Error closing database properly. Continuing."
            );
        }
    }

    /** Tries to connect to the given database, and create it if it doesn't exist already
      * @param dbFileName The name of the database file to connect to
      * @param tables A string array of SQL statements to execute to make the tables in the
      * new database
      * @return A connection to the database if successful, otherwise null.
      */
    private Connection openCreateDatabase(String dbFileName, String[] tables) {
        Connection c = null;
         try {
             c = DriverManager.getConnection(dbfilePrefix+dbFileName+";ifexists=true", "sa", "");
         } catch (HsqlException hse) {
             logger.severe("HqlException conecting to database'"+dbFileName+"': Doesn't exist");
         }

         catch (SQLException se) {
            try {
                c = DriverManager.getConnection(dbfilePrefix+dbFileName, "sa", "");
            } catch (SQLException sqle) {
                logger.severe(
                    "DriverManager: Error: Cannot connect to general database"
                   +"file (SQL error) (when trying to open new)"
                );
            }
            if (!createTables(c, tables)) {
                logger.severe(
                    "DriverManager: Error: Cannot create tables in database'"
                    + dbFileName+ "'"
                 );
            }
         }
        return c;
    }

    /** Opens a connection to the business specified with the username
      * The database file is located in db/$username
      * @param String busUsername : The username of the business
      * @return Whether a connection was sucessfully made
      */
    public boolean connectToBusiness(String busUsername) throws SQLException {
        // Close any existing connection
        if (businessConnection != null && !businessConnection.isClosed()) {
            businessConnection.close();
        }

        // Look up the business name in the table of businesses
        Statement stmt = generalConnection.createStatement();

        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM BUSINESS WHERE USERNAME='"+busUsername+"'"
        );
        if (rs.next()) {
            this.currBus = new Business (
                rs.getString("USERNAME"),
                rs.getString("BUSINESS_NAME"),
                rs.getString("OWNER_NAME"),
                rs.getString("ADDRESS"),
                rs.getString("PHONE")
            );
        }
        else {
            logger.warning("connectToBusiness: No business with that username");
            return false;
        }

        // We now know it exists for certain, but not whether it has a database
        // Open or create the business' database
        this.businessConnection = openCreateDatabase("db/"+busUsername, SQL_TABLES_BUSINESS);
        if (this.businessConnection == null) {
            return false;
        }
        this.businessConnection.setAutoCommit(false);
        return true;
    }
    
    public Business getCurrentBusiness() {
        return this.currBus;
    }

    /** Gets the business associated with the username
      * @param businessUsername the username of the business account
      * @return A Business object representing the business, or null if
      * it does not exist or the database encountered an error.
      */
    public Business getBusiness(String businessUsername) {
        try {
            Statement stmt = generalConnection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT * FROM BUSINESS "
            +"WHERE USERNAME='"+businessUsername+"'"
            );

            rs.next();
            return new Business (
                rs.getString("USERNAME"),
                rs.getString("BUSINESS_NAME"),
                rs.getString("OWNER_NAME"),
                rs.getString("ADDRESS"),
                rs.getString("PHONE")
            );
        }
        catch (SQLException sqle) {
            return null;
        }
    }

    public String getCustomerBusinessName(String username) {
        try {
            Statement stmt = generalConnection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT BUSINESS FROM CREDENTIALS WHERE USERNAME='"+username+"'"
            );
            if (rs.next()) {
                return rs.getString("BUSINESS");
            }
            else {
                return null;
            }
        }
    catch (SQLException sqle) {
        logger.severe("Database error getting customer business name:"+username);
        return null;
    }
    }

    public ArrayList<Business> getAllBusinesses() {
        ArrayList<Business> businesses = new ArrayList<Business>();
        try {
            Statement stmt = generalConnection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM BUSINESS ");

            while(rs.next()) {
                businesses.add(new Business (
                    rs.getString("USERNAME"),
                    rs.getString("BUSINESS_NAME"),
                    rs.getString("OWNER_NAME"),
                    rs.getString("ADDRESS"),
                    rs.getString("PHONE")
                ));
            }
        }
        catch (SQLException sqle) {
            return null;
        }
        return businesses;
    }
    
    /** Gets only the businesses who have logged in before. Only these
      * businesses can be registered with; the others are not ready to take
      * registrations.
      * @throws SQLException If the databas(es) have an error
      */
    public ArrayList<Business> getActiveBusinesses() throws SQLException {
        ArrayList<Business> businesses = getAllBusinesses();
        if (businesses == null) return null;
        ListIterator<Business> it = businesses.listIterator();
        while (it.hasNext()){
            Business b = it.next();
            try {
                String busDBFileName = dbfilePrefix+"db/"+b.username+";ifexists=true";
                Connection c = DriverManager.getConnection(busDBFileName, "sa", "");
                c.close();
            }
            // If we can't establish a connection, it must not exist. Remove it.
            catch (SQLException sqle) {
                it.remove();
            }
        }
        return businesses;
    }

    /** Update the business info in the database.
      * @return true if there was an update, false if there was not.
      * @throws SQLException If a database error occurred. */
    public boolean updateBusinessInfo(
        String busUsername,
        String busName,
        String owner,
        String addr,
        String phone,
        File iconFile
    ) throws SQLException
    {
        String filePath;
        if (iconFile != null) {
            filePath = iconFile.getAbsolutePath();
        }
        else {
            filePath = "NULL";
        }
            
        PreparedStatement pstmt = generalConnection.prepareStatement(
        "UPDATE BUSINESS "
            +"SET "
                +"BUSINESS_NAME=?,"
                +"OWNER_NAME=?,"
                +"ADDRESS=?,"
                +"PHONE=? "
            +"WHERE USERNAME=?"
        );

        pstmt.setString(1, busName);
        pstmt.setString(2, owner);
        pstmt.setString(3, addr);
        pstmt.setString(4, phone);
        pstmt.setString(5, busUsername);

        int updateCount = pstmt.executeUpdate();
        return (updateCount > 0 ? true : false);
    }

    /** Deletes the business from given object representation.
      * @return true if the business existed and was deleted; false if it did not
      * @param bus The business to delete
      * @throws SQLException If a database error occurred, which does not
      * include when the business does not exist */
    public boolean deleteBusiness(Business bus) throws SQLException {
        Statement stmt = generalConnection.createStatement();
        stmt.execute(
            String.format(
                "DELETE FROM BUSINESS "
                    +"WHERE business_name='%s'"
                    +"AND   owner_name='%s'"
                    +"AND   address='%s'"
                    +"AND   phone='%s'",
                    bus.businessName,
                    bus.businessOwner,
                    bus.address,
                    bus.phone
            )
        );
        if (stmt.getUpdateCount() == 0) {
            return false;
        }
        else {
            return true;
        }
    }

    /** Asks the database to check if there is a user with the given
      * username and password
      * @param username The username of the user. The user may be a business
      * or a customer.
      * @param password The password of the user. The only restriction is that
      * the passowrd given should not be blank.
      */
    public boolean checkUser (String username, String password)
        throws SQLException {
        // Take the hash 
        byte[] password_hash = Digest.sha256(password);
        boolean success = false;

        PreparedStatement statement = generalConnection.prepareStatement(
            "SELECT USERNAME, PASSWORD FROM CREDENTIALS WHERE USERNAME='"+username+"'"
        );

        ResultSet rs = statement.executeQuery(); 
        while (rs.next()) {
            String result_username = rs.getString("username");
            byte[] result_password = rs.getBytes("password");
            logger.info("Input: username,password = "+username+","
                + Digest.digestToHexString(password_hash)+"\n"
            );
            logger.info("Result:username,password = "+result_username+","
                + Digest.digestToHexString(result_password)+"\n"
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
     *  @throws SQLException If a general database error occurs
     *  @throws SQLIntegrityConstraintViolationException If this exception is
     *  thrown, the caller should give a message about the username/password
     *  already existing.
     */
    private void addUser(String username, String password, BusinessSelection business)
        throws SQLException {

        byte[] password_hash = Digest.sha256(password);
        PreparedStatement statement = null;

        statement = generalConnection.prepareStatement(
            "INSERT INTO CREDENTIALS VALUES (?, ?, ?)"
        );

        statement.setString(1, username);
        statement.setBytes(2, password_hash);
        statement.setString(3, business.business.username);

        logger.info("About to execute adding user...");
        statement.execute();

        statement.close();
        // Don't commit, we don't know if we're successful yet
    }

    public boolean addCustomer(String username, String password, String name,
        String address, String phone, BusinessSelection business)
        throws SQLException
    {

        generalConnection.commit();
        addUser(username, password, business);
        // Open a connection to the business if it exists
        Connection c = null;
        String dbFileName = business.business.username;
        try {
            c = DriverManager.getConnection(dbfilePrefix+"db/"+dbFileName+";ifexists=true", "sa", "");
        }
        // If it doesn't exist, error
        catch (HsqlException hse) {
            logger.severe("HqlException conecting to database'"+dbFileName+"': Doesn't exist");
            generalConnection.rollback();
            c.close();
            return false;
        }
        // Insert data into business table
        PreparedStatement stmt = c.prepareStatement(
            "INSERT INTO CUSTOMERS VALUES (?, ?, ?, ?)"
        );
        stmt.setString(1, username);
        stmt.setString(2, name);
        stmt.setString(3, address);
        stmt.setString(4, phone);
        stmt.executeUpdate();
        
        // Disconnect from business
        c.close();
        // It's now safe to update the credentials database
        generalConnection.commit();
        return true;
    }

    /** Attempts to register a new business in the system with a
      * given set of attributes.
      * @throws SQLException If a database error occurs 
      */
    public void registerBusiness(
        String username, String password,
        String busname, String ownername,
        String address, String phone
    ) throws SQLException
    {
        /* Insert entries into the general database */
        Business bus = new Business(username, busname, ownername, address, phone);
        generalConnection.commit();
        try {
            addUser(username, password, new BusinessSelection(bus));
        } catch (SQLException sqle) {
            // Do not save the user if they were not succesfully added
            logger.info(String.format(
                "registerBusiness: SQLException adding business with AddUser"
               +"(%s, %s, %s)", username, password, busname
            ));
            generalConnection.rollback();
            throw sqle;
        }
        
        try {
            PreparedStatement stmt = generalConnection.prepareStatement(
                "INSERT INTO BUSINESS "
               +"(USERNAME, BUSINESS_NAME, OWNER_NAME, ADDRESS, PHONE)"
               +"VALUES (?, ?, ?, ?, ?)"
            );
            stmt.setString(1, username);
            stmt.setString(2, busname);
            stmt.setString(3, ownername);
            stmt.setString(4, address);
            stmt.setString(5, phone);
            stmt.executeUpdate();            
            stmt.close();
            generalConnection.commit();
        } catch (SQLException sqle) {
            logger.info(String.format(
                "registerBusiness: SQLException adding business to BUSINESS:"
               +"(%s, %s, %s, %s, %s)",
                username, busname, ownername, address, phone
            ));
            throw sqle;
        }

        /* Open the business database for setup */
        connectToBusiness(username);
        businessConnection.close();
    }

    /** Gets the customer with the given username. Customers are uniquely
      * identified in the database by their username.
      * @param username The customer's username
      * @return A Customer object with the customer's username, name,
      * address, and phone number.
      * @throws SQLException If a database error occurs
      */
    public Customer getCustomer(String username) throws SQLException {
        // Get the customer by username
        Statement stmt = businessConnection.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT USERNAME, NAME, ADDRESS, PHONE FROM CUSTOMERS "
           +"WHERE USERNAME='"+username+"'"
        );
        // Construct the customer as object and return
        if (rs.next()) {
            return new Customer (
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4)
            );
        }
        else {
            logger.severe("Error getting customer:"+username+"\nNo results.");
            logger.severe("Query was:");
            logger.severe("SELECT USERNAME, NAME, ADDRESS, PHONE FROM CUSTOMERS"
                +" WHERE USERNAME='"+username+"'");
            return null;
        }
    }

    /** Returns all the customers in the database as ArrayList<Customer>
      * @throws SQLException If a database error occurs
      */
    public ArrayList<Customer> getAllCustomers() throws SQLException {
        ArrayList<Customer> customers = new ArrayList<Customer>();
        // Ask database for all customers
        PreparedStatement pstmt = generalConnection.prepareStatement(
            "SELECT USERNAME, NAME, ADDRESS, PHONE FROM CUSTOMERS "
        );
        ResultSet rs = pstmt.executeQuery();
        // Construct the customer as object and return
        while (rs.next()) {
            customers.add (
                new Customer (
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4)
                )
            );
        }
        return customers;
    }

    public ArrayList<String> getCustomerInfoForDropDown() {
        ArrayList<String> customers = new ArrayList<String>();
        try {
            // Ask database for all customers
            PreparedStatement pstmt = businessConnection.prepareStatement(
                    "SELECT NAME, USERNAME, PHONE, ADDRESS FROM CUSTOMERS"
            );
            ResultSet rs = pstmt.executeQuery();
            // Construct the customer as object and return
            while (rs.next()) {
                StringBuffer s = new StringBuffer();
                s.append("Name: ");
                s.append(rs.getString("NAME"));
                s.append(" | Username: ");
                s.append(rs.getString("USERNAME"));
                s.append(" | Phone: ");
                s.append(rs.getString("PHONE"));
                s.append(" | Address: ");
                s.append(rs.getString("ADDRESS"));
                customers.add(s.toString());
            }

        }
        catch (SQLException sqle) {
            customers = new ArrayList<String>();
            logger.warning("Error getting customers for dropdown.");
            customers.add("Database Error getting customers for dropdown");
        }
        return customers;
    }

 
    /** Gets the type of the user with given username as an enum UserType.
      * Type may be NON_EXISTANT, CUSTOMER, BUSINESS or SUPERUSER.
      * @param username The username to request type of
      * @return The UserType of the given username
      * @throws SQLException If there was a database error */
    public UserType getUserType(String username) throws SQLException {
        Statement stmt = generalConnection.createStatement();
        String queryStr = "SELECT COUNT(USERNAME) FROM %s WHERE USERNAME='%s'";
        
        String[] userTypes = {"BUSINESS", "SUPERUSER"};
        /* Check for business or superuser */
        for (String userType : userTypes) {
            ResultSet rs = stmt.executeQuery(
                String.format(queryStr, userType, username)
            );
            rs.next();
            if (rs.getInt(1) == 1) {
                if (userType.equals("BUSINESS")) {
                    return UserType.BUSINESS;
                }
                else if (userType.equals("SUPERUSER")) {
                    return UserType.SUPERUSER;
                }
            }
        }
        /* Check for customer - if not customer, then non-existant */
        ResultSet rs = stmt.executeQuery(
            String.format(queryStr, "CREDENTIALS", username)
        );
        rs.next();
        if (rs.getInt(1) == 1) { return UserType.CUSTOMER; }
        else { return UserType.NON_EXISTANT; }
    }

    /** Gets all of the appointments in the system within the date range of
     *  7 days starting from today
     *  @return An ArrayList of Appointment objects representing all the 
     *  appointments within the date range.
     *  @throws SQLException If a general database error occurs
     */
    public ArrayList<Appointment> getThisWeeksAppointments()
        throws SQLException
    {
        ArrayList<Appointment> appointments = new ArrayList<Appointment>();
        Statement stmt = businessConnection.createStatement();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        String dateStr = String.format("DATE '%04d-%02d-%02d'",
                 cal.get(Calendar.YEAR),
                 cal.get(Calendar.MONTH)+1,
                 cal.get(Calendar.DAY_OF_MONTH)
                );

        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM Appointment "
            +"WHERE ("
            +"    date_and_time >= "+dateStr
            +"    AND"
            +"    date_and_time <= "+dateStr+" + INTERVAL '7' DAY"
            +") "
        );
        while (rs.next()) {
            try {
                appointments.add(
                    new Appointment(
                        new Date(rs.getTimestamp("DATE_AND_TIME").getTime()),
                        rs.getInt("APPOINTMENT_TYPE"),
                        rs.getLong("EMPLOYEE"),
                        getCustomer(rs.getString("CUSTOMER"))
                    )
                );
            }
            catch (SQLException sqle) {
                logger.warning(
                    "Error getting appointment. Error code: "
                        +sqle.getErrorCode()
                ); 
            }
        }
        return appointments;
    }

    /** Attempts to save the appointment
      * @param cust The customer to ask for a booking
      * @param time The time to ask for a booking
      * @return Whether the booking could be made. This depends on whether
      * there is a free employee at the given time.
      * @throws SQLException If a database error occurred
      */
    public boolean saveAppointment(Appointment apt)
        throws SQLException
    {
        if (businessConnection == null || businessConnection.isClosed()) {
            throw new SQLException("Not connected to a business");
        }

        // Get the assigned employee
        Employee emp = getEmployee(apt.getEmployeeID());
        System.out.println("Employee got: "+emp);
        // If they are not free at appointment time, reject
        if (emp.appointmentHours.contains(apt.getDate())) {
            System.out.println("DBM:saveAppointment: Returning false because"
                + " employee has appointment at given time");
            return false;
        }

        // Add the appointment to the database
        PreparedStatement pstmt = businessConnection.prepareStatement(
            "INSERT INTO APPOINTMENT "
           +"VALUES (default, ?, ?, ?, ?)"
        );
        pstmt.setTimestamp(1, new java.sql.Timestamp(apt.getDate().getTime()));
        pstmt.setInt(2, apt.getAppointmentType());
        pstmt.setLong(3, apt.getEmployeeID());
        pstmt.setString(4, apt.getCustomer().username);
        try {
            pstmt.execute();
        } catch (SQLIntegrityConstraintViolationException sqlie) {
            // Return false e.g. if employee, appointmentType were bogus
            // SQLException is meant to indicate error, but this is expected
            // behaviour for referential integrity
            return false;
        }

        businessConnection.commit();

        return true;
    }

    /** Returns the availability of employees for these 7 days.
      * @param distinct If set to true, do not return duplicate
      * WeekDates if there is more than one employee available at a given time
      * @return ArrayList<WeekDate> representing the availability of all
      * employees, optionally containing duplicates if
      * called with distinct = false
      */ 
    public ArrayList<WeekDate>
    getSevenDayEmployeeAvailability(boolean distinct)
        throws SQLException
    {
        if (businessConnection == null || businessConnection.isClosed()) {
            throw new SQLException("Not connected to a business");
        }

        ArrayList<WeekDate> availableDates = new ArrayList<WeekDate>();
        Statement stmt = businessConnection.createStatement();
        ResultSet rs;
        try {
            rs = stmt.executeQuery (
                "SELECT "+(distinct ? "DISTINCT " : "")
                +"AVAILABLE_DAY, AVAILABLE_TIME "
                +"FROM AVAILABILITY "
                +"ORDER BY AVAILABLE_DAY, AVAILABLE_TIME "
            );
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
            throw sqle;
        }

        while (rs.next()) {
            availableDates.add(
                new WeekDate(DayOfWeek.of(rs.getInt(1)), rs.getInt(2))
            );
        }

        System.out.println("Available dates:"+availableDates);
        System.out.flush();

        return availableDates;
    }

    /** Gets an ArrayList containg all the names and IDs of all employees
      * as preformatted Strings.
      * @return All employees listed in the form name+" #"+employee_Id
      * e.g. Joe Blogs #9
      */
    public ArrayList<String> getEmployeeNameIDs() throws SQLException {
        if (businessConnection == null || businessConnection.isClosed()) {
            throw new SQLException("Not connected to a business");
        }

        ArrayList<String> emplNames = new ArrayList<String>();
        Statement stmt = businessConnection.createStatement();
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(
                "SELECT EMPL_NAME, EMPL_ID FROM EMPLOYEE"
            );
        } catch (SQLException sqle) {
            logger.warning("SQL Error getting employee names and IDs:");
            sqle.printStackTrace();
            throw sqle;
        }

        while(rs.next()) {
            emplNames.add(rs.getString("EMPL_NAME") +" #"+ rs.getString("EMPL_ID"));
        }

        return emplNames;
    }

    /** Gets the employee with the given employee id as an object
      * @param empl_id The employee id
      * @return The employee as an object with associated working
      * hours and appointment hours
      */
    public Employee getEmployee(long empl_id) throws SQLException {
        if (businessConnection == null || businessConnection.isClosed()) {
            throw new SQLException("Not connected to a business");
        }

        // Data needed for an employee
        Employee employee;
        String empl_name;
        ArrayList<WeekDate> available_hours = new ArrayList<WeekDate>();
        ArrayList<Appointment> appointments = new ArrayList<Appointment>();

        Statement stmt = businessConnection.createStatement();
        ResultSet rs = null;

        // Get name
        try {
            rs = stmt.executeQuery(
                "SELECT EMPL_NAME FROM EMPLOYEE WHERE EMPL_ID = "+empl_id
            );

            rs.next();
            empl_name = (rs.getString(1));
        } catch (SQLException sqle) {
            // Employee does not exist, ignore error
            throw sqle;
        }

        // Get working hours
        try {
            available_hours = getEmployeeAvailability((int)empl_id);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw sqle;
        }

        // Get appointment hours
        try {
            rs = stmt.executeQuery (
                "SELECT DATE_AND_TIME,APPOINTMENT_TYPE,EMPLOYEE, CUSTOMER "
               +"FROM APPOINTMENT "
               +"WHERE EMPLOYEE="+empl_id
               );

            while (rs.next()) {
                logger.fine("Found appointment date: "+rs.getDate(1));
                Date currAptDate = new Date(rs.getTimestamp(1).getTime());
                appointments.add(
                    new Appointment (
                        currAptDate, //DATE_AND_TIME
                        rs.getInt(2), // APPOINTMENT_TYPE
                        rs.getLong(3), // EMPLOYEE
                        getCustomer(rs.getString(4)) // CUSTOMER
                    )
                );
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw sqle;
        }

        return new Employee(empl_id, empl_name, available_hours, appointments);
    }

    /** Gets the given employee's available days and times on the roster
      * You may want to just fetch the whole Employee with getEmployee
      * @return ArrayList<WeekDate> representing the availability
      * @throws SQLException if the employee doesn't exist, or a database
      * error occurs
     */
    public ArrayList<WeekDate> getEmployeeAvailability(long employeeID)
        throws SQLException
    {
        if (businessConnection == null || businessConnection.isClosed()) {
            throw new SQLException("Not connected to a business");
        }

        Statement stmt = businessConnection.createStatement();
        ResultSet rs = null;
        ArrayList<WeekDate> availWeekDates = new ArrayList<WeekDate>();

        try {
            rs = stmt.executeQuery (
                "SELECT AVAILABLE_DAY, AVAILABLE_TIME "+
                "FROM EMPLOYEE EMP JOIN AVAILABILITY AVA "+
                "ON EMP.EMPL_ID = AVA.EMPLOYEE "+
                "WHERE EMPL_ID = "+employeeID
            );

            while (rs.next()) {
                WeekDate wd = new WeekDate(
                        DayOfWeek.of(rs.getInt(1)), rs.getInt(2)
                );
                System.out.println("Found available WeekDate: "+wd);
                availWeekDates.add(wd);
            }
        } catch (SQLException sqle) {
            logger.warning("SQL Error in getEmployeeAvailability:");
            sqle.printStackTrace();
            throw new SQLException("Cannot get available WeekDates (empl_id = )"
                    +employeeID);
        }

        return availWeekDates;
    }

    /** Adds an employee with the given name. The ID is generated automatically.
      * To get the new employee as an object, use getEmployee after calling this
      * method.
      * @param name The name of a new employee to add
      * @return The new ID of the employee if successful, or -1 for failure
      * @throw SQLException If a database error occurs
    */
    public long addEmployee(String name) throws SQLException {
        if (businessConnection == null || businessConnection.isClosed()) {
            throw new SQLException("Not connected to a business");
        }

        // Insert an employee with the given name
        Statement stmt = businessConnection.createStatement();
        try {
            stmt.execute (
                "INSERT INTO EMPLOYEE (EMPL_ID, EMPL_NAME) "
                +"VALUES (default, '"+name+"')"
            );
        } catch (SQLException sqle) {
            logger.severe(
                "Error creating employee(name="+name+"):"
            );
            sqle.printStackTrace();
            return -1;
        }

        // Return max ID
        long maxID;
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery (
                "SELECT MAX(EMPL_ID) FROM EMPLOYEE"
            );
            rs.next();
            maxID = rs.getLong(1);
        } catch (SQLException sqle) {
            logger.severe(
                "Error getting max employee_id (EMPL_ID) from database:"
            );
            sqle.printStackTrace();
            // It was added, but we can't know the ID, so throw an exception
            throw sqle;
        }

        return maxID;
    }

    /** Tries to update the given employee's information in the database.
      * @param employee The employee object representing the new data.
      * @return Whether the update was successful.
      */
    public boolean updateEmployee(Employee employee) throws SQLException {
        if (businessConnection == null || businessConnection.isClosed()) {
            throw new SQLException("Not connected to a business");
        }

        businessConnection.commit();
        int updateCount = 0;
        Statement stmt = businessConnection.createStatement();

        /* Update name */
        try {
            updateCount = stmt.executeUpdate(
                "UPDATE EMPLOYEE"
                +" SET EMPL_NAME = '"+employee.name+"'"
                +" WHERE EMPL_ID = "+employee.id
            );

        } catch (SQLException sqle) {
            logger.severe("Error updating employee name for:"+employee
            +". Rolling back.");
            sqle.printStackTrace();
            businessConnection.rollback();
        }

        /* Remove all availability for this employee - so we can add all given 
           availability. Rollback will save us if we fail */
        for (WeekDate currAvailability : employee.workingHours) {
            Date availDate = DayOfWeekConversion.wd2cal(currAvailability).getTime();
            if (employee.appointmentHours.contains(availDate)) {
                System.err.println("Can't remove availability for employee:"
                    +employee.name+"#"+employee.id+" at "+availDate+". "
                    +"They have an appointment at that time"
                );
            }
            else {
                // TODO
            }
                /*
                throw new SQLIntegrityConstraintViolationException(
                    "Cannot remove employee when they have an appointment"
                );*/
        }

        try {
            stmt.execute("DELETE FROM AVAILABILITY"
            +" WHERE EMPLOYEE="+employee.id);
        } catch (SQLException sqle) {
            System.err.println(
                "DatabaseManager: Failed to delete from availability, empl_id="
                    +employee.id+". Rolling back."
            );
            businessConnection.rollback();
        }

        /* Update availability - insert all availability */
        PreparedStatement pstmt;
        for (WeekDate currDate : employee.workingHours) {
            try {
                pstmt = businessConnection.prepareStatement(
                    "INSERT INTO AVAILABILITY VALUES (?, ?, ?)"
                );
                pstmt.setLong(1, employee.id);
                pstmt.setInt(2, currDate.getTime());
                pstmt.setInt(3, currDate.getDayOfWeek().getValue());
                updateCount += pstmt.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException sqlie) {
                // Already exists, ignore it
            } catch (SQLException sqle) {
                // Something bad actually happened
                System.err.println(
                    "DatabaseManager: Failed to insert availability "
                    +"for empl_id:"+employee.id+". Rolling back."
                );
                sqle.printStackTrace();
                businessConnection.rollback();
            }
        }

        /* Update appointments? - no, do that seperately  
            Remember: appointments belong in a separate table and are returned 
            to the Employee object at runtime, but only for convenience
            of access */

        // Return true even if no updates occurred - it still means we
        // guarantee the record is correct
        businessConnection.commit();
        return true;
    }

    /** Deletes the employee and their appointments and availability from the
      * database, forcing if necessary.
      * The employee will not be permanently deleted until save.
      * @param employeeID The ID of the employee to delete
      * @param force If true, delete the employee even if they have appointments
      * they have not yet completed
      * @return Whether the employee could be deleted. False indicates that they
      * still have appointments in the future
      * @throws SQLException If a database error occurs
      */
    public boolean deleteEmployee(Employee employee, boolean force)
        throws SQLException
    {
        System.out.println("Deleting employee #"+employee.id+". force="+force);
        if (businessConnection == null || businessConnection.isClosed()) {
            throw new SQLException("Not connected to a business");
        }
        Statement stmt = businessConnection.createStatement();
        try {
            stmt.execute("DELETE FROM EMPLOYEE WHERE EMPL_ID="+employee.id);
        }
        // Employee still has appointments, availability in the system
        catch (SQLIntegrityConstraintViolationException sqlie) {
            if (force) {
                // Delete regardless
                stmt.execute(
                    "DELETE FROM APPOINTMENT WHERE EMPLOYEE=0; "+
                    "DELETE FROM AVAILABILITY WHERE EMPLOYEE=0; "+
                    "DELETE FROM EMPLOYEE WHERE EMPL_ID=0"
                );
                return true;
            }

            // Not forcing, only delete if we don't have any future appointments
            Date currDate = new Date();
            boolean dateInFuture = false;
            for (Date aptDate : employee.appointmentHours) {
                // Convert WeekDate to absolute date
                System.out.println("Comparing dates "+aptDate
                    +" and "+currDate);
                System.out.println("aptDate.compareTo(currDate)="
                    +aptDate.compareTo(currDate));
                if (aptDate.compareTo(currDate) > 0) {
                    dateInFuture = true;
                    break;
                }
            }
            if (!dateInFuture) {
                // Delete regardless
                stmt.execute(
                    "DELETE FROM APPOINTMENT WHERE EMPLOYEE=0; "+
                    "DELETE FROM AVAILABILITY WHERE EMPLOYEE=0; "+
                    "DELETE FROM EMPLOYEE WHERE EMPL_ID=0"
                );
                return true;
            }
            else {
                // Inform caller we cannot delete
                return false;
            }
        }
        commit();
        return true;
    }
    /** @deprecated Marks the employee available or unavailable at the given dates and times
     *  @param employeeID The ID of the employee to mark availabilty for
     *  @param dates An ArrayList of dates representing times the employee is
     *  available for
     *  @param available An ArrayList of same size as dates, representing the
     *  whether the employee is available at the given date
     *  THIS METHOD IS DEPRECATED, please use updateEmployee
     */
    public boolean setEmployeeAvailability(int employeeID,
        ArrayList<WeekDate> dates,
        ArrayList<Boolean> availability)
        throws SQLException, DateTimeException
    {
        if (businessConnection == null || businessConnection.isClosed()) {
            throw new SQLException ("The business connection is closed.");
        }

        int resultUpdates = 0;
        WeekDate givenWeekDate;
        for (int dateIdx = 0; dateIdx < dates.size(); ++dateIdx) {
            givenWeekDate = dates.get(dateIdx);
            // Available
            if (availability.get(dateIdx) == true) {
                PreparedStatement pstmt = businessConnection.prepareStatement(
                    "INSERT INTO AVAILABILITY VALUES(?, ?, ?)"
                );
                pstmt.setInt(1, employeeID);
                pstmt.setInt(2, givenWeekDate.getTime());
                pstmt.setInt(3, givenWeekDate.getDayOfWeek().getValue());
                try {
                    resultUpdates += pstmt.executeUpdate();
                } catch (SQLException sqle) {
                    System.err.println("DatabaseManager: Error inserting appointment");
                    System.err.println("date="+givenWeekDate.toString()
                            + ";empl_id=" + employeeID);
                    sqle.printStackTrace();
                    throw sqle;
                }
            }
            // Unavailable
            else {
                PreparedStatement pstmt = businessConnection.prepareStatement(
                    "DELETE FROM AVAILABILITY "+
                    "WHERE EMPLOYEE = ? AND AVAILABLE_TIME = ?"
                );
                pstmt.setInt(1, employeeID);
                pstmt.setTimestamp(2, new java.sql.Timestamp(givenWeekDate.getTime()));
                System.out.format("Deleting values(%d,%s)\n", employeeID,
                        givenWeekDate.toString());
                try {
                    resultUpdates += pstmt.executeUpdate();
                    System.out.println("resultUpdates:"+resultUpdates);
                } catch (SQLException sqle) {
                    System.err.println("DatabaseManager: Error deleting appointment");
                    System.err.println("date="+givenWeekDate.toString() 
                            + ";empl_id=" + employeeID);
                    sqle.printStackTrace();
                    throw sqle;
                }
            }

        }

        System.out.println("resultUpdates:"+resultUpdates);
        return (resultUpdates > 0 ? true : false);
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

    private boolean scannerSaveAppointment(Scanner sc) throws SQLException {
        // Create a new Calendar at the start of the requested hour, on Monday.
        System.out.println("Making appointment for this Monday. Which hour?");
        int hour = Integer.parseInt(sc.next());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date dateAndTime = cal.getTime();

        System.out.println("Date and time made: "+dateAndTime);
        System.out.println("Default customer got as:"
                +getCustomer("default_customer"));

        // Try booking a new appointment at the given hour with employee 0, of
        // type 0, as the default customer
        Appointment apt = new Appointment (
            dateAndTime, 0, 0, getCustomer("default_customer")
        );
        return saveAppointment(apt);
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
            System.out.print("DBM > ");
            input = sc.next();
            sc.nextLine();
            int employee;
            switch (input) {
                case "add_employee":
                    System.out.print("New employee name: ");
                    long result = dbm.addEmployee(sc.next());
                    System.out.println();
                    System.out.println(
                        result == -1 ?
                        "Adding employee failed"
                        : "Adding employee succeeded! New ID: "+result
                    );
                    break;
                case "delete_employee":
                    System.out.print("Employee ID: ");
                    Employee emp =
                        dbm.getEmployee(Long.parseLong(sc.next()));
                    if (!dbm.deleteEmployee(emp, false)) {
                        System.out.println("Force deletion? ");
                        if (sc.nextBoolean()) {
                            System.out.println(
                                "Success="+dbm.deleteEmployee(emp, true)
                            );
                        }
                    }
                    else {
                        System.out.println(
                            "Sucessfully deleted useless employee."
                        );
                    }
                    break;
                case "check":
                    dbm.scannerCheckUser(sc);
                    break;
                case "quit":
                    dbm.close();
                    return;
                case "business":
                    String business = sc.next();
                    boolean success = false;
                    try {
                        success = dbm.connectToBusiness(business);
                    } catch (SQLException sqle) {
                        System.out.println("Couldn't connect to business:"+business);
                        sqle.printStackTrace();
                    }
                    System.out.println(
                        (success ? "Successfully connected " : "Did not successfully connect ")
                        +("to business:"+business+"\n")
                    );
                    break;
                case "get_availability":
                    employee = sc.nextInt();
                    ArrayList<WeekDate> availability = null;
                    try {
                        availability = dbm.getEmployeeAvailability(1);
                    } catch (SQLException sqle) {
                        System.out.println("Couldn't get availability for employee:"+employee);
                        sqle.printStackTrace();
                        break;
                    }
                    for (WeekDate d : availability) {
                        System.out.println(d);
                    }
                    break;
                case "7days":
                    ArrayList<WeekDate> avail =
                        dbm.getSevenDayEmployeeAvailability(false);
                    break;
                case "customer":
                    System.out.println(dbm.getCustomer(sc.next()));
                    break;
                case "customers":
                    System.out.println(dbm.getAllCustomers());
                    break;
                case "save_appointment":
                    System.out.println("Sucess="+Boolean.toString(dbm.scannerSaveAppointment(sc)));
                    break;
                case "cust_dropdown":
                    System.out.println(dbm.getCustomerInfoForDropDown());
                    break;
                case "check_available":
                    {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    cal.set(Calendar.HOUR_OF_DAY, 10);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    Date reqDate = cal.getTime();
                    ArrayList<WeekDate> weekAvailability =
                        dbm.getSevenDayEmployeeAvailability(false);
                    ArrayList<Appointment> weekAppointments =
                        dbm.getThisWeeksAppointments();

                    // Check if seven day availability has this date
                    // Generate the Date for all availability
                    boolean cont = false;
                    int availIdx;
                    for (availIdx = 0; availIdx < weekAvailability.size(); ++availIdx) {
                        Date queryDate =
                            DayOfWeekConversion.wd2cal(
                                weekAvailability.get(availIdx)
                            ).getTime();
                        System.out.println("Compare converted date: "+queryDate);
                        if (queryDate.equals(reqDate)) {
                            cont = true;
                            break;
                        }
                    }
                    // Quit if weekAvailability contains no matching date
                    if (!cont) { System.out.println("No matches. Quitting early.\nfalse"); break; }

                    // Count how many employees are free at reqDate
                    int freeEmpCount;
                    for (freeEmpCount = 0; availIdx < weekAvailability.size();
                        ++availIdx, ++freeEmpCount)
                    {
                        if (DayOfWeekConversion.wd2cal(
                                weekAvailability.get(availIdx)
                            ).getTime().equals(reqDate))
                        {
                            continue;
                        } else break;
                    }

                    // Count how many appointments are occuring at reqDate
                    // FIXME: THIS IS STARTING FROM THE START OF APPOINTMENTS
                    // AND BREAKING IMMEDIATELY. FIRST PARSE FROM THE START OF THE ARRAY
                    // UNTIL WE FIND IT, THEN BREAK WHEN WE ARE DONE

                    // Find the array index of the first appointment
                    int aptCount = 0;
                    int aptIdx;
                    for (aptIdx = 0; aptIdx < weekAppointments.size(); ++aptIdx) {
                        // Get the date
                        Date aptDate = weekAppointments.get(aptIdx).getDate();
                        System.out.println("aptDate="+aptDate);
                        System.out.println("reqDate="+reqDate);
                        if (aptDate.equals(reqDate)) {
                            ++aptCount;
                            break;
                        } else continue;
                    }

                    // Starting from the second appointment, keep adding until
                    // we reach something else or end of array
                    for (++aptIdx; aptIdx < weekAppointments.size(); ++aptIdx) {
                        // Get the date
                        Date aptDate = weekAppointments.get(aptIdx).getDate();
                        System.out.println("aptDate="+aptDate);
                        System.out.println("reqDate="+reqDate);
                        if (aptDate.equals(reqDate)) {
                            ++aptCount;
                            continue;
                        } else break;
                    }

                    // If there are more availabilities than appointments occuring, the
                    // time slot must be bookable (but we don't know who the employee
                    // will be)
                    System.out.println("freeEmpCount="+freeEmpCount);
                    System.out.println("aptCount="+aptCount);
                    if (freeEmpCount - aptCount > 0) {
                        System.out.println("Yea");
                    } else System.out.println("Nay");

                    }
                    break;
                /*case "set_availability":
                    employee = sc.nextInt();
                    ArrayList<Date> availableDates = new ArrayList<Date>();
                    Date today00 = new Date();
                    /*today00.setHours(0);
                    today00.setHours(today00.getHours()+10);
                    today00.setMinutes(0);
                    today00.setSeconds(0);*//*
                    availableDates.add(today00);
                    ArrayList<Boolean> availabilities = new ArrayList<Boolean>();
                    availabilities.add(sc.nextBoolean());
                    try {
                        dbm.setEmployeeAvailability(employee, availableDates, availabilities);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "reset_availability":
                    employee = sc.nextInt();
                    availability = null;
                    try {
                        availability = dbm.getEmployeeAvailability(1);
                    } catch (SQLException sqle) {
                        System.out.println("Couldn't get availability for employee:"+employee);
                        sqle.printStackTrace();
                        break;
                    }
                    /*ArrayList<Boolean>*//* availabilities = new ArrayList<Boolean>();
                    for (WeekDate d : availability) {
                        System.out.println(d);
                        availabilities.add(false);
                    }
                    employee = sc.nextInt();
                    availabilities.add(sc.nextBoolean());
                    try {
                        dbm.setEmployeeAvailability(employee, availability, availabilities);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                */
                case "default_business":
                    System.out.println(dbm.getBusiness(defaultBusinessName));
                    break;
                default:
                    System.out.println("Not a valid command. Received:"+input);
            }
        }
    }
}
