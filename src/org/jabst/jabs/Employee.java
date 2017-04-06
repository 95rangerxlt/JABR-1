package org.jabst.jabs;

import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Employee {

	public long id;
	String name;
	ArrayList<Date> workingHours;
	Calendar startDate;
	Timetable table;
	public int hoursInADay = 8;
	public int startingHour = 9;

	public Employee(String name) {
		this.name = name;
	}
	
	public Employee(String name, ArrayList<Date> hours) {
		this.name = name;
		this.workingHours = hours;

		if(hours.size() > 0) {
			createTableFromDates();
		}
	}

	public Employee(long id, String name, ArrayList<Date> hours) {
		this(name, hours);
		this.id = id;
	}

	public Employee() {
		this("Joe", new ArrayList<Date>());
	}

	public void createDatesFromTable() {
		workingHours = new ArrayList<Date>();//reset data

		if(table.table.size() > 0) {//make sure there is data
			// nested loops for nested arrays
			for(int i = 0; i < table.table.size(); i++) {//days
				for(int j = 0; j < table.table.get(0).size(); j++) {//hours

					if(table.table.get(i).get(j) == Timetable.CellStatus.BOOKED_BY_YOU) {
						// create date
						Calendar timeSlot = (Calendar)startDate.clone();
						timeSlot.add(Calendar.DAY_OF_YEAR, i);
						timeSlot.add(Calendar.HOUR_OF_DAY, j);
						workingHours.add(timeSlot.getTime());
					}
				}
			}
		}
	}

	public void createTableFromDates() {
		table = new Timetable();
		ArrayList<Calendar> hoursCalendar = getCalendars(workingHours);
		Calendar min = minDate(hoursCalendar);
		startDate = (Calendar)min.clone();
		Calendar max = maxDate(hoursCalendar);
		long diff = max.getTimeInMillis() - min.getTimeInMillis();
		int days = (int)TimeUnit.MILLISECONDS.toDays(Math.abs(diff));
		table.table = new ArrayList<ArrayList<Timetable.CellStatus>>();

		// create tables
		for(int d = 0; d < days; d++) {
			table.table.add(new ArrayList<Timetable.CellStatus>());
			for(int h = 0; h < hoursInADay; h++) {
				table.table.get(d).add(Timetable.CellStatus.FREE);
			}
		}

		// fill tables
		for(int i = 0; i < hoursCalendar.size(); i++) {
			table.table.get(hoursCalendar.get(i).get(Calendar.DAY_OF_YEAR) - min.get(Calendar.DAY_OF_YEAR)).set(hoursCalendar.get(i).get(Calendar.HOUR_OF_DAY)-startingHour, Timetable.CellStatus.BOOKED_BY_YOU);
		}
	}

	private ArrayList<Calendar> getCalendars(ArrayList<Date> list) {
		ArrayList<Calendar> newList = new ArrayList<Calendar>();
		for(int i = 0; i < list.size(); i++) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(list.get(i));
			newList.add(cal);
		}
		return newList;
	}
	

	private Calendar minDate(ArrayList<Calendar> list) {
		Calendar min = list.get(0);
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).compareTo(min) < 0) {
				min = list.get(i);
			}
		}
		return min;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public ArrayList<Date> getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(ArrayList<Date> workingHours) {
		this.workingHours = workingHours;
	}


}