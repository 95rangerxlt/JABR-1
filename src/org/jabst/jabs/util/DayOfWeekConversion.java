package org.jabst.jabs.util;

import java.time.DayOfWeek;
import java.util.Calendar;

/** Static methods for converting between DayOfWeek and Calendar representations
  * of the day of week
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
