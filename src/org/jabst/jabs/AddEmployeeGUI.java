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


public class AddEmployeeGUI {

	private static String redBorder = "-fx-border-color: red ; -fx-border-width: 2px ;";

	public static AddEmployeeInfo display(SessionManager session) {
		// setup object to return
		AddEmployeeInfo info = new AddEmployeeInfo();
		EmployeeManager employeeManager = session.getEmployeeManager();
		Employee currEmployee = null;

		// create the window
		Stage window = new Stage();

		// create all elements
		Button bNew = new Button("New");
		Button bDelete = new Button("Delete");
		Button bSave = new Button("Save");
		bSave.setDefaultButton(true);

		TextField tfName = new TextField();
		tfName.setPromptText("Name");
		tfName.setPrefWidth(500);

		ComboBox cbEmployeeSelect = new ComboBox();
		cbEmployeeSelect.getItems().add("Select Employee");
		cbEmployeeSelect.getItems().addAll(
			employeeManager.getEmployeeNameIDs()
		);
		cbEmployeeSelect.setValue("Select Employee");

		TimetableGUI table = new TimetableGUI(/*Set timetable on combobox update*/);

		//block events to other window
		window.initModality(Modality.APPLICATION_MODAL);
		
		// Close on request
		window.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				window.close();
			}
		});
		
		cbEmployeeSelect.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				handleEmployeeSelect(cbEmployeeSelect, tfName, employeeManager,
				currEmployee, table);
				
			}
		});
		


		// event handlers
		bSave.setOnAction(new EventHandler<ActionEvent>() {
			
			// handle method is called when the button is pressed
			@Override
			public void handle(ActionEvent event) {
				info.button = AddEmployeeInfo.Buttons.SAVE;
			}
		});

		bNew.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				info.button = AddEmployeeInfo.Buttons.NEW;
			}
		});

		bDelete.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				info.button = AddEmployeeInfo.Buttons.DELETE;
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

 		HBox selEmpAndName = new HBox();
 		HBox saveAndDelete = new HBox();

		root.setSpacing(2);
		root.setPadding(new Insets(3.0, 3.0, 3.0, 3.0));

		selEmpAndName.setSpacing(2);
		// selEmpAndName.setPadding(new Insets(3.0, 3.0, 3.0, 3.0));

		saveAndDelete.setSpacing(2);
		// saveAndDelete.setPadding(new Insets(3.0, 3.0, 3.0, 3.0));

		//add elements to the layout
		selEmpAndName.getChildren().addAll(cbEmployeeSelect, bNew);
		saveAndDelete.getChildren().addAll(bSave, bDelete);
		root.getChildren().addAll(selEmpAndName, tfName, table, saveAndDelete);

		Scene scene = new Scene(root, 700, 500);//create area inside window

		window.setTitle("Add Employee GUI -placeholder-");//text at the top of the window
		window.setScene(scene);//add scene to window
		window.showAndWait();//put the window on the desktop

		return info;
	}
	
	public static void handleEmployeeSelect(ComboBox cbEmployeeSelect,
		TextField tfName,
		EmployeeManager employeeManager,
		Employee currEmployee,
		TimetableGUI table)
	{
		String [] employeeFields = cbEmployeeSelect.getValue().toString().split(" #");
		String employeeName = employeeFields[0];
		long employeeID = Long.parseLong(employeeFields[1]);
		if (employeeName.equals("Select Employee")) {
			tfName.setText("");
		}
		else {
			tfName.setText(employeeName);
		}
		
		currEmployee = employeeManager.getEmployee(employeeID);
		if (currEmployee == null) {
			System.err.println("Cannot find employee "+cbEmployeeSelect.getValue());
		}

		System.out.println("creating table from dates...");
		currEmployee.table = currEmployee.createTableFromDates();
		System.out.println("...Success!");

		table = new TimetableGUI(currEmployee.table);
	}

	public void setupTable(TimetableGUI table) {

	}

}
