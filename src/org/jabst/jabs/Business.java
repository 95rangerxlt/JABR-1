package org.jabst.jabs;

public class Business {

	// Variables
    String username;
	String businessName;
	String businessOwner;
	String address;
	String phone;

	/** Sole onstructor. Only the database should construct Businesses */
	Business(String username, String businessName, String businessOwner, String address, String phone){
		this.username = username;
		this.businessName = businessName;
		this.businessOwner = businessOwner;
		this.address = address;
		this.phone = phone;
	}

	/** Not user-friendly for output @see BusinessSelection */
	public String toString() {
		return (
			"businessName="+businessName
		   +"\nbusinessOwner="+businessOwner
		   +"\naddress="+address
		   +"\nphone="+phone
		);
	}
}
