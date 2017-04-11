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


public class AvailabilityGUI {

	public static AvailabilityInfo display(SessionManager session) {
		// setup object to return
		AvailabilityInfo info = new AvailabilityInfo();
		EmployeeManager employeeManager = session.getEmployeeManager();
		Employee currEmployee = null;

		// create the window
		Stage window = new Stage();

		// create all elements
		Button bClose = new Button("Close");
		bClose.setDefaultButton(true);

		TextField tfName = new TextField();
		tfName.setPromptText("Name");
		tfName.setPrefWidth(500);

		Employee allEmployees = null;
		TimetableGUI table = new TimetableGUI(/*Set timetable on combobox update*/);
		getData(employeeManager, allEmployees, table);

		//block events to other window
		window.initModality(Modality.APPLICATION_MODAL);

		// event handlers
		bClose.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Customer Menu window Closed");
			}
		});

		// when the window is closed
		window.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				System.out.println("Customer Menu window Closed");
			}
		});
		
		// Setup Window and layout
 		VBox root = new VBox();//layout manager

		root.setSpacing(2);
		root.setPadding(new Insets(3.0, 3.0, 3.0, 3.0));

		//add elements to the layout
		root.getChildren().addAll(table, bClose);

		Scene scene = new Scene(root, 900, 400);//create area inside window

		window.setTitle("Availability GUI -placeholder-");//text at the top of the window
		window.setScene(scene);//add scene to window
		window.showAndWait();//put the window on the desktop

		return info;
	}
	
	public static void getData(EmployeeManager employeeManager, Employee allEmployees,
		TimetableGUI table)
	{
		allEmployees = employeeManager.getEmployee(-1);
		if (allEmployees == null) {
			System.err.println("Cannot find all employees");
		}

		System.out.println("creating table from dates...");
		allEmployees.table = allEmployees.createTableFromDates();
		System.out.println("...Success!");

		table.allEmployees = true;
		table.table = allEmployees.table.table;
		table.update();
	}

}
