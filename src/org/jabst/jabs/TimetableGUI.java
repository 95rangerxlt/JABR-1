package org.jabst.jabs;

import javafx.scene.layout.GridPane;// grid layout
import javafx.scene.control.*;//buttons, labels  etc.
import javafx.geometry.Insets;//insets = padding
import javafx.scene.paint.Color;

import java.util.ArrayList;

class TimetableGUI extends GridPane {

	public boolean allEmployees = false;
	public ArrayList<ArrayList<Timetable.CellStatus>> table;//data
	public TimetableCellGUI cells[][];//GUI
	public TimetableCellGUI.Type type;
	public Timetable timetable;

	public TimetableGUI() {
		this(new Timetable(true));
	}
	
	public TimetableGUI(Timetable table) {
		this.table = table.table;
		this.timetable = table;
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

	//sets data from GUI
	public void updateTableFromCells() {
		if(this.cells.length == 0)
			return;//something went wrong
		if(this.cells[0].length == 0)
			return;//something went wrong
		for(int j = 0; j < cells[0].length; j++) {
			for(int i = 0; i < cells.length; i++) {
				if(cells[i][j].selectable.isSelected()) {
					this.table.get(i).set(j, Timetable.CellStatus.BOOKED_BY_YOU);
				} else {
					this.table.get(i).set(j, Timetable.CellStatus.FREE);
				}
			}
		}
		this.timetable.table = this.table;
	}

	//sets GUI from data
	public void update() {
		if(this.table.size() == 0)
			return;//something went wrong
		for(int j = 0; j < this.table.get(0).size(); j++) {
			for(int i = 0; i < this.table.size(); i++) {
				if(allEmployees) {
					cells[i][j] = new TimetableCellGUI(TimetableCellGUI.Type.NONE, new String[] {"", "", ""}, false, 120, 40);
					switch(this.table.get(i).get(j)) {
						case FREE:
							// System.out.println("setting border fill to WHITE");
							cells[i][j].border.setFill(Color.WHITE);
						break;
						case BOOKED:
							// System.out.println("setting border fill to RED");
							cells[i][j].border.setFill(Color.RED);
						break;
						case BOOKED_BY_YOU:
							// System.out.println("setting border fill to LIGHTGRAY");
							cells[i][j].border.setFill(Color.LIGHTGRAY);
						break;
					}
					cells[i][j].border.setStroke(Color.GRAY);
					this.add(cells[i][j], i, j);
					continue;
				}
				switch(this.table.get(i).get(j)) {
					case FREE:
						cells[i][j] = new TimetableCellGUI(type, new String[] {"FREE", "BOOKED_BY_YOU", "BOOKED"}, false, 120, 40);
						// cells[i][j].border.setFill(Color.WHITE);
					break;
					case BOOKED:
						cells[i][j] = new TimetableCellGUI(TimetableCellGUI.Type.NONE, new String[] {"FREE", "BOOKED_BY_YOU", "BOOKED"}, false, 120, 40);
						cells[i][j].border.setFill(Color.RED);//special case, color is not decided by selectable
					break;
					case BOOKED_BY_YOU:
						cells[i][j] = new TimetableCellGUI(type, new String[] {"FREE", "BOOKED_BY_YOU", "BOOKED"}, true, 120, 40);
						// cells[i][j].border.setFill(Color.LIGHTGRAY);
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

	public void removeData() {
		if(this.table.size() == 0)
			return;//something went wrong
		for(int j = 0; j < this.table.get(0).size(); j++) {
			for(int i = 0; i < this.table.size(); i++) {
				this.table.get(i).set(j, Timetable.CellStatus.FREE);
			}
		}
	}
}