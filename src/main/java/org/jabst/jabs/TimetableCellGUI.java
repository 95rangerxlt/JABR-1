package org.jabst.jabs;

import javafx.scene.layout.StackPane;// basic layout
import javafx.scene.control.*;//buttons, labels  etc.
import javafx.geometry.Insets;//insets = padding
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.HBox;
import javafx.event.EventHandler;//this activates when a button is pressed
import javafx.event.ActionEvent;//type of event
import javafx.scene.paint.Color;
import javafx.beans.value.*;


public class TimetableCellGUI extends StackPane {
	
	public static enum Type {
		CHECKBOX, RADIOBUTTON, NONE, HEADING
	}

	public Type type;

	public Rectangle border;
	public Selectable selectable;//your checkbox or radiobutton
	public String[] states = new String[3];//deselected, selected, default

	public TimetableCellGUI() {
		this(Type.CHECKBOX, new String[] {"default_desel", "default_sel", "default_default"}, false, 120, 40);
	}

	public TimetableCellGUI(Type type, String[] states, boolean selected, int width, int height) {
		this.states = states;
		this.type = type;
		border = new Rectangle(width, height);

		selectable = new Selectable(type);
		if(type == Type.NONE) {//the selectable shouldnt be added, so it shouldnt show... yet it does
			this.getChildren().addAll(border);
		} else if(type == Type.HEADING) {
			this.getChildren().addAll(border, new Label(states[0]));
		} else {
			this.getChildren().addAll(border, selectable);
		}

		selectable.setSelected(selected);
		selectable.parent = this;
		update();

		// when the checkbox/radiobutton is pressed
		selectable.get().setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				switch(type) {
					case CHECKBOX:
						// TODO
						System.out.println("Checkbox checked!");
					break;
					case RADIOBUTTON:
						// TODO deselect the other one
						System.out.println("Radiobutton Selected!");
					break;
					default:
						System.out.println("ERROR: TimetableCellGUI type not set!");
					break;
				}
				update();
			}
		});

		//when something is deselected
		if(type == Type.RADIOBUTTON) {
			selectable.button.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviousySelected, Boolean isNowSelected) {
					if(wasPreviousySelected) {
						border.setFill(Color.WHITE);
						System.out.println(""+type+" Deselected!");
					}
				}
			});
		}
	}

	public TimetableCellGUI(Type type, String[] states, boolean selected, int width, int height, ToggleGroup tg) {
		this(type, states, selected, width, height);
		selectable.setToggleGroup(tg);
	}

	public void update() {
		if(type == Type.NONE) {
			return;
		}
		selectable.setText(selectable.isSelected() ? states[1] : states[0]);
		if(selectable.isSelected()) {
			border.setFill(Color.LIGHTGRAY);
		} else {
			border.setFill(Color.WHITE);
		}
	}

}

class Selectable extends StackPane {
	public TimetableCellGUI.Type type;
	private CheckBox checkbox;
	RadioButton button;
	TimetableCellGUI parent;

	public Selectable() {
		this(TimetableCellGUI.Type.NONE);
	}

	public Selectable(TimetableCellGUI.Type type) {
		this.type = type;
		switch(type) {
			case CHECKBOX:
				checkbox = new CheckBox();
				this.getChildren().addAll(checkbox);
			break;
			case RADIOBUTTON:
				button = new RadioButton();
				this.getChildren().addAll(button);
			break;
			default:
				// empty selectable
			break;
		}
	}

	public Selectable(TimetableCellGUI.Type type, String name) {
		this(type);
		get().setText(name);
	}

	public void setToggleGroup(ToggleGroup tg) {
		button.setToggleGroup(tg);
	}

	public ButtonBase get() {
		switch(type) {
			case CHECKBOX:
				return checkbox;
			case RADIOBUTTON:
				return button;
			default:
				// System.out.println("ERROR: Selectable get\n\tSelectable doesn't have a type");
				return new CheckBox();
		}
	}

	public void setText(String a) {
		switch(type) {
			case CHECKBOX:
				checkbox.setText(a);
			break;
			case RADIOBUTTON:
				button.setText(a);
			default:
				// System.out.println("ERROR: Selectable setText\n\tSelectable doesn't have a type");
			break;
		}
	}

	public void setSelected(boolean a) {
		switch(type) {
			case CHECKBOX:
				checkbox.setSelected(a);
			break;
			case RADIOBUTTON:
				button.setSelected(a);
			default:
				// System.out.println("ERROR: Selectable setSelected\n\tSelectable doesn't have a type");
			break;
		}
	}

	public boolean isSelected() {
		switch(type) {
			case CHECKBOX:
				return checkbox.isSelected();
			case RADIOBUTTON:
				return button.isSelected();
			default:
				// System.out.println("ERROR: Selectable isSelected\n\tSelectable doesn't have a type");
				return false;
		}
	}
}