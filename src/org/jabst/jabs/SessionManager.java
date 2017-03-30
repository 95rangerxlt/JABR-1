package org.jabst.jabs;

import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.SQLException;
import org.hsqldb.HsqlException;

public class SessionManager extends Application {
	// Fields
	private DatabaseManager dbm;

	public enum Window {
		LOGIN, REGISTER
	}

	// ++++++++++++THIS IS YOUR NEW MAIN++++++++++++++++
	@Override
	public void start(Stage primaryStage) {
		Window currentWindow = Window.LOGIN;
		String username = "";
		String password = "";

		for(;;) {
			switch(currentWindow) {
				case LOGIN:
					LoginInfo lInfo = Login.display(this, username, password);
					username = lInfo.username;
					password = lInfo.password;
					if(lInfo.button == LoginInfo.Buttons.LOGIN) {
						// open respective window
						System.exit(0);
					} else if(lInfo.button == LoginInfo.Buttons.REGISTER) {
						// open register window
						currentWindow = Window.REGISTER;
					}
				break;
				case REGISTER:
					RegisterInfo rInfo = Register.display(this, username, password);
					username = rInfo.username;
					password = rInfo.password;
					if(rInfo.button == RegisterInfo.Buttons.REGISTER) {
						// open respective window
						System.exit(0);
					} else if(rInfo.button == RegisterInfo.Buttons.LOGIN) {
						// open register window
						currentWindow = Window.LOGIN;
					}
				break;
			}
		}

	}
	
	public SessionManager() {
		try {
			dbm = new DatabaseManager(DatabaseManager.dbDefaultFileName);
		} catch (SQLException sqle) {
			System.err.println("FATAL: SessionManager: Could not open DatabaseManager");
			System.exit(1);
		}
	}
	
	// Methods
	public boolean loginUser(String username, String password){
		try {
			return dbm.checkUser(username, password);
		} catch (SQLException sqle) {
			return false;
		}
	}

	public boolean registerUser(String username, String password, String name, String address, String phone){
		// Query Database for existing user
		// populate database
		if(username.equals(password))
			return true;
		return false;
		// false if user exists
	}
	
	public void populateCustomer(String username){
		// Request object data from DatabaseManager
		// create new customer object
	}
	
	public void populateBusiness(String username){
		// Request object data from DatabaseManager
		// create new business object
	}
	
	public void swapToLoginWindow(String user, String pass) {
		Login.display(this, user, pass);
	}
	
	void swapToRegisterWindow(String username, String password){
		Register.display(this, username, password);
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