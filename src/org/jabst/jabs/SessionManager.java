package org.jabst.jabs;

import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import org.hsqldb.HsqlException;

import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

public class SessionManager extends Application {
	// Fields
	private DatabaseManager dbm;
	private EmployeeManager employeeManager;
	private Logger logger;
	private ConsoleHandler ch;

	public enum Window {
		LOGIN, REGISTER, BUSINESSMENU, CUSTOMERMENU, SUPERUSERMENU, ADDEMPLOYEE, 
	}

	/** This now operates as the Main method.
	 *  This is the entry point into the program.
	 *  GUI windows are launched from here.
	 * @param primaryStage
	 */
	@Override
	public void start(Stage primaryStage) {
		Window currentWindow = Window.LOGIN;
		String username = "";
		String password = "";
		for(;;) {
			switch(currentWindow) {
				// open respective window based on previous input
				case LOGIN:
					LoginInfo lInfo = LoginGUI.display(this, username, password);
					username = lInfo.username;
					password = lInfo.password;
					if(lInfo.button == LoginInfo.Buttons.LOGIN) {
						// Open business menu for business,
						// customer menu for customer
						
						try {
							UserType userType = dbm.getUserType(username);
							if (userType == UserType.NON_EXISTANT) {
								// Loop around
								// TODO: Feedback
								currentWindow = Window.LOGIN;
							}
							else if (userType == UserType.CUSTOMER) {
								currentWindow = Window.CUSTOMERMENU;
							}
							else if (userType == UserType.BUSINESS) {
								currentWindow = Window.BUSINESSMENU;
							}
							else if (userType == UserType.SUPERUSER) {
								currentWindow = Window.SUPERUSERMENU;
							}
							
						} catch (SQLException sqle) {
							// TODO: Handle updating interface to show database error
							sqle.printStackTrace();
							currentWindow = Window.LOGIN;
						}
					}
					else if(lInfo.button == LoginInfo.Buttons.REGISTER) {
						// open register window
						currentWindow = Window.REGISTER;
					}
					else if (lInfo.button == LoginInfo.Buttons.CLOSE) {
						shutdown();
					}
					break;
				case REGISTER:
					RegisterInfo rInfo = RegisterGUI.display(this, username, password);
					username = rInfo.username;
					password = rInfo.password;
					if(rInfo.button == RegisterInfo.Buttons.REGISTER) {
						// open respective window
						load_database();
						currentWindow = Window.LOGIN;
					}
					else if(rInfo.button == RegisterInfo.Buttons.LOGIN) {
						// open register window
						currentWindow = Window.LOGIN;
					}
					break;
				case BUSINESSMENU:
					try {
						dbm.connectToBusiness(username);
					} catch (SQLException sqle) {
						// TODO: Visual cue
						logger.warning("Cannot connect to business:"+username);
						currentWindow = Window.LOGIN;
						break;
					}
					username = "";
					password = "";
					BusinessInfo bInfo = BusinessMenuGUI.display(this);
					if(bInfo.button == BusinessInfo.Buttons.LOGOUT) {
						currentWindow = Window.LOGIN;
					}
						break;
				case CUSTOMERMENU:
					try {
						// FIXME: Need to use arguments
						UserType type = dbm.getUserType(username);
						if (type == UserType.NON_EXISTANT) {
							logger.severe("User has no type:"+username+"!");
						}
						else if (type == UserType.CUSTOMER) {
							String cbn = dbm.getCustomerBusinessName(username);
							if (cbn == null) {
								// TODO: Visual cue
								currentWindow = Window.LOGIN;
							}
							dbm.connectToBusiness(cbn);
						}
						else if (type == UserType.BUSINESS) {
							dbm.connectToBusiness(username);
						}
						else if (type == UserType.SUPERUSER) {
							// No need for business connection
						}
					} catch (SQLException sqle) {
						// TODO: Visual cue
						logger.warning("Error connecting to default"
							+" business for customer menu");
						currentWindow = Window.LOGIN;
						break;
					}
					CustomerInfo cInfo;
					try {
						cInfo = CustomerMenuGUI.display(
									this, dbm.getCustomer(username)
								);
					} catch (SQLException sqle) {
						// TODO: Visual cue
						logger.warning("Error getting customer info for"
							+" customer="+username);
						currentWindow = Window.LOGIN;
						break;
					} 
					// Only remove these after username is no longer needed
					username = "";
					password = "";
					if(cInfo.button == CustomerInfo.Buttons.OK) {
						shutdown();
					}
					break;
				case SUPERUSERMENU:
					SuperUserGUI.display(this);
					currentWindow = Window.LOGIN;
					break;
				case ADDEMPLOYEE:
					AddEmployeeInfo aInfo = AddEmployeeGUI.display(this);
					if(aInfo.button == AddEmployeeInfo.Buttons.SAVE) {
						shutdown();
					}
					break;
			}
		}
	}

	/**
	 * System Exit
	 */
	void shutdown() {
		dbm.close();
		System.exit(0);
	}

	/**
	 * Saves changes to the Database
	 */
	public void save() {
		dbm.commit();
	}

	/**
	 * Creates a new DatabaseManager object
	 * This is used to handle all database queries
	 */
	public void load_database() {
		try {
			dbm = new DatabaseManager(DatabaseManager.dbDefaultFileName);
		} catch (SQLException sqle) {
			logger.severe("FATAL: Could not open DatabaseManager");
			System.exit(1);
		}
	}

	/**
	 * Constructor for SessionManager object
	 */
	public SessionManager() {
		this.logger = Logger.getLogger("org.jabst.jabs.DatabaseManager");
		logger.setLevel(Level.FINEST);
		this.ch = new ConsoleHandler();
		logger.addHandler(ch);
		logger.info("Opened sessionManager logger");
		load_database();
		this.employeeManager = new EmployeeManager(this);
	}
	/**
	 * Get method for EmployeeManager object
	 * @return : EmployeeManager object if one exists
	 */
	public EmployeeManager getEmployeeManager() {
		return this.employeeManager;
	}

	/**
	 * Get method for DatabaseManager object
	 * @return : DatabaseManager object if one exists
	 */
	public DatabaseManager getDatabaseManager() {
		return this.dbm;
	}

	/**
	 * Queries user login input against the Database
	 * @param username : Username to query
	 * @param password : password to query
	 * @return : a boolean result
	 */
	public boolean loginUser(String username, String password){
		try {
			return dbm.checkUser(username, password);
		} catch (SQLException sqle) {
			return false;
		}
	}

	/**
	 * Attempts to add a new dataset to the Database
	 * @param username : String, unique user data to be added
	 * @param password : String, user data to be added
	 * @param name : String, user data to be added
	 * @param address : String, user data to be added
	 * @param phone : String, user data to be added
	 * @return : A boolean result
	 */
	public boolean registerUser(String username, String password,
		String name, String address, String phone, String business)
	{
		// If it throws an exception, it failed. Otherwise it succeeded
		try {
			dbm.addUser(username, password, name, address, phone, business);
			logger.info("SessionManager: Successfully added user with dbm");
			return true;
		} catch (SQLException sqle) {
			// false if user exists or the database is somehow broken
			if (sqle instanceof SQLIntegrityConstraintViolationException) {
				logger.warning(
					"SessionManager: Adding user failed: Already a user with that username"
				);
			}
			else {
				logger.warning(
					"SessionManager: Adding user failed: Other database error"
				);
				sqle.printStackTrace();
			}
			return false;
		}
	}

	public void populateCustomer(String username){
		// Request object data from DatabaseManager
		// create new customer object
	}

	public void populateBusiness(String username){
		// Request object data from DatabaseManager
		// create new business object
	}

	void launchCustomerMenu(){
		// NYI
		//Application.launch(CustomerMenu.class);
	}

	void launchBusinessMenu(){
		// NYI
		//Application.launch(BusinessMenu.class);
	}

	/**
	 * Validates the user input for the Name value
	 * @param input : String, user input
	 * @return : a boolean success result
	 */
	public boolean validateNameInput(String input){
		boolean valid = input.chars().allMatch(Character::isLetter);
		valid &= !(input.equals(""));
		return valid;
	}

	/**
	 * Validates the user input for the Phone value
	 * @param input : String, user input
	 * @return : a boolean success result
	 */
	public boolean validatePhoneInput(String input){
		boolean valid = input.chars().allMatch(Character::isDigit);
		if (!(input.length() == 10)){
			valid = false;
		}
		return valid;		
	}

	/**
	 * Validates the user input for the Username value
	 * @param input : String, user input
	 * @return : a boolean success result
	 */
	public boolean validateUsernameInput(String input){
		boolean valid = input.chars().allMatch(Character::isLetter);
		valid &= !(input.equals(""));
		if (input.length() > 20){
			valid = false;
		}
		return valid;
	}

	/**
	 * Validates the user input for the Password value
	 * @param input : String, user input
	 * @return : a boolean success result
	 */
	public boolean validatePasswordStrength(String input){
		// TODO: Strength Conditions
		boolean valid = true;
		valid &= !(input.equals(""));
		return valid;
	}

	/**
	 * Validates the user input for the Address value
	 * @param input : String, user input
	 * @return : a boolean success result
	 */
	public boolean validateAddressInput(String input){
		// TODO: determine conditions
		boolean valid = true;
		valid &= !(input.equals(""));
		return valid;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
