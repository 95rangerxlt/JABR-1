package org.jabst.jabs;

import org.jabst.jabs.util.*;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.time.DayOfWeek;

import org.jabst.jabs.util.DateTableConversion;

public class Employee {

	public long id;
	String name;
	ArrayList<WeekDate> workingHours;
	ArrayList<Date> appointmentHours;
	ArrayList<Appointment> appointments;
	Calendar startDate;
	Timetable table;
	public int hoursInADay = 8;
	public static int startingHour = 9;

	public Employee(long id, String name,
		ArrayList<WeekDate> workingHours, ArrayList<Appointment> appointments) {
		this.name = name;
		this.id = id;
		this.workingHours = workingHours;
		this.appointments = appointments;

		if(workingHours.size() > 0) {
			table = createTableFromWeekDates(workingHours);
		}
	}

	public void createWeekDatesFromTable() {
		this.workingHours = new ArrayList<WeekDate>();//reset data

		if(table.table.size() > 0) {//make sure there is data
			// nested loops for nested arrays
			for(int i = 0; i < table.table.size(); i++) {//days
				for(int j = 0; j < table.table.get(0).size(); j++) {//hours

					if(table.table.get(i).get(j) == Timetable.CellStatus.BOOKED_BY_YOU) {
						// create weekdate
						WeekDate timeSlot = new WeekDate(DayOfWeek.of(i), 0);
                        // Hour = cellIdx + startingHour
						timeSlot.setTimeOfDayHour(j+startingHour);
                        this.workingHours.add(timeSlot);
					}
				}
			}
		}
        System.out.println(this.workingHours);
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

	public Timetable createTableFromWeekDates(ArrayList<WeekDate> dates) {
		table = new Timetable(true);
		System.out.println("creating table from " + dates.size() + " shifts");
		table.createBlankTables();

		// fill tables
		for(int i = 0; i < dates.size(); i++) {
			int dayIdx = dates.get(i).getDayOfWeek().getValue();
			int hourIdx = dates.get(i).getStartingHour() - startingHour;

            if (hourIdx < 0) {
                System.err.println("Discarding out of range "
                        +"WeekDate:"+dates.get(i));
                continue;
            }

			table.table.get(dayIdx).set(hourIdx, Timetable.CellStatus.BOOKED_BY_YOU);
		}
		return table;
	}


	private Calendar maxDate(ArrayList<Calendar> list) {
		Calendar max = list.get(0);
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).compareTo(max) > 0) {
				max = list.get(i);
			}
		}
		return max;
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

	private WeekDate maxWeekDate(ArrayList<WeekDate> list) {
		WeekDate max = list.get(0);
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).compareTo(max) > 0) {
				max = list.get(i);
			}
		}
		return max;
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

    public String toString() {
        return "EMPLOYEE TOSTRING NOT FINISHED";
    }

}
