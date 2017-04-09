package org.jabst.jabs;

import java.util.ArrayList;

public class Timetable {

	public static enum CellStatus {
		FREE, BOOKED, BOOKED_BY_YOU
	};
	
	public ArrayList<ArrayList<CellStatus>> table;

	public Timetable(boolean blank) {
		if (blank) {
			table = new ArrayList<ArrayList<CellStatus>>();
			ArrayList<CellStatus> row;
			for (int rowIdx = 0; rowIdx < 7; ++rowIdx) {
				row = new ArrayList<CellStatus>();
				for (int cell = 0; cell < 8; ++cell) {
					row.add(CellStatus.FREE);
				}
				table.add(row);
			}
		}
		else {
			table = new ArrayList<ArrayList<CellStatus>>();
				ArrayList<CellStatus> r1 = new ArrayList<CellStatus>();
			r1.add(CellStatus.FREE);
			r1.add(CellStatus.BOOKED);
			r1.add(CellStatus.BOOKED_BY_YOU);
			table.add(r1);
				ArrayList<CellStatus> r2 = new ArrayList<CellStatus>();
			r2.add(CellStatus.FREE);
			r2.add(CellStatus.BOOKED_BY_YOU);
			r2.add(CellStatus.BOOKED_BY_YOU);
				ArrayList<CellStatus> r3 = new ArrayList<CellStatus>();
			r3.add(CellStatus.BOOKED);
			r3.add(CellStatus.BOOKED);
			r3.add(CellStatus.BOOKED_BY_YOU);
				ArrayList<CellStatus> r4 = new ArrayList<CellStatus>();
			r4.add(CellStatus.BOOKED_BY_YOU);
			r4.add(CellStatus.BOOKED);
			r4.add(CellStatus.FREE);
				ArrayList<CellStatus> r5 = new ArrayList<CellStatus>();
			r5.add(CellStatus.FREE);
			r5.add(CellStatus.FREE);
			r5.add(CellStatus.BOOKED);
			
			table.add(r1);
			table.add(r2);
			table.add(r3);
			table.add(r4);
			table.add(r5);
		}
	}

	void createBlankTables() {
		table = new ArrayList<ArrayList<CellStatus>>();
		ArrayList<CellStatus> row;
		for (int rowIdx = 0; rowIdx < 7; ++rowIdx) {
			row = new ArrayList<CellStatus>();
			for (int cell = 0; cell < 8; ++cell) {
				row.add(CellStatus.FREE);
			}
			table.add(row);
		}
	}
}