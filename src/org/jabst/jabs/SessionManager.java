
public class SessionManager {

	//Variables
	
	private String username; //parse from GUI?
	private String password; //parse from GUI?
	//Initialise the database
	//DatabaseManager dbm = new DatabaseManager();
	
	
	//Methods
	
	public boolean session()
	{
		if (requestUserCredentialCheck(username, password) == true)
		{
			// Create either CustomerUser or BusinessUser object
			// Initialise appropriate menu for new user object (Integration with GUI)
			// Populate business objects if CustomerUser is created
			return true;
		}
		else
		{
			return false;
		}
			
		
	}
	
	public boolean requestUserCredentialCheck(String username, String password)
	{
		//dbm.RequestUserCredentialCheck(username, password);
		return true; //stub
		
	}
}
