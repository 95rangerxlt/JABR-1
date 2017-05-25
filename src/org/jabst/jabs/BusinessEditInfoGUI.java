package org.jabst.jabs;

import java.io.File;
import javafx.event.ActionEvent;//type of event
import javafx.event.EventHandler;//this activates when a button is pressed
import javafx.scene.Scene;//area inside stage
import javafx.scene.control.*;//buttons, labels  etc.
import javafx.scene.layout.VBox;//layout manager
import javafx.scene.layout.HBox;
import javafx.stage.Stage;//window
import javafx.stage.Modality;
import javafx.stage.WindowEvent;//when window closes
import javafx.stage.FileChooser;//choose icon
import javafx.geometry.Insets;//insets = padding


public class BusinessEditInfoGUI {

	public static void display(SessionManager session) {
		// create the window
		Stage window = new Stage();

		// create all elements
		Label lBusinessName = new Label("BusinessName: ");
		TextField tfBusinessName = new TextField();
		Label lBusinessOwner = new Label("BusinessOwner: ");
		TextField tfBusinessOwner = new TextField();
		Label lAddress = new Label("Address: ");
		TextField tfAddress = new TextField();
		Label lPhone = new Label("Phone: ");
		TextField tfPhone = new TextField();

		FileChooser fChooser = new FileChooser();
		fChooser.setTitle("Choose Icon");

		Button bSetIcon = new Button("Set Icon");
		Button bSave = new Button("Save");
		Button bClose = new Button("Close");



		//block events to other window
		window.initModality(Modality.APPLICATION_MODAL);

		bSetIcon.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				File file = fChooser.showOpenDialog(new Stage());
				//TODO: set icon
			}
		});

		bSave.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				/* Validate inputs given */
                		boolean valid = true;
				valid &=
				session.validateAddressInput(tfAddress.getText());
				if (!valid){
					NotificationGUI.display("Invalid Address Input:\nPlease enter your address in the correct format.", "Registration Error");
					return;
				} 
				System.out.println("Address valid");
				valid &=
				session.validateBusinessNameInput(tfBusinessName.getText());
				if (!valid){
					NotificationGUI.display("Invalid Business Name Input:\nPlease enter a Business Name containing only letters (A-Z,a-z)", "Registration Error");
					return;
				}
				System.out.println("Business Name valid");
				valid &=
				session.validateNameInput(tfBusinessOwner.getText());
				if (!valid){
					NotificationGUI.display("Invalid Name Input:\nPlease enter a Name containing only letters (A-Z,a-z)", "Registration Error");
					return;
				}
				System.out.println("Owner Name valid");
				valid &=
				session.validatePhoneInput(tfPhone.getText());
				if (!valid){
					NotificationGUI.display("Invalid Phone Number Input:\nPlease enter a valid Australian phone number including area code", "Registration Error");
					return;
				} 
				System.out.println("Phone valid, saving");
				System.out.println("TODO: save button functionality");
			}
		});

		bClose.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				window.close();
			}
		});

		
		// Setup Window and layout
 		VBox root = new VBox();//layout manager
 		HBox buttonBox = new HBox();

 		root.setSpacing(SessionManager.spacing);
 		root.setPadding(SessionManager.padding);

 		buttonBox.setSpacing(SessionManager.spacing);

		//add elements to the layout
 		root.getChildren().addAll(lBusinessName, tfBusinessName, lBusinessOwner, tfBusinessOwner, lAddress, tfAddress, lPhone, tfPhone, buttonBox);
 		buttonBox.getChildren().addAll(bSetIcon, bSave, bClose);

		Scene scene = new Scene(root/*, 300, 200*/);//create area inside window

		window.setTitle("Business GUI");//text at the top of the window
		window.setScene(scene);//add scene to window
		window.showAndWait();//put the window on the desktop
	}

}
