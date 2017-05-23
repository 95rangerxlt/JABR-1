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

 		root.setSpacing(2);
 		root.setPadding(new Insets(3.0, 3.0, 3.0, 3.0));

 		buttonBox.setSpacing(2);

		//add elements to the layout
 		root.getChildren().addAll(lBusinessName, tfBusinessName, lBusinessOwner, tfBusinessOwner, lAddress, tfAddress, lPhone, tfPhone, buttonBox);
 		buttonBox.getChildren().addAll(bSetIcon, bSave, bClose);

		Scene scene = new Scene(root/*, 300, 200*/);//create area inside window

		window.setTitle("Business GUI");//text at the top of the window
		window.setScene(scene);//add scene to window
		window.showAndWait();//put the window on the desktop
	}

}
