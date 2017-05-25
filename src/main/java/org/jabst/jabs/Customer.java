package org.jabst.jabs;

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

	// Constructors
	Customer(String username, String name, String address, String phone) {
		this.username = username;
		this.name = name;
		this.address = address;
		this.phone = phone;
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
