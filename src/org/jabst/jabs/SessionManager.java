package org.jabst.jabs;

import javafx.application.Application;

public class SessionManager {

	public SessionManager() {}

	public boolean loginUser(String user, String pass) {
		if(user.equals(pass))
			return true;
		return false;
	}

	public void swapToLoginWindow(String user, String pass) {
		Application.launch(Login.class, new String[]{user, pass});
	}

	public boolean registerUser(String user, String pass, String name, String address, String phone) {
		if(user.equals(pass))
			return true;
		return false;
	}

	public void swapToRegisterWindow(String user, String pass) {
		Application.launch(Register.class, new String[]{user, pass});
	}


	public static void main(String[] args) {
		Application.launch(Login.class, new String[]{"", ""});
	}

}
