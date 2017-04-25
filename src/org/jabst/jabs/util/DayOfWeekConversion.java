package org.jabst.jabs.util;

import java.time.DayOfWeek;
import java.util.Calendar;

/** Static methods for converting between DayOfWeek and Calendar representations
  * of the day of week
  */
public class DayOfWeekConversion {

    /** Converts an integer from Calendar.SUNDAY, MONDAY etc into the
      * equivalent ordinal used by DayOfWeek.
      * Use DayOfWeek.of(int) to convert to a DayOfWeek.
      */
    public static int cal2dow(int cal) {
        cal -= 2;
        cal = (cal == -1 ? 6 : cal );
        return cal;
    }
    
    /** Converts in integer from DayOfWeek.ordinal() to the equivalent
      * ordinal used by Calendar.SUNDAY, MONDAY etc.
      * Use Calendar.set(DAY_OF_WEEK, int) to set a calendar to this
      * day of week.
      */
    public static int dow2cal(int dow) {
        dow += 2;
        dow = (dow == 8 ? 1 : dow);
        return dow;
    }

    /** Main is used to test this class */
    public static void main(String[] args) {
        System.out.println("Test: WeekDay Conversion Calendar -> DayOfWeek MON-FRI");
        
        System.out.println("DayOfWeek:"+(DayOfWeek.MONDAY.ordinal()));
        System.out.println("DayOfWeek:"+(DayOfWeek.TUESDAY.ordinal()));
        System.out.println("DayOfWeek:"+(DayOfWeek.WEDNESDAY.ordinal()));
        System.out.println("DayOfWeek:"+(DayOfWeek.THURSDAY.ordinal()));
        System.out.println("DayOfWeek:"+(DayOfWeek.FRIDAY.ordinal()));
        System.out.println("DayOfWeek:"+(DayOfWeek.SATURDAY.ordinal()));
        System.out.println("DayOfWeek:"+(DayOfWeek.SUNDAY.ordinal()));
        
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
        
        System.out.println("DayOfWeek:"+dow2cal(DayOfWeek.SUNDAY.ordinal()));
        System.out.println("DayOfWeek:"+dow2cal(DayOfWeek.MONDAY.ordinal()));
        System.out.println("DayOfWeek:"+dow2cal(DayOfWeek.TUESDAY.ordinal()));
        System.out.println("DayOfWeek:"+dow2cal(DayOfWeek.WEDNESDAY.ordinal()));
        System.out.println("DayOfWeek:"+dow2cal(DayOfWeek.THURSDAY.ordinal()));
        System.out.println("DayOfWeek:"+dow2cal(DayOfWeek.FRIDAY.ordinal()));
        System.out.println("DayOfWeek:"+dow2cal(DayOfWeek.SATURDAY.ordinal()));

    }
}