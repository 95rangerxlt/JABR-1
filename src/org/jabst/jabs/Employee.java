package org.jabst.jabs;

import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Employee {

	public long id;
	String name;
	ArrayList<Date> workingHours;
	ArrayList<Date> appointmentHours;
	Calendar startDate;
	Timetable table;
	public int hoursInADay = 8;
	public int startingHour = 9;
/*
	public Employee(String name) {
		this.name = name;
	}
	
	public Employee(String name, ArrayList<Date> hours, ) {
		this.name = name;
		this.workingHours = hours;

		if(hours.size() > 0) {
			createTableFromDates();
		}
	}
*/
	public Employee(long id, String name,
		ArrayList<Date> workingHours, ArrayList<Date> appointmentHours) {
		this.name = name;
		this.id = id;
		this.workingHours = workingHours;
		this.appointmentHours = appointmentHours;

		if(workingHours.size() > 0) {
			table = createTableFromDates();
		}
	}
/*
	public Employee() {
		this("Joe", new ArrayList<Date>());
	}*/

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

	public Timetable createTableFromDates() {
		table = new Timetable(true);
		ArrayList<Calendar> hoursCalendar = getCalendars(workingHours);
		Calendar min = minDate(hoursCalendar);
		startDate = (Calendar)min.clone();
		Calendar max = maxDate(hoursCalendar);
		int days = max.get(Calendar.DAY_OF_YEAR) - min.get(Calendar.DAY_OF_YEAR);
		table.createBlankTables();

		// create tables
		// for(int d = 0; d < days; d++) {
		// 	table.table.add(new ArrayList<Timetable.CellStatus>());
		// 	for(int h = 0; h < hoursInADay; h++) {
		// 		table.table.get(d).add(Timetable.CellStatus.FREE);
		// 	}
		// }

		// fill tables
		System.out.println("Filling tables...");
		System.out.println("hoursCalendar size: " + hoursCalendar.size());
		for(int i = 0; i < hoursCalendar.size(); i++) {

			System.out.println("i="+i);
			System.out.println("row index:");
			System.out.println("hoursCalendar.get(i).get(Calendar.DAY_OF_YEAR) - min.get(Calendar.DAY_OF_YEAR) = "
				+hoursCalendar.get(i).get(Calendar.DAY_OF_YEAR) +"-"
				+min.get(Calendar.DAY_OF_YEAR)+"="
				+(hoursCalendar.get(i).get(Calendar.DAY_OF_YEAR)-min.get(Calendar.DAY_OF_YEAR))
			);
			int getting = (hoursCalendar.get(i).get(Calendar.DAY_OF_YEAR) - min.get(Calendar.DAY_OF_YEAR));

			System.out.println("Size: " + table.table.size());
			System.out.println("getting: "+table.table.get(getting));

			System.out.println("getting int: "+getting);
			// ArrayList<Timetable.CellStatus> row =
			// table.table.get(getting);
			
			// System.out.println("hoursCalendar.get(i).get(Calendar.HOUR_OF_DAY)="+hoursCalendar.get(i).get(Calendar.HOUR_OF_DAY));
			// System.out.println("startingHour="+startingHour);
			
			// System.out.println(
			// "hoursCalendar.get(i).get(Calendar.HOUR_OF_DAY)-startingHour="
			// 	+(hoursCalendar.get(i).get(Calendar.HOUR_OF_DAY)-startingHour)
			// );
			
			
			int cellIdx = hoursCalendar.get(i).get(Calendar.HOUR_OF_DAY)-startingHour;
			System.out.println("hour of day: "+hoursCalendar.get(i).get(Calendar.HOUR_OF_DAY));
			System.out.println("cellIdx: " + cellIdx);

			System.out.println("timetable at 0: " + table.table.get(getting).get(0));
			
			if (cellIdx < 0 || cellIdx > hoursInADay) {
				System.out.println(
					"Discarding out of hours entry for employee hours:"
					+hoursCalendar.get(i).toString()
				);
				// continue;
			} else {
				System.out.println("setting cell: " + cellIdx);
				table.table.get(getting).set(cellIdx, Timetable.CellStatus.BOOKED_BY_YOU);
			}
			System.out.println("next entry in hoursCalendar");
		}
		System.out.println("end of createTableFromDates");
		// TODO: clean up all these println statements
		return table;
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