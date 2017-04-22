package org.jabst.jabs;


import javafx.event.ActionEvent;//type of event
import javafx.event.EventHandler;//this activates when a button is pressed
import javafx.scene.Scene;//area inside stage
import javafx.scene.input.KeyEvent;//key listener
import javafx.scene.control.*;//buttons, labels  etc.
import javafx.scene.layout.VBox;//layout manager
import javafx.scene.layout.HBox;
import javafx.stage.Stage;//window
import javafx.stage.Modality;//pauses other window when this one is open
import javafx.stage.WindowEvent;//when the window is closed
import javafx.geometry.Insets;//insets = padding

public class LoginGUI {

	private static String redBorder = "-fx-border-color: red ; -fx-border-width: 2px ;";

	public static LoginInfo display(SessionManager session, String user, String pass) {
		LoginInfo info = new LoginInfo();
		// create the window
		Stage window = new Stage();
		// create all elements
		Button bLogin = new Button("Login");
		Button bRegister = new Button("Register");
		Label lUName = new Label("Username: ");
		Label lPWord = new Label("Password: ");
		TextField tfUName = new TextField();
		PasswordField tfPWord = new PasswordField();

		tfUName.setText(user);
		tfPWord.setText(pass);

		//block events to other window
		window.initModality(Modality.APPLICATION_MODAL);

		// setup login button
		bLogin.setDefaultButton(true);
		bLogin.setOnAction(new EventHandler<ActionEvent>() {
			
			// handle method is called when the button is pressed
			@Override
			public void handle(ActionEvent event) {

				System.out.println(tfUName.getText());
				System.out.println(tfPWord.getText());
				// check username and password


				if(session.loginUser(tfUName.getText(), tfPWord.getText())) {
					// StageCoordinate sc = new StageCoordinate(window);
					info.username = tfUName.getText();
					info.password = tfPWord.getText();
					info.button = LoginInfo.Buttons.LOGIN;
					window.close();//close the window
				} else {
					tfUName.setText("");
					tfPWord.setText("");
					tfUName.setStyle(redBorder);
					tfPWord.setStyle(redBorder);
				}
			}
		});
		
		// setup register button (swap to register window)
		bRegister.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				info.button = LoginInfo.Buttons.REGISTER;
				info.username = tfUName.getText();
				info.password = tfPWord.getText();
				window.close();
			}
		});

		tfUName.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// TODO: input validation
				System.out.println("key pressed in username text field");
			}
		});

		tfPWord.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// TODO: input validation
				System.out.println("key pressed in password text field");
			}
		});

		// when the window is closed
		window.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				info.button = LoginInfo.Buttons.CLOSE;
			}
		});

		VBox root = new VBox();//layout manager
		HBox buttons = new HBox();

		root.setSpacing(2);
		root.setPadding(new Insets(3.0, 3.0, 3.0, 3.0));
		buttons.setSpacing(2);

		//add elements to the layout
		buttons.getChildren().addAll(bLogin, bRegister);
		root.getChildren().addAll(lUName, tfUName, lPWord, tfPWord, buttons);

		Scene scene = new Scene(root/*, 300, 200*/);//create area inside window

		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// TODO: input validation
				System.out.println("key pressed");
			}
		});

		window.setTitle("JABLS System: JABLS Automatic Booking Login System");//text at the top of the window
		window.setScene(scene);//add scene to window
		window.showAndWait();//put the window on the desktop

		return info;
	}

	public static LoginInfo display(SessionManager session) {
		return display(session, "", "");
	}

}
