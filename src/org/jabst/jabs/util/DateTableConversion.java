package org.jabst.jabs.util;

import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;

import org.jabst.jabs.Timetable;

/** Static class for conversions between ArrayList<Date> or ArrayList<WeekDate>
  * and Timetable. All methods are static and do not involve state. Employee and
  * AddEmployeeGUI in particular will make use of these methods.
  */
public final class DateTableConversion {

    /** Takes an ArrayList<Date> and returns a corresponding timetable.
      * The conversion starts from the startingHour and goes for hoursInADay
      * entries. It also updateds the vlaue of startDate by finding the first
      * date entry in dates
      * @param dates The dates to convert
      * @param startDate The object which will have its value set to the first
      * found date
      * @param hoursInADay The number of hours in a day, beyond which no more
      * cells will be added to the table after startingHour
      * @param startingHour The hour of the day to start the table on.
      */
    public static Timetable createTableFromDates(
        ArrayList<Date> dates,
        Calendar startDate,
        int hoursInADay,
        int startingHour
        )
    {
        Timetable table = new Timetable(true);
        System.out.println("creating table from " + dates.size() + " shifts");
        ArrayList<Calendar> hoursCalendar = getCalendars(dates);
        Calendar min = minDate(hoursCalendar);
        startDate = (Calendar)min.clone();
        Calendar max = maxDate(hoursCalendar);
        int days = max.get(Calendar.DAY_OF_YEAR) - min.get(Calendar.DAY_OF_YEAR);

        // fill tables
        for(int i = 0; i < hoursCalendar.size(); i++) {

            System.out.println("i="+i);
            System.out.println("row index:");
            System.out.println("hoursCalendar.get(i).get(Calendar.DAY_OF_YEAR) - min.get(Calendar.DAY_OF_YEAR) = "
                +hoursCalendar.get(i).get(Calendar.DAY_OF_YEAR) +"-"
                +min.get(Calendar.DAY_OF_YEAR)+"="
                +(hoursCalendar.get(i).get(Calendar.DAY_OF_YEAR)-min.get(Calendar.DAY_OF_YEAR))
            );
            int getting = (hoursCalendar.get(i).get(Calendar.DAY_OF_YEAR) - min.get(Calendar.DAY_OF_YEAR));
            
            int cellIdx = hoursCalendar.get(i).get(Calendar.HOUR_OF_DAY)-startingHour;
            
            if (cellIdx < 0 || cellIdx > hoursInADay) {
                System.out.println(
                    "Discarding out of hours entry for employee hours:"
                    +hoursCalendar.get(i).toString()
                );
                continue;
            }
            System.out.println("setting cell: " + cellIdx);
            table.table.get(getting).set(cellIdx, Timetable.CellStatus.BOOKED_BY_YOU);
        }
        System.out.println("end of createTableFromDates");
        // TODO: clean up all these println statements
        return table;
    }

    /** Converts ArrayList<Date> to corresponding ArrayList<Calendar> */
    public static ArrayList<Calendar> getCalendars(ArrayList<Date> dates) {
        ArrayList<Calendar> calendars = new ArrayList<Calendar>();
        for(int i = 0; i < dates.size(); i++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dates.get(i));
            calendars.add(cal);
        }
        return calendars;
    }

    /** Finds the earliest dated calendar in an ArrayList<Calendar> */
    public static Calendar minDate(ArrayList<Calendar> list) {
        System.out.println("minDate:\n\tlist size: " + list.size());
        if(list.size() > 0) {
            Calendar min = list.get(0);
            for(int i = 0; i < list.size(); i++) {
                if(list.get(i).compareTo(min) < 0) {
                    min = list.get(i);
                }
            }
            return min;
        } else {
            return null;
        }
    }

    /** Finds the latest dated calendar in an ArrayList<Calendar> */
    public static Calendar maxDate(ArrayList<Calendar> list) {
        Calendar max = list.get(0);
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).compareTo(max) > 0) {
                max = list.get(i);
            }
        }
        return max;
    }

}