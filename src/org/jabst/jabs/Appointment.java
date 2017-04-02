package org.jabst.jabs;

import java.util.Date;

/** Holds information about an appointment */
public class Appointment {
    
    private Date dateAndTime;
    private int appointmentType;
    private int employee;
    private String customer;
    
    public Appointment (Date dateAndTime, int appointmentType,
        int employee, String customer)
    {
        this.dateAndTime = dateAndTime;
        this.appointmentType = appointmentType;
        this.employee = employee;
        this.customer = customer;
        
    }
    
    public Date getDate() {
        return dateAndTime;
    }
    
    public int getAppointmentType() {
        return appointmentType;
    }
    
    public int getEmployee() {
        return employee;
    }
    
    public String getCustomer() {
        return customer;
    }
    
}