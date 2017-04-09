package org.jabst.jabs;

import javafx.scene.layout.GridPane;// grid layout
import javafx.scene.control.*;//buttons, labels  etc.
import javafx.geometry.Insets;//insets = padding
import javafx.scene.paint.Color;

import java.util.ArrayList;

class TimetableGUI extends GridPane {

	public ArrayList<ArrayList<Timetable.CellStatus>> table;
	public TimetableCellGUI cells[][];
	public TimetableCellGUI.Type type;

	public TimetableGUI() {
		this(new Timetable(true));
	}
	
	public TimetableGUI(Timetable table) {
		this.table = table.table;
		System.out.println("TimetableGUI:table.table.size="+table.table.size());
		if(table.table.size() > 0)
			cells = new TimetableCellGUI[this.table.size()][this.table.get(0).size()];
		else
			cells = new TimetableCellGUI[0][0];
		type = TimetableCellGUI.Type.CHECKBOX;
		setupSpacing();
		update();
	}

	public void setTable(ArrayList<ArrayList<Timetable.CellStatus>> table) {
		this.table = table;
		update();
	}

	public ArrayList<ArrayList<Timetable.CellStatus>> getTable() {
		return this.table;
	}

	public void update() {
		if(this.table.size() == 0)
			return;
		for(int j = 0; j < this.table.get(0).size(); j++) {
			for(int i = 0; i < this.table.size(); i++) {
				switch(this.table.get(i).get(j)) {
					case FREE:
						cells[i][j] = new TimetableCellGUI(type, "FREE", 120, 40);
						cells[i][j].border.setFill(Color.WHITE);
					break;
					case BOOKED:
						cells[i][j] = new TimetableCellGUI(TimetableCellGUI.Type.NONE, "BOOKED", 120, 40);
						cells[i][j].border.setFill(Color.RED);
					break;
					case BOOKED_BY_YOU:
						cells[i][j] = new TimetableCellGUI(type, "BOOKED_BY_YOU", 120, 40);
						cells[i][j].border.setFill(Color.LIGHTGRAY);
					break;
				}
				cells[i][j].border.setStroke(Color.GRAY);

				this.add(cells[i][j], i, j);
			}
		}
	}

	public void setupSpacing() {
		this.setPadding(new Insets(3, 3, 3, 3));
		this.setVgap(2);
		this.setHgap(2);
	}
}