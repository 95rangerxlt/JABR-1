package org.jabst.jabs;

import java.util.ArrayList;
import java.util.Date;

public class EmployeeManager {
	//Fields
	ArrayList<Employee> employees = new ArrayList<Employee>();
	
	
	//Methods
	
	/** Creates a new employee object
	 * 
	 * @param employeeName : the name of the new employee
	 */
	public void createEmployee(String employeeName){
		Employee employee = new Employee(employeeName);
		employees.add(employee);
		// dbm.addEmployee();
	}

	/** Gets Employee by name
	*
	* @param employeeName : name of the employee you are looking for
	*/
	public Employee getEmployee(String employeeName) {
		for(int i = 0; i < employees.size(); i++) {
			if(employees.get(i).getName().equals(employeeName)) {
				return employees.get(i);
			}
		}
		// if it gets here, shits fucked
		return new Employee();
	}

	/** Gets Employee by id
	*
	* @param id : the id of the emplyee you are looking for
	*/
	public Employee getEmployee(int id) {
		for(int i = 0; i < employees.size(); i++) {
			if(employees.get(i).getId() == id) {
				return employees.get(i);
			}
		}
		// if it gets here, shits fucked
		return new Employee();
	}

	/** Adds employee objects to the ArrayList
	 * Designed to accept input from the database
	 * @param employee : The employee object to add
	 */
	public void loadEmployee(Employee employee){
		employees.add(employee);
	}
	
	/** Returns the unique ID of an employee
	 * 
	 * @param employeeName : name of the employee
	 * @return a long containing the unique ID of the employee or null
	 */
	public long getEmployeeID(String employeeName){
		for(int i = 0; i < employees.size(); i++){
			if (employeeName.equals(employees.get(i).getName())){
				return employees.get(i).id;
			}		
		}
		return 0;
	}
	
	/** Removes employee from the ArrayList
	 * 
	 * @param employeeName : use getEmployeeID(String employeeName) to ensure valid ID
	 */
	public void removeEmployee(long employeeID) {
		for(int i = 0; i < employees.size(); i++) {
			if (employeeID == employees.get(i).getId()) {
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
				return employees.get(i).getWorkingHours();
			}
		}
		return new ArrayList<Date>();
	}

}
