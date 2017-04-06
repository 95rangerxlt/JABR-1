package org.jabst.jabs;

import java.util.ArrayList;

public class Timetable {

	public static enum CellStatus {
		FREE, BOOKED, BOOKED_BY_YOU
	}

	public ArrayList<ArrayList<CellStatus>> table = new ArrayList<ArrayList<CellStatus>>()/* {
		{CellStatus.FREE,			CellStatus.BOOKED,			CellStatus.BOOKED_BY_YOU},
		{CellStatus.FREE,			CellStatus.BOOKED_BY_YOU,	CellStatus.BOOKED_BY_YOU},
		{CellStatus.BOOKED,			CellStatus.BOOKED,			CellStatus.BOOKED_BY_YOU},
		{CellStatus.BOOKED_BY_YOU,	CellStatus.BOOKED,			CellStatus.FREE},
		{CellStatus.FREE,			CellStatus.FREE,			CellStatus.BOOKED}
	}*/;
}