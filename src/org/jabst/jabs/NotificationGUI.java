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

public class NotificationGUI {

	public static void display(String text, String title) {
		// create the window
		Stage window = new Stage();
		// create all elements
		Button bOk = new Button("Ok");
		Label lText = new Label(text);

		//block events to other window
		window.initModality(Modality.APPLICATION_MODAL);

		// setup login button
		bOk.setDefaultButton(true);
		bOk.setOnAction(new EventHandler<ActionEvent>() {
			
			// handle method is called when the button is pressed
			@Override
			public void handle(ActionEvent event) {
				window.close();
			}
		});

		VBox root = new VBox();//layout manager

		root.setSpacing(SessionManager.spacing);
		root.setPadding(SessionManager.padding);

		//add elements to the layout
		root.getChildren().addAll(lText, bOk);

		Scene scene = new Scene(root/*, 300, 200*/);//create area inside window

		window.setTitle(title);//text at the top of the window
		window.setScene(scene);//add scene to window
		window.showAndWait();//put the window on the desktop
	}

}
