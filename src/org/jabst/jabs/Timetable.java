package org.jabst.jabs;

public class Timetable {

	public static enum CellStatus {
		FREE, BOOKED, BOOKED_BY_YOU
	}

	public CellStatus table[][] = new CellStatus[][] {
		{CellStatus.FREE,			CellStatus.BOOKED,			CellStatus.BOOKED_BY_YOU},
		{CellStatus.FREE,			CellStatus.BOOKED_BY_YOU,	CellStatus.BOOKED_BY_YOU},
		{CellStatus.BOOKED,			CellStatus.BOOKED,			CellStatus.BOOKED_BY_YOU},
		{CellStatus.BOOKED_BY_YOU,	CellStatus.BOOKED,			CellStatus.FREE},
		{CellStatus.FREE,			CellStatus.FREE,			CellStatus.BOOKED}
	};
}