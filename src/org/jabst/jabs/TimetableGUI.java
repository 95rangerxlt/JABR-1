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
	ToggleGroup tg;

	public TimetableGUI() {
		this(new Timetable(true));
	}
	
	public TimetableGUI(Timetable table) {
		this.table = table.table;
		this.timetable = table;
		System.out.println("TimetableGUI:table.table.size (days) ="+table.table.size());
		if(table.table.size() > 0) {
			System.out.println("TimetableGUI:table.table.get(0).size (hours in first day) ="+table.table.get(0).size());
			cells = new TimetableCellGUI[this.table.size()][this.table.get(0).size()];
		} else {
			System.out.println("TimetableGUI:constructing empty table");
			cells = new TimetableCellGUI[0][0];
		}
		type = TimetableCellGUI.Type.CHECKBOX;
		System.out.println("a");
		setupSpacing();
		System.out.println("a");
		update();
		System.out.println("a");
	}

	public TimetableGUI(Timetable table, TimetableCellGUI.Type type) {
		this.table = table.table;
		this.type = type;
		this.timetable = table;
		if(type == TimetableCellGUI.Type.RADIOBUTTON) {
			this.tg = new ToggleGroup();
		}
		System.out.println("TimetableGUI:table.table.size (days) ="+table.table.size());
		if(table.table.size() > 0) {
			System.out.println("TimetableGUI:table.table.get(0).size (hours in first day) ="+table.table.get(0).size());
			cells = new TimetableCellGUI[this.table.size()][this.table.get(0).size()];
		} else {
			System.out.println("TimetableGUI:constructing empty table");
			cells = new TimetableCellGUI[0][0];
		}
		System.out.println("a");
		setupSpacing();
		System.out.println("a");
		update();
		System.out.println("a");
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
		for(int i = 0; i < cells.length; i++) {
			for(int j = 0; j < cells[i].length; j++) {
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
		System.out.println("Updating GUI table from DATA");
		if(this.table.size() == 0)
			return;//something went wrong

		// headings
		for(int i = 0; i < this.table.size(); i++) {
			TimetableCellGUI cell = new TimetableCellGUI(TimetableCellGUI.Type.HEADING, new String[] {"DAY: "+i, "", ""}, false, 120, 40);
			cell.border.setFill(Color.WHITE);
			cell.border.setStroke(Color.GRAY);
			this.add(cell, i+1, 0);
		}
		for(int j = 0; j < this.table.get(0).size(); j++) {
			TimetableCellGUI cell = new TimetableCellGUI(TimetableCellGUI.Type.HEADING, new String[] {""+((j+8) > 12 ? (j+8)-12 : j+8)+":00", "", ""}, false, 120, 40);
			cell.border.setFill(Color.WHITE);
			cell.border.setStroke(Color.GRAY);
			this.add(cell, 0, j+1);
		}

		// table data
		for(int i = 0; i < this.table.size(); i++) {
			for(int j = 0; j < this.table.get(i).size(); j++) {
				// System.out.println("DAY IDX: "+i);
				// System.out.println("HOUR IDX: "+j);
				if(allEmployees) {
					// System.out.println("constructing for all employee\ncreating cell:");
					cells[i][j] = new TimetableCellGUI(TimetableCellGUI.Type.NONE, new String[] {"", "", ""}, false, 120, 40);
					// System.out.println("cell constructed");
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
						default:
							System.out.println("ERROR: CellStatus "+this.table.get(i).get(j)+" not found");
						break;
					}
					cells[i][j].border.setStroke(Color.GRAY);
					this.add(cells[i][j], i+1, j+1);
					continue;
				}
				// System.out.println("not constructing for all employees");
				switch(this.table.get(i).get(j)) {
					case FREE:
						// System.out.println("CELL TYPE: FREE");
						if(this.type == TimetableCellGUI.Type.RADIOBUTTON) {
							cells[i][j] = new TimetableCellGUI(type, new String[] {"FREE", "BOOKED_BY_YOU", "BOOKED"}, false, 120, 40, tg);
						} else {
							cells[i][j] = new TimetableCellGUI(type, new String[] {"FREE", "BOOKED_BY_YOU", "BOOKED"}, false, 120, 40);
						}
						// cells[i][j].border.setFill(Color.WHITE);
					break;
					case BOOKED:
						// System.out.println("CELL TYPE: BOOKED");
						cells[i][j] = new TimetableCellGUI(TimetableCellGUI.Type.NONE, new String[] {"FREE", "BOOKED_BY_YOU", "BOOKED"}, false, 120, 40);
						cells[i][j].border.setFill(Color.RED);//special case, color is not decided by selectable
					break;
					case BOOKED_BY_YOU:
						// System.out.println("CELL TYPE: BOOKED_BY_YOU");
						if(this.type == TimetableCellGUI.Type.RADIOBUTTON) {
							cells[i][j] = new TimetableCellGUI(type, new String[] {"FREE", "BOOKED_BY_YOU", "BOOKED"}, true, 120, 40, tg);
						} else {
							cells[i][j] = new TimetableCellGUI(type, new String[] {"FREE", "BOOKED_BY_YOU", "BOOKED"}, true, 120, 40);
						}
						// cells[i][j].border.setFill(Color.LIGHTGRAY);
					break;
					case UNAVAILABLE:
						// System.out.println("CELL TYPE: UNAVAILABLE");
						cells[i][j] = new TimetableCellGUI(TimetableCellGUI.Type.NONE, new String[] {"", "", ""}, false, 120, 40);
						cells[i][j].border.setFill(Color.RED);//special case, color is not decided by selectable
					break;
					default:
						System.out.println("ERROR: CellStatus "+this.table.get(i).get(j)+" not found");
					break;
				}
				cells[i][j].border.setStroke(Color.GRAY);

				this.add(cells[i][j], i+1, j+1);//add each cell to the gridpane
			}
		}
		System.out.println("TIMETABLEGUI: FINISHED Updating GUI table from DATA");
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