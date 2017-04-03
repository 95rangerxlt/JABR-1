package org.jabst.jabs;

public class Business {

	// Variables
	String businessName;
	String businessOwner;
	String address;
	String phone;

	// Methods
		Business(String businessName, String businessOwner, String address, String phone){
			this.businessName = businessName;
			this.businessOwner = businessOwner;
			this.address = address;
			this.phone = phone;
		}

	
	public String toString() {
		return (
			"businessName="+businessName
		   +"\nbusinessOwner="+businessOwner
		   +"\naddress="+address
		   +"\nphone="+phone
		);
	}
}
