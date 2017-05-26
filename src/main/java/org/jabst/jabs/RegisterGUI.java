package org.jabst.jabs;


import javafx.event.ActionEvent;//type of event
import javafx.event.EventHandler;//this activates when a button is pressed
import javafx.scene.Scene;//area inside stage
import javafx.scene.input.KeyEvent;//key listener
import javafx.scene.control.*;//buttons, labels  etc.
import javafx.scene.layout.VBox;//layout manager
import javafx.scene.layout.HBox;
import javafx.stage.Stage;//window
import javafx.stage.Modality;
import javafx.stage.WindowEvent;//when window closes
import javafx.geometry.Insets;//insets = padding

import java.util.ArrayList;

import java.sql.SQLException;

public class RegisterGUI {

	private static String redBorder = "-fx-border-color: red ; -fx-border-width: 2px ;";

	public static RegisterInfo display(SessionManager session, String user, String pass) {
		RegisterInfo info = new RegisterInfo();
		// create the window
		Stage window = new Stage();
		// create all elements
		ComboBox cbBusinessSelect = new ComboBox();
		updateCombobox(session, cbBusinessSelect);
		cbBusinessSelect.setValue("Select Business");

		Button bLogin = new Button("Login");
		Button bRegister = new Button("Register");
		Label lUName = new Label("Username: ");
		Label lPWord = new Label("Password: ");
		Label lPWord2 = new Label("Re-Enter Password: ");

		Label lName = new Label("Name: ");
		TextField tfName = new TextField();
tfName.setMaxWidth(SessionManager.maxTfWidth);
		Label lAddress = new Label("Address: ");
		TextField tfAddress = new TextField();
tfAddress.setMaxWidth(SessionManager.maxTfWidth);
		// Label lEmail = new Label("Email: ");
		// TextField tfEmail = new TextField();
// tfEmail.setMaxWidth(SessionManager.maxTfWidth);
		Label lPhone = new Label("Phone: ");
		TextField tfPhone = new TextField();
tfPhone.setMaxWidth(SessionManager.maxTfWidth);

		TextField tfUName = new TextField();
tfUName.setMaxWidth(SessionManager.maxTfWidth);
		PasswordField tfPWord = new PasswordField();
tfPWord.setMaxWidth(SessionManager.maxTfWidth);
		PasswordField tfPWord2 = new PasswordField();
tfPWord2.setMaxWidth(SessionManager.maxTfWidth);

		// tfUName.setText(user);//homy didnt want this
		// tfPWord.setText(pass);//homy didnt want this

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
				
				// Validate user input
				if (!session.validateNameInput(tfName.getText())){
					// Error message
					System.out.println("Invalid Name");
					NotificationGUI.display("Invalid Name Input:\nPlease enter your name containing only letters (A-Z,a-z).", "Registration Error");
					return;
				}
				if (!session.validateAddressInput(tfAddress.getText())){
					// Error message
					System.out.println("Invalid Address");
					NotificationGUI.display("Invalid Address Input:\nPlease enter your address in the correct format.", "Registration Error");
					return;
				}
				if (!session.validatePhoneInput(tfPhone.getText())){
					// Error message
					System.out.println("Invalid Phone Number");
					NotificationGUI.display("Invalid Phone Number Input:\nPlease enter a valid Australian phone number including area code", "Registration Error");
					return;
				}
				if (!session.validateUsernameInput(tfUName.getText())){
					// Error message
					System.out.println("Invalid Username");
					NotificationGUI.display("Invalid Username Input:\nPlease enter a Username containing only letters (A-Z,a-z)", "Registration Error");
					return;
				}
				if (!session.validatePasswordStrength(tfPWord.getText())){
					// Error message
					System.out.println("Invalid Password");
					NotificationGUI.display("Invalid Password Input:\nPlease enter a stronger Password", "Registration Error");
					return;
				}
				
				//TODO: also do business combobox

				// register user or clear fields
				if(session.registerUser(
					tfUName.getText(),
					tfPWord.getText(),
					tfName.getText(),
					tfAddress.getText(),
					tfPhone.getText(),
					((BusinessSelection) cbBusinessSelect.getValue()))
				) {
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
				info.username = ""/*tfUName.getText()*/;//homy didnt want this
				info.password = ""/*tfPWord.getText()*/;//homy didnt want this
				window.close();
			}
		});

		// when the window is closed
		window.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				System.out.println("Register window Closed");
			}
		});

		tfUName.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// Character restrictions
				System.out.println("key pressed in username text field");
				tfUName.setStyle (
						session.validateUsernameInput(tfUName.getText())
						? "" : redBorder
				);
			}
		});

		tfPWord.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// Password strength
				System.out.println("key pressed in password text field");
				tfPWord.setStyle (
						session.validatePasswordStrength(tfUName.getText())
						? "" : redBorder
				);
			}
		});

		tfName.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// TODO: input validation
				System.out.println("Key pressed in Name text field");
				tfName.setStyle (
						session.validateNameInput(tfName.getText())
						? "" : redBorder
				);
			}
		});

		tfAddress.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// TODO: input validation
				System.out.println("Key pressed in Address text field");
				tfAddress.setStyle (
						session.validateAddressInput(tfUName.getText())
						? "" : redBorder
				);

			}
		});

		tfPhone.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// TODO: input validation
				System.out.println("Key pressed in Phone text field");
				tfPhone.setStyle (
						session.validatePhoneInput(tfPhone.getText())
						? "" : redBorder
				);
			}
		});


		VBox root = new VBox();//layout manager
		HBox buttons = new HBox();

		root.setSpacing(SessionManager.spacing);
		root.setPadding(SessionManager.padding);
		buttons.setSpacing(SessionManager.spacing);

		//add elements to the layout
		buttons.getChildren().addAll(bRegister, bLogin);
		root.getChildren().addAll(cbBusinessSelect, lName, tfName, lAddress, tfAddress, lPhone, tfPhone, lUName, tfUName, lPWord, tfPWord, lPWord2, tfPWord2, buttons);

		Scene scene = new Scene(root/*, 300, 200*/);//create area inside window
/*
		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// TODO: input validation
				System.out.println("key pressed");
			}
		});*/

		window.setTitle("JABRS System: JABRS Automatic Booking Registration System");//text at the top of the window
		window.setScene(scene);//add scene to window
		window.showAndWait();//put the window on the desktop

		return info;
	}

	public static RegisterInfo display(SessionManager session) {
		return display(session, "", "");
	}

	static void updateCombobox(SessionManager session, ComboBox cb) {
		cb.getItems().clear();

		/* Get all businesses*/
		ArrayList<Business> activeBusinesses;
		try {
			activeBusinesses =  session.getDatabaseManager().getActiveBusinesses();
		} catch (SQLException sqle) {
			cb.getItems().add("Database error.");
			return;
		}
		/* Place them in containers which have a user-friendly toString() */
		ArrayList<BusinessSelection> comboBoxVals
			= new ArrayList<BusinessSelection>();
		for (Business b : activeBusinesses) {
			comboBoxVals.add(new BusinessSelection(b));
		}
		
		cb.getItems().setAll(
			comboBoxVals
		);
		cb.getItems().add(0, "Select Business");
	}

}

