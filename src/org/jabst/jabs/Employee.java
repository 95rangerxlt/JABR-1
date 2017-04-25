package org.jabst.jabs;

import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.jabst.jabs.util.DateTableConversion;

public class Employee {

	public long id;
	String name;
	ArrayList<WeekDate> workingHours;
	ArrayList<Date> appointmentHours;
	Calendar startDate;
	Timetable table;
	public int hoursInADay = 8;
	public int startingHour = 9;

	public Employee(long id, String name,
		ArrayList<WeekDate> workingHours, ArrayList<Date> appointmentHours) {
		this.name = name;
		this.id = id;
		this.workingHours = workingHours;
		this.appointmentHours = appointmentHours;

		if(workingHours.size() > 0) {
			table = createTableFromDates();
		}
	}


	public static ArrayList<Date> createDatesFromTable(Timetable table, Calendar startDate) {
		ArrayList<Date> dates = new ArrayList<Date>();

		if(table.table.size() > 0) {//make sure there is data
			// nested loops for nested arrays
			for(int i = 0; i < table.table.size(); i++) {//days
				for(int j = 0; j < table.table.get(0).size(); j++) {//hours

					if(table.table.get(i).get(j) == Timetable.CellStatus.BOOKED_BY_YOU) {
						// create date
						Calendar timeSlot = (Calendar)startDate.clone();
						timeSlot.add(Calendar.DAY_OF_YEAR, i);
						timeSlot.add(Calendar.HOUR_OF_DAY, j);
						dates.add(timeSlot.getTime());
					}
				}
			}
		}
		return dates;
	}

	public Timetable createTableFromDates() {
		// FIXME: Temporarily using appointments
		// instead of working hours so it compiles
		return DateTableConversion.createTableFromDates(
            appointmentHours,
            startDate,
            hoursInADay,
            startingHour
        );
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public ArrayList<WeekDate> getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(ArrayList<WeekDate> workingHours) {
		this.workingHours = workingHours;
	}


}