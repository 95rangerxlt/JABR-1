package org.jabst.jabs;

import javafx.application.Application;
import java.sql.SQLException;

public class SessionManager {
	// Fields
	private DatabaseManager dbm;
	
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
		return true;
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
		Application.launch(Login.class, new String[]{user,pass});
	}
	
	void swapToRegisterWindow(String username, String password){
		Application.launch(Register.class, new String[]{username,password});
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
		Application.launch(Login.class, new String[]{"", ""});
	}
}