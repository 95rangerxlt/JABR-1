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
import java.util.Scanner;
import java.util.Date;

// Util imports from this project
import org.jabst.jabs.util.Digest;
import org.jabst.jabs.util.DayOfWeekConversion;

// For returning result sets as native objects
import java.util.ArrayList;

// Time imports
import java.util.Calendar;
import java.time.DateTimeException;
import java.time.DayOfWeek;

public class DatabaseManager {
    private static final String dbfilePrefix = "jdbc:hsqldb:file:";
    private static final String[] SQL_TABLES_GENERAL = {
        "CREATE TABLE CREDENTIALS ("
            +"USERNAME VARCHAR(20),"
            +"PASSWORD VARBINARY(32) NOT NULL,"
            +"PRIMARY KEY(USERNAME))",
        "CREATE TABLE CUSTOMERS ("
            +"USERNAME VARCHAR(20),"
            +"NAME VARCHAR(40) NOT NULL,"
            +"ADDRESS VARCHAR(255) NOT NULL,"
            +"PHONE VARCHAR(10) NOT NULL,"
            +"PRIMARY KEY(USERNAME),"
            +"FOREIGN KEY (USERNAME) REFERENCES CREDENTIALS(USERNAME));",
        "CREATE TABLE BUSINESS ("
            +"USERNAME VARCHAR (40),"
            +"BUSINESS_NAME VARCHAR(40) NOT NULL,"
            +"OWNER_NAME VARCHAR(40) NOT NULL,"
            +"ADDRESS VARCHAR(255) NOT NULL,"
            +"PHONE VARCHAR(10) NOT NULL,"
            +"PRIMARY KEY (USERNAME))",
            
        // Default data
        // password = default
        "INSERT INTO CREDENTIALS VALUES('default_business','37a8eec1ce19687d132fe29051dca629d164e2c4958ba141d5f4133a33f0688f')",
        "INSERT INTO CREDENTIALS VALUES('default_customer','37a8eec1ce19687d132fe29051dca629d164e2c4958ba141d5f4133a33f0688f')",
        "INSERT INTO BUSINESS VALUES('default_business', 'default business', 'default_owner', 'default_addr', '0420123456')",
        "INSERT INTO CUSTOMERS VALUES('default_customer','default customer','default','0420123546')"
    };
    private static final String[] SQL_TABLES_BUSINESS = {
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
        "INSERT INTO EMPLOYEE VALUES (DEFAULT, 'default_employee', 'default', '0420123456')",
        "INSERT INTO APPOINTMENTTYPE VALUES (DEFAULT, 'DEFAULT_APPOINTMENT_TYPE', 99);",
        "INSERT INTO AVAILABILITY VALUES (0, 36000, 1)",
        "INSERT INTO AVAILABILITY VALUES (0, 32400, 1)",
        "INSERT INTO APPOINTMENT VALUES (DEFAULT, DATEADD('dd', %d, CURDATE)+INTERVAL '9' HOUR, 0, 0, 'default_customer')",
        "INSERT INTO APPOINTMENT VALUES (DEFAULT, DATEADD('dd', %d, CURDATE)+INTERVAL '10' HOUR, 0, 0, 'default_customer')"
    };
    
    public static final String dbDefaultFileName = "db/credentials_db";
    public static final String defaultBusinessName = "default_business";
    private Connection generalConnection;
    private Connection businessConnection;
    
    /** Creates a new DatabaseManager
     * Always open the DatabaseManager at program start (call the constructor),
     * and close it at program finish ( see: close() )
     * @param The name of the file to open
     * @throws HsqlException, SQLException
     */
    public DatabaseManager(String dbfile) throws HsqlException, SQLException {
         this.generalConnection = openCreateDatabase(dbfile, SQL_TABLES_GENERAL);
         if (generalConnection == null) {
             throw new SQLException();
         }
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
        cal.setTime(new Date());
        int dow = cal.get(Calendar.DAY_OF_WEEK);

        // Convert to Mon-Sun form because that is the form used in WeekDate
        dow = DayOfWeekConversion.cal2dow(dow);

        // Format the day of week into the SQL
        SQL_TABLES_BUSINESS[8] = String.format(SQL_TABLES_BUSINESS[8], dow);
        SQL_TABLES_BUSINESS[9] = String.format(SQL_TABLES_BUSINESS[9], dow);

        boolean success = false;
        for (String currTable : tables) {
            Statement statement = null;
            try {
                statement = connection.createStatement();
            } catch (SQLException se) {
                System.err.println("Error creating statement for table");
                return false;
            }
            
            try {
                // Statement.execute returns false if no results were returned,
                // including for CREATE statements
                statement.execute(currTable);
                System.out.println("Successfully created table");
                success = true;
            } catch (SQLException se) {
                System.out.println("Failed to create table");
                return false;
            }
        }
        return success;
    }
    
    /** Asks the database to save the data it has now */
    public void commit() {
        try {
            generalConnection.commit();
            if (businessConnection != null && !businessConnection.isClosed()) {
                businessConnection.commit();
            }
        } catch (SQLException e) {
            System.err.println("DatabaseManager: Error commiting");
            e.printStackTrace();
        }
    }

    /** Closes the database connection associated with the manager
        You MUST do this, or data will not be saved on program exit
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
            System.err.println(
                "DatabaseManager: Error closing database properly. Continuing."
            );
        }
    }
    
    /** Tries to connect to the given database, and create it if it doesn't exist already
        @param dbFileName The name of the database file to connect to
        @param tables A string array of SQL statements to execute to make the tables in the
        new database
        @return A connection to the database if successful, otherwise null.
     */
    private Connection openCreateDatabase(String dbFileName, String[] tables) {
        Connection c = null;
         try {
             c = DriverManager.getConnection(dbfilePrefix+dbFileName+";ifexists=true", "sa", "");
         } catch (HsqlException hse) {
             System.err.println("HqlException conecting to database'"+dbFileName+"': Doesn't exist");
         }

         catch (SQLException se) {
            try {
                c = DriverManager.getConnection(dbfilePrefix+dbFileName, "sa", "");
            } catch (SQLException sqle) {
                System.err.println(
                    "DriverManager: Error: Cannot connect to general database file (SQL error) (when trying to open new)"
                );
            }
            if (!createTables(c, tables)) {
                System.err.println(
                    "DriverManager: Error: Cannot create tables in database'"+dbFileName+"'"
                 );
            }
         }
        return c;
    }
    
    /** Opens the default business database */
    public boolean connectToBusiness() throws SQLException {
        return connectToBusiness(defaultBusinessName);
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
            "SELECT COUNT(USERNAME) FROM BUSINESS WHERE USERNAME='"+busUsername+"'"
            );
        rs.next();
        switch(rs.getInt(1)) {
            case 0:
                return false;
            case 1:
                break;
            default:
                throw new AssertionError("Found 2 of business "+busUsername);
                // Never happens
        }
        
        // We now know it exists for certain, but not whether it has a database
        // Open or create the business' database
        this.businessConnection = openCreateDatabase("db/"+busUsername, SQL_TABLES_BUSINESS);
        if (this.businessConnection == null) {
            return false;
        }
        return true;
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
    
    /** Asks the database to check if there is a user with the given
      * username and password
      */
    public boolean checkUser (String username, String password)
        throws SQLException {
        byte[] password_hash = Digest.sha256(password);
        boolean success = false;

        PreparedStatement statement = generalConnection.prepareStatement(
            "SELECT * from CREDENTIALS WHERE USERNAME='"+username+"'"
        );

        ResultSet rs = statement.executeQuery(); 
        while (rs.next()) {
            String result_username = rs.getString("username");
            byte[] result_password = rs.getBytes("password");
            System.out.format("Input: username,password = %s,%s\n",
                username, Digest.digestToHexString(password_hash)
            );
            System.out.format("Result:username,password = %s,%s\n",
                result_username, Digest.digestToHexString(result_password)
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
    private void addUser(String username, String password)
        throws SQLException {

        byte[] password_hash = Digest.sha256(password);
        PreparedStatement statement = null;

        statement = generalConnection.prepareStatement(
            "INSERT INTO CREDENTIALS VALUES (?, ?)"
        );

        statement.setString(1, username);
        statement.setBytes(2, password_hash);

        System.out.println("About to execute adding user...");
        statement.execute();

        statement.close();
        // After adding a user, they need to be able to log in again
        generalConnection.commit();
    }

    /** Adds a user with the given arguments
        @param username Must be no longer than 40 characters
        @param password No size limit
        @param name Must be no longer than 40 character
        @param address Must be no longer than 255 characters
        @param phone Must be no longer than 10 characters (no international numbers)
        @throws SQLException if the database size constraints were exceeded
      */
    public void addUser(String username, String password,
        String name, String address, String phone) throws SQLException
    {
        // Add to credentials table
        addUser(username, password);
        
        // Now add to customers table
        PreparedStatement statement = generalConnection.prepareStatement(
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
    
    /** Checks if there is a business with the given username
        @param username The username to check
        @return Whether the username represents a business or not
        @throws SQLException If the database encountered an error
      */
    public boolean isBusiness(String username) throws SQLException {
        // NYI: Check if in Business(name)
        Statement stmt = generalConnection.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT COUNT(USERNAME) FROM BUSINESS WHERE USERNAME='"+username+"'"
        );
        
        rs.next();
        switch(rs.getInt(1)) {
            case 0:
                return false;
            case 1:
                return true;
            default:
                throw new AssertionError(
                    "Found more than one business with username="+username
                    );
        }
    }
    
    
    /** Gets all of the appointments in the system within the date range of
     *  7 days starting from today
     *  @return An ArrayList of Appointment objects representing all the 
     *  appointments within the date range.
     */
    public ArrayList<Appointment> getThisWeeksAppointments() throws SQLException {
        ArrayList<Appointment> appointments = new ArrayList<Appointment>();
        Statement stmt = businessConnection.createStatement();
        ResultSet rs = stmt.executeQuery(            "SELECT * FROM Appointment"
            +"WHERE ("
            +"    date_and_time >= DATE_SUB(CURDATE(),  DAYOFWEEK(CURDATE())-1)"
            +"    AND"
            +"    date_and_time <= DATE_SUB(CURDATE(),  DAYOFWEEK(CURDATE())-1) + INTERVAL '7' DAY"
            +")"
            +"ORDER BY DATE_AND_TIME"
        );
        while (rs.next()) {
            try {
                appointments.add(
                    new Appointment(
                        rs.getDate("DATE_AND_TIME"),
                        rs.getInt("APPOINTMENT_TYPE"),
                        rs.getInt("EMPLOYEE"),
                        rs.getString("CUSTOMER")
                    )
                );
            }
            catch (SQLException sqle) {
                System.err.println(
                    "Error getting appointment. Error code: "+sqle.getErrorCode()
                ); 
            }
        }
        return appointments;
    }
    
    public ArrayList<WeekDate> getSevenDayEmployeeAvailability() throws SQLException{
        if (businessConnection == null || businessConnection.isClosed()) {
            throw new SQLException("Not connected to a business");
        }
        
        ArrayList<WeekDate> availableDates = new ArrayList<WeekDate>();
        Statement stmt = businessConnection.createStatement();
        ResultSet rs = stmt.executeQuery (
            "SELECT DISTINCT AVAILABLE_DAY, AVAILABLE_TIME, FROM AVAILABILITY"
          +" WHERE AVAILABLE_TIME >= CURDATE"
          +" AND AVAILABLE_TIME <= CURDATE + INTERVAL '7' DAY"
        );
        
        while (rs.next()) {
            availableDates.add(
                new WeekDate(DayOfWeek.of(rs.getInt(1)), rs.getInt(2))
            );
        }
        
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
            System.err.println("SQL Error getting employee names and IDs:");
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
        ArrayList<Date> appointment_hours = new ArrayList<Date>();
        
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
            sqle.printStackTrace();
            throw sqle;
        }
            
        // Get times
        try {
            rs = stmt.executeQuery (
                "SELECT AVAILABLE DAY, AVAILABLE_TIME "+
                "FROM EMPLOYEE EMP JOIN AVAILABILITY AVA "+
                "ON EMP.EMPL_ID = AVA.EMPLOYEE "+
                "WHERE EMPL_ID = "+empl_id
            );
            
            while (rs.next()) {
                System.out.println("Found available date: "+rs.getDate(1));
                available_hours.add(new WeekDate(DayOfWeek.of(rs.getInt(1)), rs.getInt(2)));
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw sqle;
        }
        
        try {
            rs = stmt.executeQuery (
                "SELECT APT.DATE_AND_TIME"
                +" FROM EMPLOYEE EMP INNER JOIN APPOINTMENT APT"
                +" ON EMP.EMPL_ID = APT.EMPLOYEE"
                +" WHERE EMP.EMPL_ID = "+empl_id);

            while (rs.next()) {
            System.out.println("Found appointment date: "+rs.getDate(1));
                appointment_hours.add(new Date(rs.getTimestamp(1).getTime()));
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw sqle;
        }
        
        return new Employee(empl_id, empl_name, available_hours, appointment_hours);
    }
    
    /** Gets the given employee's available dates for the next 7 days
      * @return ArrayList<Date> representing the availability
      * @throws SQLException if the employee doesn't exist, or a database
      * error occurs
     */
    public ArrayList<Date> getEmployeeAvailability(int employeeID) throws SQLException{
        ArrayList<Date> availDates = new ArrayList<Date>();
        if (businessConnection == null || businessConnection.isClosed()) {
            throw new SQLException("Not connected to a business");
        }
        Statement stmt = businessConnection.createStatement();
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(
                "SELECT AVAILABLE_TIME FROM AVAILABILITY "
                    +"WHERE EMPLOYEE="+employeeID
                    +" AND (AVAILABLE_TIME >= CURDATE) "
                    +" AND (AVAILABLE_TIME <= CURDATE() + INTERVAL '7' DAY)"
            );
        } catch (SQLException sqle) {
            System.err.println("SQL Error in getEmployeeAvailability:");
            sqle.printStackTrace();
            throw sqle;
        }
        while (rs.next()) {
            availDates.add(rs.getDate(1));
        }
        return availDates;
    }

    /** Adds an employee with the given name. The ID is generated automatically
        @param name The name of a new employee to add
        @return whether the database sucessfully added the employee
    */
    public boolean addEmployee(String name) throws SQLException {
        if (businessConnection == null || businessConnection.isClosed()) {
            throw new SQLException("Not connected to a business");
        }
        Statement stmt = businessConnection.createStatement();
        ResultSet rs = null;
        int maxId;
        try {
            rs = stmt.executeQuery (
                "SELECT MAX(EMPL_ID) FROM EMPLOYEE"
            );
            maxId = rs.getInt(1);
        } catch (SQLException sqle) {
            System.err.println(
                "Error getting max employee_id (EMPL_ID) from database:"
            );
            sqle.printStackTrace();
            throw sqle;
        }
        try {
            return stmt.execute (
                "INSERT INTO EMPLOYEE (EMPL_ID, EMPL_NAME) "
               +"VALUES ("+maxId+", '"+name+"')"
            );
        } catch (SQLException sqle) {
            System.err.println(
                "Error creating employee(name=)"+name+"):"
            );
            sqle.printStackTrace();
            throw sqle;
        }
    }
    
    public boolean updateEmployee(Employee employee) throws SQLException {
        if (businessConnection == null || businessConnection.isClosed()) {
            throw new SQLException("Not connected to a business");
        }
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
            System.err.println("Error updating employee:"+employee);
            sqle.printStackTrace();
        }
        
        /* Update availability / working hours */
        PreparedStatement pstmt;
        for (WeekDate currDate : employee.workingHours) {
            try {
                pstmt = businessConnection.prepareStatement(
                    "INSERT INTO AVAILABILITY VALUES (?, ?, ?)"
                );
                pstmt.setLong(1, employee.id);
                pstmt.setInt(2, currDate.getTime());
                pstmt.setInt(3, currDate.getDayOfWeek().ordinal());
                
                updateCount += pstmt.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException sqle) {
                // Already exists, ignore it
            }
        }
        
        if (updateCount > 0) {
            return true;
        }
        else {
            return false;
        }
    }
    
    /** Marks the employee available or unavailable at the given dates and times
     *  @param employeeID The ID of the employee to mark availabilty for
     *  @param dates An ArrayList of dates representing times the employee is
     *  available for
     *  @param available An ArrayList of same size as dates, representing the
     *  whether the employee is available at the given date
     */
    public boolean setEmployeeAvailability(int employeeID,
        ArrayList<Date> dates,
        ArrayList<Boolean> availability)
        throws SQLException, DateTimeException
    {
        if (businessConnection == null || businessConnection.isClosed()) {
            throw new SQLException ("The business connection is closed.");
        }
        
        int resultUpdates = 0;
        Date givenDate;
        for (int dateIdx = 0; dateIdx < dates.size(); ++dateIdx) {
            givenDate = dates.get(dateIdx);
            // Available
            if (availability.get(dateIdx) == true) {
                PreparedStatement pstmt = businessConnection.prepareStatement(
                    "INSERT INTO AVAILABILITY VALUES(?, ?)"
                );
                pstmt.setInt(1, employeeID);
                pstmt.setTimestamp(2, new java.sql.Timestamp(givenDate.getTime()));
                try {
                    resultUpdates += pstmt.executeUpdate();
                } catch (SQLException sqle) {
                    System.err.println("DatabaseManager: Error inserting appointment");
                    System.err.println("date="+givenDate.toString() + ";empl_id=" + employeeID);
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
                pstmt.setTimestamp(2, new java.sql.Timestamp(givenDate.getTime()));
                System.out.format("Deleting values(%d,%s)\n", employeeID, givenDate.toString());
                try {
                    resultUpdates += pstmt.executeUpdate();
                    System.out.println("resultUpdates:"+resultUpdates);
                } catch (SQLException sqle) {
                    System.err.println("DatabaseManager: Error deleting appointment");
                    System.err.println("date="+givenDate.toString() + ";empl_id=" + employeeID);
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
            int employee;
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
                    ArrayList<Date> availability = null;
                    try {
                        availability = dbm.getEmployeeAvailability(1);
                    } catch (SQLException sqle) {
                        System.out.println("Couldn't get availability for employee:"+employee);
                        sqle.printStackTrace();
                        break;
                    }
                    for (Date d : availability) {
                        System.out.println(d);
                    }
                    break;
                case "set_availability":
                    employee = sc.nextInt();
                    ArrayList<Date> availableDates = new ArrayList<Date>();
                    Date today00 = new Date();
                    /*today00.setHours(0);
                    today00.setHours(today00.getHours()+10);
                    today00.setMinutes(0);
                    today00.setSeconds(0);*/
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
                    /*ArrayList<Boolean>*/ availabilities = new ArrayList<Boolean>();
                    for (Date d : availability) {
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
                case "default_business":
                    System.out.println(dbm.getBusiness(defaultBusinessName));
                    break;
                default:
                    System.out.println("Not a valid command. Received:"+input);
            }
        }
    }
}
