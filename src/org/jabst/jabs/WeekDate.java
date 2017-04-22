package org.jabst.jabs;

import java.time.DayOfWeek;

/** WeekDate represents a time and a day of week together.
 *  WeekDate is used to represent employee availability. It stores the time
 *  of day as 
 */
public class WeekDate {
    /** Monday, Tuesday, ... @see java.time.DayOfWeek */
    DayOfWeek dayOfWeek;
    /** Range: 0 <= timeOfDay <= 86399 */
    private int timeOfDay;
    
    public WeekDate(DayOfWeek dayOfWeek, int timeOfDay) {
        this.dayOfWeek = dayOfWeek;
        this.timeOfDay = timeOfDay;
    }
    
    /** Gets the hour (0-23) that the time of day starts at */
    public int getStartingHour() {
        return timeOfDay / 3600;
    }
    
    /** Sets the time of day to the given value in seconds.
      * Note: you may want to construct a new WeekDate instead
      */
    public boolean setTimeOfDay(int timeOfDay) {
        if (0 <= timeOfDay && timeOfDay <= 86399) {
            this.timeOfDay = timeOfDay;
            return true;
        } else return false;
    }
    
    /** Sets the time of day to the given hour (0-23)
      * Note: you may want to construct a new WeekDate instead
      */
    public boolean setTimeOfDayHour(int hour) {
        if (0 <= hour && hour <= 23) {
            timeOfDay = hour*3600;
            return true;
        } else return false;
    }
}