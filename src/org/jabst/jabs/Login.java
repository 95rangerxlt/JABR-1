package org.jabs.jabst;

import javafx.application.Application;
import javafx.event.ActionEvent;//type of event
import javafx.event.EventHandler;//this activates when a button is pressed
import javafx.scene.Scene;//area inside stage
import javafx.scene.control.*;//buttons, labels  etc.
import javafx.scene.layout.VBox;//layout manager
import javafx.scene.layout.HBox;
import javafx.stage.Stage;//window

public class Login extends Application {

public String hashAlgorithm = "SHA-256";

	@Override
	public void start(Stage primaryStage) {
		// create all elements
		Button bLogin = new Button("Login");
		Button bRegister = new Button("Register");
		Label lUName = new Label("Username: ");
		Label lPWord = new Label("Password: ");
		TextField tfUName = new TextField();
		PasswordField tfPWord = new PasswordField();

		// bLogin.setText("Login");
		bLogin.setOnAction(new EventHandler<ActionEvent>() {
			// handle method is called when the button is pressed
			@Override
			public void handle(ActionEvent event) {
				String username = tfUName.getText();
				String password = tfPWord.getText();

				System.out.println(username);
				System.out.println(password);
				// check username and password

// TODO: SET LOGIN CLASS HERE
				if(loginClass.checkLogin(username, password)) {
					System.exit(0);//close the window
				} else {
					tfUName.setText("");
					tfPWord.setText("");
				}
			}
		});
		// can just use lambda function
		bRegister.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
// TODO: tell session manager to open register window
				System.exit(0);
			}
		});

		VBox root = new VBox();//layout manager
		HBox buttons = new HBox();

		//add elements to the layout
		buttons.getChildren().addAll(bLogin, bRegister);
		root.getChildren().addAll(lUName, tfUName, lPWord, tfPWord, buttons);

		Scene scene = new Scene(root/*, 300, 200*/);//create window

		primaryStage.setTitle("JABLS System: JABLS Automatic Booking Login System");//text at the top of the window
		primaryStage.setScene(scene);//add scene to window
		primaryStage.show();//put the window on the desktop
	}

	public static void main(String[] args) {
		launch(args);
	}

}

class loginClass {
	public static boolean checkLogin(String u, String p) {
		if(u.equals("test") && p.equals("password") || u.equals(p)) {
			return true;
		}
		return false;
	}
}