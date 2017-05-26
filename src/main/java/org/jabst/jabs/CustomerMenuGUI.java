package org.jabst.jabs;

import org.jabst.jabs.util.*;
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

import java.util.ArrayList;

public class CustomerMenuGUI {


	private static String redBorder = "-fx-border-color: red ; -fx-border-width: 2px ;";

	static Customer customer = new Customer("fake_username", "NeEdCuRrEnTlYlOgGeDiNcUsToMeR", "noaddress", "nophone");
	static Appointment[][] tableAnalogue;

	public static CustomerInfo display(SessionManager session, Customer cust) {
		// setup object to return
		customer = cust;
		CustomerInfo info = new CustomerInfo();
		EmployeeManager employeeManager = session.getEmployeeManager();
		Employee allEmployees = employeeManager.getEmployee(-1, true);//get all employees and allow duplicate WeekDates

		// create the window
		Stage window = new Stage();

		// create all elements
		Button bOk = new Button("Ok");
		bOk.setDefaultButton(true);

		System.out.println("CUST MENU: creating table");
		Timetable table = new Timetable(true);
		tableAnalogue = new Appointment[table.days][table.hours];
		table.createTablesOfType(Timetable.CellStatus.UNAVAILABLE);
		TimetableGUI tableGUI = new TimetableGUI(createTableFromAppointments(constructAppointments(employeeManager), table), TimetableCellGUI.Type.RADIOBUTTON);
		//the constructor already calls setupSpacing
		// tableGUI.update();
		System.out.println("CUST MENU: table created");

		//block events to other window
		window.initModality(Modality.APPLICATION_MODAL);

		// event handlers
		bOk.setOnAction(new EventHandler<ActionEvent>() {
			
			// handle method is called when the button is pressed
			@Override
			public void handle(ActionEvent event) {
				info.button = CustomerInfo.Buttons.OK;
				boolean flag = false;
				for(int i = 0; i < tableGUI.cells.length; i++) {
					if(flag)
						break;
					for(int j = 0; j < tableGUI.cells[i].length; j++) {
						if(tableGUI.cells[i][j].selectable.isSelected()) {
							flag = true;
							tableAnalogue[i][j].appointmentType = 0;
							if(!employeeManager.saveAppointment(tableAnalogue[i][j]))
								System.out.println("Appointment not saved");
						}
					}
				}
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

		root.setSpacing(SessionManager.spacing);
		root.setPadding(SessionManager.padding);

		//add elements to the layout
		root.getChildren().addAll(bOk, tableGUI);

		Scene scene = new Scene(root/*, 1200, 500*/);//create area inside window

		tableGUI.update();

		window.setTitle("Making a booking with "+session.getDatabaseManager().getCurrentBusiness().businessName);//text at the top of the window
		window.setScene(scene);//add scene to window
		window.showAndWait();//put the window on the desktop

		return info;
	}

	public static Timetable createTableFromAppointments(ArrayList<Appointment> apts) {
		return createTableFromAppointments(apts, new Timetable(true));
	}

	public static Timetable createTableFromAppointments(ArrayList<Appointment> apts, Timetable table) {
		int c = 0;

        // fill tables
		for(int i = 0; i < apts.size(); i++) {

			int dayIdx = DayOfWeekConversion.dat2wd(apts.get(i).getDate()).dayOfWeek.getValue();
			dayIdx = (dayIdx == 7 ? 0 : dayIdx);
			int hourIdx = DayOfWeekConversion.dat2wd(apts.get(i).getDate()).getStartingHour() - Employee.startingHour;//this still isnt properly defined
			System.out.println("appointment date: "+ apts.get(i).getDate().toString());
			System.out.println("appointment weekdate: "+ DayOfWeekConversion.dat2wd(apts.get(i).getDate()).toString());

			if (hourIdx < 0 || hourIdx > table.hours) {
				System.out.println("Skipping Appointment:\n"+apts.get(i).toString());
				System.out.println("HourIDX ("+hourIdx+") was less than 0 or greater than "+table.hours);
				continue;
			}
			tableAnalogue[dayIdx][hourIdx] = apts.get(i);
			table.table.get(dayIdx).set(hourIdx, Timetable.CellStatus.FREE);
			c++;
		}
		System.out.println("CREATETABLEFROMAPPOINTMENTS: number of available cells: "+c);
		return table;
	}

	// public static ArrayList<Date> getDates(ArrayList<Appointment> appointments) {
	// 	ArrayList<Date> dates = new ArrayList<Date>();
	// 	for(int i = 0; i < appointments.size(); i++) {
	// 		dates.add(appointments.get(i).getDate());
	// 	}
	// 	return dates;
	// }

	/** 
	* creates a set of null appointments (type == -1) matching all of the free employees that can be written to
	* this is real bad - i might improve it later. we'll see
	*/
	//TODO: turns out this doesn't actually work
	private static ArrayList<Appointment> constructAppointments(EmployeeManager empMan) {
		System.out.println("constructAppointments started");
		ArrayList<Appointment> fakeAppointments = new ArrayList<Appointment>();
		ArrayList<String> emps = empMan.getEmployeeNameIDs();
		ArrayList<Employee> employees = new ArrayList<Employee>();
		System.out.println(emps.toString());

		for(int i = 0; i < emps.size(); i++) {//need a better way to get all employees
			String [] employeeFields = emps.get(i).toString().split(" #");
			System.out.println(employeeFields.toString());
			long employeeID = Long.parseLong(employeeFields[1]);
			employees.add(empMan.getEmployee(employeeID));
			System.out.println(employees.toString());

			//this will only go out of bounds if the ID is somehow wrong
			ArrayList<WeekDate> dates = employees.get(i).getWorkingHours();
			System.out.println(dates.toString());
			for(int j = 0; j < dates.size(); j++) {
				fakeAppointments.add(new Appointment(dates.get(j), -1, employeeID, customer));
			}

		}
		System.out.println(fakeAppointments.toString());
		//yes, this is java. fuck efficiency.
		//we have super computers in our pockets
		for(int i = 0; i < employees.size(); i++) {
			for(int j = 0; j < employees.get(i).appointments.size(); j++) {
				for(int k = 0; k < fakeAppointments.size(); k++) {

					System.out.println("c");
					if(employees.get(i).appointments.get(j).getDate().equals(fakeAppointments.get(k).getDate())) {
						System.out.println("deleting appointment:\n"+fakeAppointments.get(k).getDate());
						fakeAppointments.remove(k);
						break;
					}
					System.out.println("c");
				}
			}
		}
		System.out.println("constructAppointments finished ("+fakeAppointments.size()+" appointments)");
		return fakeAppointments;
	}
/*
	private static ArrayList<Appointment> constructAppointments(EmployeeManager empMan) {
		ArrayList<Appointment> fakeAppointments = new ArrayList<Appointment>();
		//get a list of appointments
		ArrayList<Appointment> realAppointments = empMan.getThisWeeksAppointments();
		// get a list of availability
		ArrayList<WeekDate> availability = empMan.getSevenDayEmployeeAvailability(true);//with duplicates please
		// for each availability
		for(int i = 0; i < availability.size(); i++) {
			// for each appointment
			boolean flag = false;
			for(int j = 0; j < realAppointments.size(); j++) {
				// if appointment.time == availability.time && appointment.employee == availability.employee
				if()
					flag = true;
					//flag = true
			}
			//if !flag
			if(!flag) {
				//create fake appointment
				Appointment app = new Appointment(availability.get(i), -1, , customer);
				fakeAppointments.add(app);
			}
		}
		return fakeAppointments;
	}*/

}
