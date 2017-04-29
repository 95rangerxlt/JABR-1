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

public class ConfirmGUI {

	private static String redBorder = "-fx-border-color: red ; -fx-border-width: 2px ;";
	private static boolean yesNo = false;

	public static boolean display(String confirmText) {
		// create the window
		Stage window = new Stage();
		// create all elements
		Button bYes = new Button("Yes");
		Button bNo = new Button("No");
		Label lText = new Label(confirmText);

		//block events to other window
		window.initModality(Modality.APPLICATION_MODAL);

		// setup login button
		bYes.setDefaultButton(true);
		bYes.setOnAction(new EventHandler<ActionEvent>() {
			
			// handle method is called when the button is pressed
			@Override
			public void handle(ActionEvent event) {
				yesNo = true;
				window.close();
			}
		});
		
		// setup register button (swap to register window)
		bNo.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				yesNo = false;
				window.close();
			}
		});

		VBox root = new VBox();//layout manager
		HBox buttons = new HBox();

		root.setSpacing(2);
		root.setPadding(new Insets(3.0, 3.0, 3.0, 3.0));
		buttons.setSpacing(2);

		//add elements to the layout
		buttons.getChildren().addAll(bYes, bNo);
		root.getChildren().addAll(lText, buttons);

		Scene scene = new Scene(root/*, 300, 200*/);//create area inside window

		window.setTitle("Are you Sure?");//text at the top of the window
		window.setScene(scene);//add scene to window
		window.showAndWait();//put the window on the desktop

		return yesNo;
	}

}
