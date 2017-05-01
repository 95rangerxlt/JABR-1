package org.jabst.jabs;

import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import org.hsqldb.HsqlException;


public class SessionManager extends Application {
	// Fields
	private DatabaseManager dbm;
	private EmployeeManager employeeManager;

	public enum Window {
		LOGIN, REGISTER, BUSINESSMENU, CUSTOMERMENU, ADDEMPLOYEE
	}

	// ++++++++++++THIS IS YOUR NEW MAIN++++++++++++++++
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
							if (dbm.isBusiness(lInfo.username)) {
								currentWindow = Window.BUSINESSMENU;
							} else {
								currentWindow = Window.CUSTOMERMENU;
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
						System.err.println("Cannot connect to business:"+username);
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
					dbm.connectToBusiness();
					} catch (SQLException sqle) {
						// TODO: Visual cue
						System.err.println("Error connecting to default"
							+" business for customer menu");
						currentWindow = Window.LOGIN;
						break;
					}
					CustomerInfo cInfo = CustomerMenuGUI.display(this, dbm.getCustomer(username));
					// Only remove these after username is no longer needed
					username = "";
					password = "";
					if(cInfo.button == CustomerInfo.Buttons.OK) {
						shutdown();
					}
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
	
	void shutdown() {
		dbm.close();
		System.exit(0);
	}
	
	public void save() {
		dbm.commit();
	}
	
	public void load_database() {
		try {
			dbm = new DatabaseManager(DatabaseManager.dbDefaultFileName);
		} catch (SQLException sqle) {
			System.err.println("FATAL: SessionManager: Could not open DatabaseManager");
			System.exit(1);
		}
	}
	
	public SessionManager() {
		load_database();
		this.employeeManager = new EmployeeManager(this);
	}
	
	// Methods
	
	public EmployeeManager getEmployeeManager() {
		return this.employeeManager;
	}
	
	public DatabaseManager getDatabaseManager() {
		return this.dbm;
	}
	
	public boolean loginUser(String username, String password){
		try {
			return dbm.checkUser(username, password);
		} catch (SQLException sqle) {
			return false;
		}
	}

	public boolean registerUser(String username, String password,
		String name, String address, String phone)
	{
		// If it throws an exception, it failed. Otherwise it succeeded
		try {
			dbm.addUser(username, password, name, address, phone);
			System.out.println("SessionManager: Successfully added user with dbm");
			return true;
		} catch (SQLException sqle) {
			// false if user exists or the database is somehow broken
			if (sqle instanceof SQLIntegrityConstraintViolationException) {
				System.err.println(
					"SessionManager: Adding user failed: Already a user with that username"
				);
			}
			else {
				System.err.println(
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
	
	public static void main(String[] args) {
		launch(args);
	}
}