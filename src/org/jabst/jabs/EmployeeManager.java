package org.jabst.jabs;

import java.util.ArrayList;
import java.util.Date;

import java.sql.SQLException;

public class EmployeeManager {
	//Fields
	ArrayList<Employee> employees = new ArrayList<Employee>();
	private DatabaseManager dbm;
	
	// Constructor
	public EmployeeManager(SessionManager sm) {
		this.dbm = sm.getDatabaseManager();
	}
	
	//Methods
	
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
				ArrayList<Date> availability = dbm.getSevenDayEmployeeAvailability();
				Employee emp = new Employee(-1, "allEmployees", availability, new ArrayList<Date>());
				return emp;
			} catch (SQLException sqle) {
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
	public void removeEmployee(String employeeID){
		for(int i = 0; i < employees.size(); i++){
			if (employees.get(i).name.equals(employees.get(i).name)){
				employees.remove(i);
			}		
		}
	}
	
	/** Updates the workingHours attribute of the employee
	 * 
	 * @param employeeID : use getEmployeeID(String employeeName) to ensure valid ID
	 * @param workingHours : takes an ArrayList with new workingHours
	 */
	public void setWorkingHours(long employeeID, ArrayList<Date> workingHours){
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
	public ArrayList<Date> getWorkingHours(long employeeID){
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
