package org.jabst.jabs;

import javafx.scene.layout.GridPane;// grid layout
import javafx.scene.control.*;//buttons, labels  etc.
import javafx.geometry.Insets;//insets = padding
import javafx.scene.paint.Color;

class TimetableGUI extends GridPane {

	public Timetable.CellStatus table[][];
	public TimetableCellGUI cells[][];
	public TimetableCellGUI.Type type;
	
	public TimetableGUI(Timetable table) {
		this.table = table.table;
		cells = new TimetableCellGUI[this.table.length][this.table[0].length];
		type = TimetableCellGUI.Type.CHECKBOX;
		update();
	}

	public void update() {
		for(int j = 0; j < this.table[0].length; j++) {
			for(int i = 0; i < this.table.length; i++) {
				switch(this.table[i][j]) {
					case FREE:
						cells[i][j] = new TimetableCellGUI(type, "FREE");
						cells[i][j].border.setFill(Color.WHITE);
					break;
					case BOOKED:
						cells[i][j] = new TimetableCellGUI(TimetableCellGUI.Type.NONE, "BOOKED");
						cells[i][j].border.setFill(Color.RED);
					break;
					case BOOKED_BY_YOU:
						cells[i][j] = new TimetableCellGUI(type, "BOOKED_BY_YOU");
						cells[i][j].border.setFill(Color.LIGHTGRAY);
					break;
				}
				cells[i][j].border.setStroke(Color.GRAY);

				this.add(cells[i][j], i, j);
			}
		}
	}

	public void setupSpacing() {
		this.setPadding(new Insets(10, 10, 10, 10));
		this.setVgap(8);
		this.setHgap(8);
	}
}