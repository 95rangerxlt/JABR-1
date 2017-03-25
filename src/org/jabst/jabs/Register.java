package org.jabst.jabs;

import javafx.application.Application;
import javafx.event.ActionEvent;//type of event
import javafx.event.EventHandler;//this activates when a button is pressed
import javafx.scene.Scene;//area inside stage
import javafx.scene.control.*;//buttons, labels  etc.
import javafx.scene.layout.VBox;//layout manager
import javafx.scene.layout.HBox;
import javafx.stage.Stage;//window

import java.util.List;

public class Register extends Application {

	private String redBorder = "-fx-border-color: red ; -fx-border-width: 2px ;";

	@Override
	public void start(Stage primaryStage) {
		SessionManager session = new SessionManager();
		// get a StageCoordinate class
		StageCoordinate sc = new StageCoordinate();
		sc.setStage(primaryStage);

		List<String> params = getParameters().getRaw();//get parameters
		// params.get(1)

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

		tfUName.setText(params.get(0));
		tfPWord.setText(params.get(1));

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
					// StageCoordinate sc = new StageCoordinate(primaryStage);
					System.exit(0);//close the window
				} else {
					tfUName.setText("");// username taken
					tfPWord.setText("");
					tfPWord2.setText("");
					tfUName.setStyle(redBorder);
				}
			}
		});


		bLogin.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// StageCoordinate sc = new StageCoordinate(primaryStage);
				session.swapToLoginWindow(tfUName.getText(), tfPWord.getText());
				System.exit(0);
			}
		});

		VBox root = new VBox();//layout manager
		HBox buttons = new HBox();

		//add elements to the layout
		buttons.getChildren().addAll(bRegister, bLogin);
		root.getChildren().addAll(lName, tfName, lAddress, tfAddress, lPhone, tfPhone, lUName, tfUName, lPWord, tfPWord, lPWord2, tfPWord2, buttons);

		Scene scene = new Scene(root/*, 300, 200*/);//create window

		primaryStage.setTitle("JABRS System: JABRS Automatic Booking Registration System");//text at the top of the window
		primaryStage.setScene(scene);//add scene to window
		primaryStage.show();//put the window on the desktop
	}

}