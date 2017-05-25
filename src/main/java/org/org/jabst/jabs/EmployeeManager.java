package org.jabst.jabs;

import java.util.ArrayList;
import java.util.Date;

import java.sql.SQLException;

import org.jabst.jabs.util.DayOfWeekConversion;
import java.util.Calendar;

public class EmployeeManager {
	//Fields
	ArrayList<Employee> employees = new ArrayList<Employee>();
	private DatabaseManager dbm;
	SessionManager session;

	
	// Constructor
	public EmployeeManager(SessionManager sm) {
		this.dbm = sm.getDatabaseManager();
		this.session = sm;
	}
	
	//Methods

	/** adds an employee to the database with correct id
	  * @return the employee object created by the database
	  */
	public Employee addEmployee() {
		try {
			long id = dbm.addEmployee("new Employee");
			if(id == -1) {
				System.out.println("EMPLOYEE MANAGER:\n\tEmployee not created");
				return null;//can't create employee
			}
			return dbm.getEmployee(id);
		} catch (SQLException sqle) {
			System.out.println("EMPLOYEE MANAGER:\n\tError adding employee:\n");
			sqle.printStackTrace();
			return null;
		}
	}
	
	/** Updates the employee in the database to match the given object
	  * @param employee An employee object representing the employee to update
	  * @return whether the record sucessfully updated
	  */
	public boolean updateEmployee(Employee employee) {
		try {
			return dbm.updateEmployee(employee);
		} catch (SQLException sqle) {
			sqle.printStackTrace();//PLZ PRINT STACK TRACES
			return false;
		}
	}

	public ArrayList<Appointment> getThisWeeksAppointments() {
		try {
			return dbm.getThisWeeksAppointments();
		} catch (SQLException sqle) {
			sqle.printStackTrace();//PLZ PRINT STACK TRACE
			return null;
		}
	}

	public ArrayList<WeekDate> getSevenDayEmployeeAvailability(boolean duplicates) {
		try {
			return dbm.getSevenDayEmployeeAvailability(!duplicates);
		} catch (SQLException sqle) {
			sqle.printStackTrace();//PLZ PRINT STACK TRACE
			return null;
		}
	}

	public boolean saveAppointment(Appointment apt) {
		try {
			return dbm.saveAppointment(apt);
		} catch (SQLException sqle) {
			sqle.printStackTrace();//PLZ PRINT STACK TRACE
			return false;
		}
	}
	
	/** Creates a new employee object
	 * 
	 * @param employeeName : the name of the new employee
	 */
	public void createEmployee(String employeeName){
		//NYI
	}

	/** Adds employee objects to the ArrayList
	 * Designed to accept input from the database
	 * @param employee : The employee object to add
	 */
	public void loadEmployee(int employeeID){
		
	}
	
	/** Asks the database for just this employee, and passes
	 *  it back to the caller
	*/
	public Employee getEmployee(long employeeID) {
		if(employeeID == -1) {
			// get all employees
			try {
				ArrayList<WeekDate> availability
                    = dbm.getSevenDayEmployeeAvailability(false);
				ArrayList<Appointment> appointments = dbm.getThisWeeksAppointments();
				Employee emp = new Employee(
					-1, "allEmployees", availability,
					appointments
				);
				System.out.println("All Employees: \n" + availability.toString());
				return emp;
			} catch (SQLException sqle) {
				System.out.println("error getting employees:\n");
				sqle.printStackTrace();
				return null;
			}
		} else {
			try {
				System.out.println("getting Employee: " + employeeID);
				return dbm.getEmployee(employeeID);
			} catch (SQLException sqle) {
				//stacktrace plz
				System.out.println("EMPLOYEE MANAGER: can not get employee "+ employeeID);
				sqle.printStackTrace();
				return null;
			}
		}
	}

	
	/** Asks the database for just this employee, and passes
	 *  it back to the caller
	*/
	public Employee getEmployee(long employeeID, boolean duplicates) {
		if(employeeID == -1) {
			// get all employees
			try {
				ArrayList<WeekDate> availability
                    = dbm.getSevenDayEmployeeAvailability(!duplicates);
				ArrayList<Appointment> appointments = dbm.getThisWeeksAppointments();
				Employee emp = new Employee(
					-1, "allEmployees", availability,
					dbm.getThisWeeksAppointments()
				);
				System.out.println("All Employees: \n" + availability.toString());
				return emp;
			} catch (SQLException sqle) {
				System.out.println("error getting employees:\n");
				sqle.printStackTrace();
				return null;
			}
		} else {
			try {
				System.out.println("getting Employee: " + employeeID);
				return dbm.getEmployee(employeeID);
			} catch (SQLException sqle) {
				return null;
			}
		}
	}
	
	/** Returns the unique ID of an employee
	 * 
	 * @param employeeName : name of the employee
	 * @return a long containing the unique ID of the employee or null
	 */
	public long getEmployeeID(String employeeName){
		for(int i = 0; i < employees.size(); i++){
			if (employeeName.equals(employees.get(i).name)){
				return employees.get(i).id;
			}		
		}
		return -1;
	}
	
	/** Removes employee from the ArrayList
	 * 
	 * @param employeeName : use getEmployeeID(String employeeName) to ensure valid ID
	 */
	public boolean deleteEmployee(Employee emp, boolean force){
		try {
			return dbm.deleteEmployee(emp, force);
		} catch(SQLException sqle) {
			System.out.println("EMPLOYEE MANAGER: \t\ncannot remove employee:");
			sqle.printStackTrace();
			return false;
		}
	}
	
	/** Checks whether there is an employee who is free at the given Date
	  */
	public boolean checkFreeEmployeeAt(Date reqDate) {
		ArrayList<WeekDate> weekAvailability;
		ArrayList<Appointment> weekAppointments;
		try {
			weekAvailability =
				dbm.getSevenDayEmployeeAvailability(false);
			weekAppointments =
				dbm.getThisWeeksAppointments();
		} catch (SQLException sqle) {
			return false;
		}

		// Check if seven day availability has this date
		// Generate the Date for all availability
		boolean cont = false;
		int availIdx;
		for (availIdx = 0; availIdx < weekAvailability.size(); ++availIdx) {
			Date queryDate =
				DayOfWeekConversion.wd2cal(
					weekAvailability.get(availIdx)
				).getTime();
			System.out.println("Compare converted date: "+queryDate);
			if (queryDate.equals(reqDate)) {
				cont = true;
				break;
			}
		}
		// Quit if weekAvailability contains no matching date
		if (!cont) return false;
		
		// Count how many employees are free at reqDate
		int freeEmpCount;
		for (freeEmpCount = 0; availIdx < weekAvailability.size();
			++availIdx, ++freeEmpCount)
		{
			if (DayOfWeekConversion.wd2cal(
					weekAvailability.get(availIdx)
				).getTime().equals(reqDate))
			{
				continue;
			} else break;
		}
		
		// Count how many appointments are occuring at reqDate
		// FIXME: THIS IS STARTING FROM THE START OF APPOINTMENTS
		// AND BREAKING IMMEDIATELY. FIRST PARSE FROM THE START OF THE ARRAY
		// UNTIL WE FIND IT, THEN BREAK WHEN WE ARE DONE
		
		// Find the array index of the first appointment
		int aptCount = 0;
		int aptIdx;
		for (aptIdx = 0; aptIdx < weekAppointments.size(); ++aptIdx) {
			// Get the date
			Date aptDate = weekAppointments.get(aptIdx).getDate();
			System.out.println("aptDate="+aptDate);
			System.out.println("reqDate="+reqDate);
			if (aptDate.equals(reqDate)) {
				++aptCount;
				break;
			} else continue;
		}
		
		// Starting from the second appointment, keep adding until
		// we reach something else or end of array
		for (++aptIdx; aptIdx < weekAppointments.size(); ++aptIdx) {
			// Get the date
			Date aptDate = weekAppointments.get(aptIdx).getDate();
			System.out.println("aptDate="+aptDate);
			System.out.println("reqDate="+reqDate);
			if (aptDate.equals(reqDate)) {
				++aptCount;
				continue;
			} else break;
		}
		
		// If there are more availabilities than appointments occuring, the
		// time slot must be bookable (but we don't know who the employee
		// will be)
		System.out.println("freeEmpCount="+freeEmpCount);
		System.out.println("aptCount="+aptCount);
		if (freeEmpCount - aptCount > 0) {
			System.out.println("Yea");
			return true;
		} else {
			System.out.println("Nay");
			return false;
		}
	}
	
	/** Updates the workingHours attribute of the employee
	 * 
	 * @param employeeID : use getEmployeeID(String employeeName) to ensure valid ID
	 * @param workingHours : takes an ArrayList with new workingHours
	 */
	public void setWorkingHours(long employeeID, ArrayList<WeekDate> workingHours){
		for(int i = 0; i < employees.size(); i++){
			if(employeeID == employees.get(i).id){
				employees.get(i).setWorkingHours(workingHours);
			}
		}
	}
	
	/** Returns the current working hours of the employee
	 * 
	 * @param employeeID : use getEmployeeID(String employeeName) to ensure valid ID
	 * @return an ArrayList<Date> of current workingHours
	 */
	public ArrayList<WeekDate> getWorkingHours(long employeeID){
		for(int i = 0; i < employees.size(); i++){
			if(employeeID == employees.get(i).id){
				return (employees.get(i).workingHours);
			}
		}
		return null;
	}
	
	public ArrayList<String> getEmployeeNameIDs() {
		try {
			return dbm.getEmployeeNameIDs();
		} catch (SQLException sqle) {
			ArrayList<String> err = new ArrayList<String>();
			err.add("DB Error");
			return err;
		}
	}

}
