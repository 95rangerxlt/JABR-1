package org.jabst.jabs;

import java.util.Date;
import java.time.Duration;

/** Holds information about an appointment */
public class Appointment {
    
    private Date dateAndTime;
    /** This should refer to a valid type ID from the database */
    private int appointmentType;// that enum tho
    /** Should refer to a valid employee ID from the database */
    private int employee;
    /** Customer's name */
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
    
    public Duration getDuration() {
        // Ask the appointment type for the duration
        return AppointmentType.getByID(appointmentType).getDuration();
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