package org.jabst.jabs;


import javafx.event.ActionEvent;//type of event
import javafx.event.EventHandler;//this activates when a button is pressed
import javafx.scene.Scene;//area inside stage
import javafx.scene.control.*;//buttons, labels  etc.
import javafx.scene.layout.VBox;//layout manager
import javafx.scene.layout.HBox;
import javafx.stage.Stage;//window
import javafx.stage.Modality;
import javafx.stage.WindowEvent;//when window closes
import javafx.geometry.Insets;//insets = padding


public class RegisterGUI {

	private static String redBorder = "-fx-border-color: red ; -fx-border-width: 2px ;";

	public static RegisterInfo display(SessionManager session, String user, String pass) {
		RegisterInfo info = new RegisterInfo();
		// create the window
		Stage window = new Stage();
		// create all elements
		Button bLogin = new Button("Login");
		Button bRegister = new Button("Register");
		Label lUName = new Label("Username: ");
		Label lPWord = new Label("Password: ");
		Label lPWord2 = new Label("Re-Enter Password: ");

		Label lName = new Label("Name: ");
		TextField tfName = new TextField();
		Label lAddress = new Label("Address: ");
		TextField tfAddress = new TextField();
		// Label lEmail = new Label("Email: ");
		// TextField tfEmail = new TextField();
		Label lPhone = new Label("Phone: ");
		TextField tfPhone = new TextField();

		TextField tfUName = new TextField();
		PasswordField tfPWord = new PasswordField();
		PasswordField tfPWord2 = new PasswordField();

		tfUName.setText(user);
		tfPWord.setText(pass);

		//block events to other window
		window.initModality(Modality.APPLICATION_MODAL);

		// setup register button
		bRegister.setDefaultButton(true);
		bRegister.setOnAction(new EventHandler<ActionEvent>() {

			// handle method is called when the button is pressed
			@Override
			public void handle(ActionEvent event) {
				// check that passwords match
				if(!tfPWord.getText().equals(tfPWord2.getText())) {
					tfPWord.setStyle(redBorder);
					tfPWord2.setStyle(redBorder);
					tfPWord.setText("");
					tfPWord2.setText("");
					return;
				}

				System.out.println(tfUName.getText());
				System.out.println(tfPWord.getText());
				System.out.println(tfPWord2.getText());
				System.out.println("Name: "+tfName.getText());
				System.out.println("Address: "+tfAddress.getText());
				System.out.println("Phone: "+tfPhone.getText());

				// register user or clear fields
				if(session.registerUser(tfUName.getText(), tfPWord.getText(), tfName.getText(), tfAddress.getText(), tfPhone.getText())) {
					info.username = tfUName.getText();
					info.password = tfPWord.getText();
					info.address = tfAddress.getText();
					info.phone = tfPhone.getText();
					info.name = tfName.getText();
					info.button = RegisterInfo.Buttons.REGISTER;
					window.close();
				} else {
					tfUName.setText("");// username taken
					tfPWord.setText("");
					tfPWord2.setText("");
					tfUName.setStyle(redBorder);
				}
			}
		});

		// setup login button (swap to login window)
		bLogin.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				info.button = RegisterInfo.Buttons.LOGIN;
				info.username = tfUName.getText();
				info.password = tfPWord.getText();
				window.close();
			}
		});

		// when the window is closed
		window.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				System.out.println("Register window Closed");
			}
		});

		VBox root = new VBox();//layout manager
		HBox buttons = new HBox();

		root.setSpacing(2);
		root.setPadding(new Insets(3.0, 3.0, 3.0, 3.0));
		buttons.setSpacing(2);

		//add elements to the layout
		buttons.getChildren().addAll(bRegister, bLogin);
		root.getChildren().addAll(lName, tfName, lAddress, tfAddress, lPhone, tfPhone, lUName, tfUName, lPWord, tfPWord, lPWord2, tfPWord2, buttons);

		Scene scene = new Scene(root/*, 300, 200*/);//create area inside window

		window.setTitle("JABRS System: JABRS Automatic Booking Registration System");//text at the top of the window
		window.setScene(scene);//add scene to window
		window.showAndWait();//put the window on the desktop

		return info;
	}

	public static RegisterInfo display(SessionManager session) {
		return display(session, "", "");
	}

}