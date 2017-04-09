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


public class BusinessMenuGUI {

	public static BusinessInfo display(SessionManager session) {
		// setup object to return
		BusinessInfo info = new BusinessInfo();

		// create the window
		Stage window = new Stage();

		// create all elements
		MenuBar mb = new MenuBar();
		Menu mFileMenu = new Menu("File");
		MenuItem miFileSave = new MenuItem("Save");
		MenuItem miFileLogout = new MenuItem("Logout");
		MenuItem miFileQuit = new MenuItem("Quit");
		Menu mEmployeesMenu = new Menu("Employees");
		MenuItem miEditEmployee = new MenuItem("Edit Employee");
		MenuItem miAvailabilitySummary = new MenuItem("Availability Summary");
		Menu mBusinessMenu = new Menu("Business");
		MenuItem miEditBusInfo = new MenuItem("Edit Info"); 

		//block events to other window
		window.initModality(Modality.APPLICATION_MODAL);

		miFileSave.setOnAction(new EventHandler<ActionEvent>() {
			// Ask the SessionManager to save
			@Override
			public void handle(ActionEvent event) {
				session.save();
			}
		});

		miFileLogout.setOnAction(new EventHandler<ActionEvent>() {
			// Save and go to Login
			@Override
			public void handle(ActionEvent event) {
				session.save();
				info.button = BusinessInfo.button.LOGOUT;
				window.close();
			}
		});

		miFileQuit.setOnAction(new EventHandler<ActionEvent>() {
			// Quit program
			@Override
			public void handle(ActionEvent event) {
				session.shutdown();
			}
		});

		miEditEmployee.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				AddEmployeeGUI.display(session);
			}
		});

		miAvailabilitySummary.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				AvailabilityGUI.display(session);
			}
		});

		// when the window is closed
		window.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				System.out.println("Business Menu window Closed");
			}
		});
		
		// Setup Window and layout
 		VBox root = new VBox();//layout manager

 		root.setSpacing(2);
 		root.setPadding(new Insets(3.0, 3.0, 3.0, 3.0));

		//add elements to the layout
 		root.getChildren().addAll(mb);
 		mb.getMenus().addAll(mFileMenu, mEmployeesMenu, mBusinessMenu);
 		mFileMenu.getItems().addAll(miFileSave, miFileLogout, miFileQuit);
 		mEmployeesMenu.getItems().addAll(miEditEmployee, miAvailabilitySummary);
 		mBusinessMenu.getItems().add(miEditBusInfo);

		Scene scene = new Scene(root, 300, 200);//create area inside window

		window.setTitle("Business GUI");//text at the top of the window
		window.setScene(scene);//add scene to window
		window.showAndWait();//put the window on the desktop

		return info;
	}

}
