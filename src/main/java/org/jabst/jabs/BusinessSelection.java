package org.jabst.jabs;

/** A wrapper around a business for nice toString() output */
public class BusinessSelection {
    public Business business;
    public BusinessSelection(Business b) {
        this.business = b;
    }
    public String toString() {
        return business.businessName;
    }
}