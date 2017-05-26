package org.jabst.jabs;

import java.util.ArrayList;
import java.util.Date;

/** Customer represents a customer's information as a Java object.
  * Customer information is entered into the database when the customer
  * registers, and retrieve from the database later as needed.
  */
public class Customer {
	// Variables
	/** Primary key in database. Should only contain letters. */
	String username;
	String name;
	String address;
	/** Must be 8-10 digits long */
	String phone;
	ArrayList<Date> appointmentTimes;

	// Constructors
	Customer(
		String username, String name,
		String address, String phone,
		ArrayList<Date> appointmentTimes)
	{
		this.username = username;
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.appointmentTimes = appointmentTimes;
	}
	
	// Methods
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Customer {");
		sb.append("username=\"");
		sb.append(username);
		sb.append("\", ");
		sb.append("name=\"");
		sb.append(name);
		sb.append("\", ");
		sb.append("address=\"");
		sb.append(address);
		sb.append("\", ");
		sb.append("phone=\"");
		sb.append(phone);
		sb.append("\"}");
		return sb.toString();
	}
}
