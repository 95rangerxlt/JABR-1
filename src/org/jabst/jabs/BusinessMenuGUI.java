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

	private static String redBorder = "-fx-border-color: red ; -fx-border-width: 2px ;";

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
		Menu mBusinessMenu = new Menu("Business");
			MenuItem miEditBusInfo = new MenuItem("Edit Info"); 

		//block events to other window
		window.initModality(Modality.APPLICATION_MODAL);

		miFileQuit.setOnAction(new EventHandler<ActionEvent>() {
			// Quit
			
			@Override
			public void handle(ActionEvent event) {
				info.button = BusinessInfo.button.QUIT;
				window.close();
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
		mEmployeesMenu.getItems().add(miEditEmployee);
		mBusinessMenu.getItems().add(miEditBusInfo);

		Scene scene = new Scene(root, 300, 200);//create area inside window

		window.setTitle("Business GUI");//text at the top of the window
		window.setScene(scene);//add scene to window
		window.showAndWait();//put the window on the desktop

		return info;
	}

}
