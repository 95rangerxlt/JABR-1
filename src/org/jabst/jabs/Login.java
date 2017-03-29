package org.jabst.jabs;

import javafx.application.Application;
import javafx.event.ActionEvent;//type of event
import javafx.event.EventHandler;//this activates when a button is pressed
import javafx.scene.Scene;//area inside stage
import javafx.scene.control.*;//buttons, labels  etc.
import javafx.scene.layout.VBox;//layout manager
import javafx.scene.layout.HBox;
import javafx.stage.Stage;//window
import javafx.geometry.Insets;//insets = padding

import java.util.List;

public class Login extends Application {

	@Override
	public void start(Stage primaryStage) {
		// get a StageCoordinate class
		StageCoordinate sc = new StageCoordinate();
		sc.setStage(primaryStage);

		SessionManager session = new SessionManager();


		List<String> params = getParameters().getRaw();//get parameters
		// params.get(1)

		// create all elements
		Button bLogin = new Button("Login");
		Button bRegister = new Button("Register");
		Label lUName = new Label("Username: ");
		Label lPWord = new Label("Password: ");
		TextField tfUName = new TextField();
		PasswordField tfPWord = new PasswordField();

		tfUName.setPrefWidth(800);
		tfPWord.setPrefWidth(800);

		tfUName.setText(params.get(0));
		tfPWord.setText(params.get(1));

		bLogin.setDefaultButton(true);
		bLogin.setOnAction(new EventHandler<ActionEvent>() {
			// handle method is called when the button is pressed
			@Override
			public void handle(ActionEvent event) {

				System.out.println(tfUName.getText());
				System.out.println(tfPWord.getText());
				// check username and password


				if(session.loginUser(tfUName.getText(), tfPWord.getText())) {
					// StageCoordinate sc = new StageCoordinate(primaryStage);
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
				// StageCoordinate sc = new StageCoordinate(primaryStage);
				session.swapToRegisterWindow(tfUName.getText(), tfPWord.getText());
				System.exit(0);
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

		Scene scene = new Scene(root/*, 300, 200*/);//create window

		primaryStage.setTitle("JABLS System: JABLS Automatic Booking Login System");//text at the top of the window
		primaryStage.setScene(scene);//add scene to window
		primaryStage.show();//put the window on the desktop
	}

}
