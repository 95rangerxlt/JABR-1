package org.jabst.jabs.util;

import java.time.DayOfWeek;
import java.util.Calendar;
import org.jabst.jabs.WeekDate;

/** Static methods for converting between DayOfWeek and Calendar representations
  * of the day of week, and between Calendar objects and day of week objects
  */
public class DayOfWeekConversion {

    /** Converts an integer from Calendar.SUNDAY, MONDAY etc into the
      * equivalent getValue used by DayOfWeek.
      * Use DayOfWeek.of(int) to convert to a DayOfWeek.
      */
    public static int cal2dow(int cal) {
        cal -= 1;
        cal = (cal == 0 ? 7 : cal );
        return cal;
    }
    
    /** Converts in integer from DayOfWeek.getValue() to the equivalent
      * integer value  used by Calendar.SUNDAY, MONDAY etc.
      * Use Calendar.set(DAY_OF_WEEK, int) to set a calendar to this
      * day of week.
      */
    public static int dow2cal(int dow) {
        dow += 1;
        dow = (dow == 8 ? 1 : dow);
        return dow;
    }
    
    /** Converts a WeekDate into a Calendar for this week
      * @param wd The WeekDate to convert to a calendar
      * @return A Calendar which is set to the day of week and time of day
      * in the WeekDate, and to this week in absolute value.
      */
    public static Calendar wd2cal (WeekDate wd) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, wd.getStartingHour());
        c.set(Calendar.MINUTE, (wd.getTime()/60)%60);
        c.set(Calendar.SECOND, wd.getTime()%60);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DAY_OF_WEEK,
            DayOfWeekConversion.dow2cal(wd.getDayOfWeek().getValue()));
        return c;
    }
    
    /** Converts a Calendar into the corresponding WeekDate
      * @param c The Calendar to be converted
      * @return A WeekDate with the time of day and day of week
      * found in the Calendar.
      */
    public static WeekDate cal2wd (Calendar c) {
        /* WeekDate(DayOfWeek, timeOfDay */
        return new WeekDate (
            DayOfWeek.of(
                DayOfWeekConversion.cal2dow(c.get(Calendar.DAY_OF_WEEK))
            ),
              c.get(Calendar.HOUR)*3600
            + c.get(Calendar.MINUTE)*60
            + c.get(Calendar.SECOND)
        );
    }

    /** Main is used to test this class */
    public static void main(String[] args) {
        System.out.println("Test: WeekDay Conversion Calendar -> DayOfWeek MON-FRI");
        
        System.out.println("DayOfWeek:"+(DayOfWeek.MONDAY.getValue()));
        System.out.println("DayOfWeek:"+(DayOfWeek.TUESDAY.getValue()));
        System.out.println("DayOfWeek:"+(DayOfWeek.WEDNESDAY.getValue()));
        System.out.println("DayOfWeek:"+(DayOfWeek.THURSDAY.getValue()));
        System.out.println("DayOfWeek:"+(DayOfWeek.FRIDAY.getValue()));
        System.out.println("DayOfWeek:"+(DayOfWeek.SATURDAY.getValue()));
        System.out.println("DayOfWeek:"+(DayOfWeek.SUNDAY.getValue()));
        
        System.out.println("Calendar-2:"+cal2dow(Calendar.MONDAY));
        System.out.println("Calendar-2:"+cal2dow(Calendar.TUESDAY));
        System.out.println("Calendar-2:"+cal2dow(Calendar.WEDNESDAY));
        System.out.println("Calendar-2:"+cal2dow(Calendar.THURSDAY));
        System.out.println("Calendar-2:"+cal2dow(Calendar.FRIDAY));
        System.out.println("Calendar-2:"+cal2dow(Calendar.SATURDAY));
        System.out.println("Calendar-2:"+cal2dow(Calendar.SUNDAY));
        
        System.out.println("\nTest: WeekDay Conversion Calendar -> DayOfWeek SUN-SAT");
        
        System.out.println("Calendar"+Calendar.SUNDAY);
        System.out.println("Calendar"+Calendar.MONDAY);
        System.out.println("Calendar"+Calendar.TUESDAY);
        System.out.println("Calendar"+Calendar.WEDNESDAY);
        System.out.println("Calendar"+Calendar.THURSDAY);
        System.out.println("Calendar"+Calendar.FRIDAY);
        System.out.println("Calendar"+Calendar.SATURDAY);
        
        System.out.println("DayOfWeek:"+dow2cal(DayOfWeek.SUNDAY.getValue()));
        System.out.println("DayOfWeek:"+dow2cal(DayOfWeek.MONDAY.getValue()));
        System.out.println("DayOfWeek:"+dow2cal(DayOfWeek.TUESDAY.getValue()));
        System.out.println("DayOfWeek:"+dow2cal(DayOfWeek.WEDNESDAY.getValue()));
        System.out.println("DayOfWeek:"+dow2cal(DayOfWeek.THURSDAY.getValue()));
        System.out.println("DayOfWeek:"+dow2cal(DayOfWeek.FRIDAY.getValue()));
        System.out.println("DayOfWeek:"+dow2cal(DayOfWeek.SATURDAY.getValue()));

    }
}
