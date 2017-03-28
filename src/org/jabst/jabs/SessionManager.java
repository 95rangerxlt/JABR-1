//import javafx.application.Application;

public class SessionManager {

	
	//Initialise the database
	public dbmStub dbm = new dbmStub();
	
	
	//Methods
	
	public boolean loginUser(String username, String password){
		if (dbm.checkUser(username, password) == true){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void checkUserType(String username){
		if (dbm.checkUserType(username) == "Customer"){
			populateCustomer(username);
			// TODO: Here the default business will have to be created as an object.
			launchCustomerMenu();
		}
		if (dbm.checkUserType(username) == "Business"){
			populateBusiness(username);
			launchBusinessMenu();
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
		Application.launch(Login.class, new String[]{user, pass});
	}
	
	void swapToRegisterWindow(String username, String password){
		Application.launch(Register.class, new String[]{user, pass});
	}
	
	void launchCustomerMenu(){
		Application.launch(CustomerMenu.class);
	}
	
	void launchBusinessMenu(){
		Application.launch(BusinessMenu.class);
	}
	
	
}
