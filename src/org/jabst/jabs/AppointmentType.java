package org.jabst.jabs;

import java.time.Duration;

/** An AppointmentType represents a kind of appointment that can be booked
  * with a business.
  */
public class AppointmentType {
    /** Unique ID from database */
    private int typeID;
    /** How long the appointment goes for @see java.time.Duration */
    private Duration duration;
    private String humanReadableName;
    private int costCents;
    
    /** Construct a new AppointmentType with the given fields as values.
      * You will probably only want to do this for new AppointmentTypes;
      * otherwise it is suggested to use getByID
      */
    public AppointmentType(int typeID, Duration duration,
        String humanReadableName, int costCents) {
    
        this.typeID = typeID;
        this.duration = duration;
        this.humanReadableName = humanReadableName;
        this.costCents = costCents;
    }
    
    public int getTypeID() { return typeID; }
    public Duration getDuration() { return duration; }
    public String getHumanReadableName() { return humanReadableName; }
    public int getCostCents() { return costCents; }
    
    /** Saves this AppointmentType back to the database, overwriting it if it
        already exists */
    public boolean saveToDatabase(DatabaseManager dbm) {
        // NYI: dbm.saveAppointmentType(this);
        return true;
    }
    
    /** Asks the database for the AppointmentType with the given ID */
    public static AppointmentType getByID(int id) {
        // NYI: dbm.getAppointmentTypeByID(id)
        return null;
    }
}