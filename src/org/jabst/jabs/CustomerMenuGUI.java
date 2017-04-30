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


public class CustomerMenuGUI {

	private static String redBorder = "-fx-border-color: red ; -fx-border-width: 2px ;";

	public static CustomerInfo display(SessionManager session) {
		// setup object to return
		CustomerInfo info = new CustomerInfo();
		EmployeeManager employeeManager = session.getEmployeeManager();
		Employee allEmployees = employeeManager.getEmployee(-1, true);//get all employees and allow duplicate WeekDates

		// create the window
		Stage window = new Stage();

		// create all elements
		Button bOk = new Button("Ok");
		bOk.setDefaultButton(true);

		Timetable table = new Timetable(true);
		table.createTablesOfType(Timetable.CellStatus.UNAVAILABLE);
		TimetableGUI tableGUI = new TimetableGUI(table);
		//the constructor already calls setupSpacing
		// tableGUI.update();

		//block events to other window
		window.initModality(Modality.APPLICATION_MODAL);

		// event handlers
		bOk.setOnAction(new EventHandler<ActionEvent>() {
			
			// handle method is called when the button is pressed
			@Override
			public void handle(ActionEvent event) {
				info.button = CustomerInfo.Buttons.OK;
				window.close();
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
		root.getChildren().addAll(bOk, tableGUI);

		Scene scene = new Scene(root, 900, 400);//create area inside window

		tableGUI.update();

		window.setTitle("Customer GUI -placeholder-");//text at the top of the window
		window.setScene(scene);//add scene to window
		window.showAndWait();//put the window on the desktop

		return info;
	}

}
