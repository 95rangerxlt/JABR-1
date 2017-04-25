package org.jabst.jabs;

import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.time.DayOfWeek;

public class Employee {

	public long id;
	String name;
	ArrayList<WeekDate> workingHours;
	ArrayList<Date> appointmentHours;
	WeekDate startDate;
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
			table = createTableFromWeekDates(workingHours);
		}
	}

	public void createWeekDatesFromTable() {
		workingHours = new ArrayList<WeekDate>();//reset data

		if(table.table.size() > 0) {//make sure there is data
			// nested loops for nested arrays
			for(int i = 0; i < table.table.size(); i++) {//days
				for(int j = 0; j < table.table.get(0).size(); j++) {//hours

					if(table.table.get(i).get(j) == Timetable.CellStatus.BOOKED_BY_YOU) {
						// create weekdate
						WeekDate timeSlot = new WeekDate(DayOfWeek.of(i), 0);
						timeSlot.setTimeOfDayHour(j);
					}
				}
			}
		}
	}
/*
	public void createDatesFromTable() {
		workingHours = new ArrayList<WeekDate>();//reset data

		if(table.table.size() > 0) {//make sure there is data
			// nested loops for nested arrays
			for(int i = 0; i < table.table.size(); i++) {//days
				for(int j = 0; j < table.table.get(0).size(); j++) {//hours

					if(table.table.get(i).get(j) == Timetable.CellStatus.BOOKED_BY_YOU) {
						// create date
						//TODO: Change from Calendar conversion to WeekDay conversion
						WeekDate timeSlot = (Calendar)startDate.clone();
						timeSlot.add(Calendar.DAY_OF_YEAR, i);
						timeSlot.add(Calendar.HOUR_OF_DAY, j);
						workingHours.add(timeSlot.getTime());
					}
				}
			}
		}

		System.out.println("dates: \n" + workingHours.toString());
	}
*/
	public Timetable createTableFromWeekDates(ArrayList<WeekDate> dates) {
		table = new Timetable(true);
		System.out.println("creating table from " + dates.size() + " shifts");
		WeekDate min = minWeekDate(dates);
		startDate = min;
		table.createBlankTables();

		// fill tables
		for(int i = 0; i < dates.size(); i++) {
			int dayIdx = dates.get(i).getDayOfWeek().getValue();
			int hourIdx = dates.get(i).getStartingHour() - startingHour;

			table.table.get(dayIdx).set(hourIdx, Timetable.CellStatus.BOOKED_BY_YOU);
		}
		return table;
	}
/*
	public Timetable createTableFromDates() {
		table = new Timetable(true);
		System.out.println("creating table from " + workingHours.size() + " shifts");
		ArrayList<Calendar> hoursCalendar = getCalendars(workingHours);
		Calendar min = minDate(hoursCalendar);
		startDate = (Calendar)min.clone();
		Calendar max = maxDate(hoursCalendar);
		int days = max.get(Calendar.DAY_OF_YEAR) - min.get(Calendar.DAY_OF_YEAR);
		table.createBlankTables();

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
	}*/
/*
	private ArrayList<Calendar> getCalendars(ArrayList<WeekDate> list) {
		if(list.size() == 0) {
			System.out.println("cannot get calendars: \tlist size is 0");
		}
		ArrayList<Calendar> newList = new ArrayList<Calendar>();
		for(int i = 0; i < list.size(); i++) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(list.get(i));
			newList.add(cal);
		}
		return newList;
	}
	*/

	private Calendar minDate(ArrayList<Calendar> list) {
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
			System.out.println("calendar is null");
			return null;
		}
	}

	private WeekDate minWeekDate(ArrayList<WeekDate> list) {
		System.out.println("minDate:\n\tlist size: " + list.size());
		if(list.size() > 0) {
			WeekDate min = list.get(0);
			for(int i = 0; i < list.size(); i++) {
				if(list.get(i).compareTo(min) < 0) {
					min = list.get(i);
				}
			}
			return min;
		} else {
			System.out.println("calendar is null");
			return null;
		}
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

}