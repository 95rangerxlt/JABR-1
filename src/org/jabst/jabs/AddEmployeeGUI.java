package org.jabst.jabs;

import javafx.event.ActionEvent;//type of event
import javafx.event.EventHandler;//this activates when a button is pressed
import javafx.scene.Scene;//area inside stage
import javafx.scene.input.KeyEvent;//key listener
import javafx.scene.control.*;//buttons, labels  etc.
import javafx.scene.layout.VBox;//layout manager
import javafx.scene.layout.HBox;
import javafx.stage.Stage;//window
import javafx.stage.Modality;
import javafx.stage.WindowEvent;//when window closes
import javafx.geometry.Insets;//insets = padding
// import javafx.beans.value;//changelistener


public class AddEmployeeGUI {

	private static String redBorder = "-fx-border-color: red ; -fx-border-width: 2px ;";
	private static EmployeeManager employeeManager;
	private static Employee currEmployee = null;

	public static AddEmployeeInfo display(SessionManager session) {
		// setup object to return
		AddEmployeeInfo info = new AddEmployeeInfo();
		employeeManager = session.getEmployeeManager();

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
				if (cbEmployeeSelect.getValue().equals("Select Employee")) {
					return;
				}
				handleEmployeeSelect(cbEmployeeSelect, tfName, employeeManager,
					table);
			}
		});
		


		// event handlers
		bSave.setOnAction(new EventHandler<ActionEvent>() {
			
			// handle method is called when the button is pressed
			@Override
			public void handle(ActionEvent event) {
				info.button = AddEmployeeInfo.Buttons.SAVE;
				if (currEmployee != null) {
					currEmployee.name = tfName.getText();
					System.out.println(
						employeeManager.updateEmployee(currEmployee) ?
						"Succesfully saved employee." :
						"Could not save employee."
					);

					currEmployee.createDatesFromTable();
				}
				else {
					System.out.println("Not saving null employee");
				}
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

		// cbEmployeeSelect.valueProperty().addListener(new ChangeListener<String>() {

		// 	@Override
		// 	public void changed(ObservableValue ov, String t, String t1) {
		// 		System.out.println(ov);
		// 		System.out.println(t);
		// 		System.out.println(t1);

		// 		// TODO: change the table
		// 	}
		// });
		
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

		Scene scene = new Scene(root, 900, 450);//create area inside window

		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// TODO: input validation
				System.out.println("key pressed");
			}
		});

		window.setTitle("Add Employee GUI -placeholder-");//text at the top of the window
		window.setScene(scene);//add scene to window
		window.showAndWait();//put the window on the desktop

		return info;
	}
	
	public static void handleEmployeeSelect(ComboBox cbEmployeeSelect,
		TextField tfName,
		EmployeeManager employeeManager,
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
		System.out.println("currEmployee.workingHours.size(): " + currEmployee.workingHours.size());
		if (currEmployee == null) {
			System.err.println("Cannot find employee "+cbEmployeeSelect.getValue());
		}

		System.out.println("creating table from dates...");
		currEmployee.table = currEmployee.createTableFromDates();
		System.out.println("...Success!");

		table.table = currEmployee.table.table;
		table.update();
	}

}
