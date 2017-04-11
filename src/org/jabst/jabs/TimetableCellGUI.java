package org.jabst.jabs;

import javafx.scene.layout.StackPane;// basic layout
import javafx.scene.control.*;//buttons, labels  etc.
import javafx.geometry.Insets;//insets = padding
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.HBox;

public class TimetableCellGUI extends StackPane {
	
	public static enum Type {
		CHECKBOX, RADIOBUTTON, NONE
	}

	public Type type;

	public Rectangle border;
	// private RadioButton radioButton;
	// private CheckBox checkBox;
	public ButtonBase selectable;
	private HBox hbox;

	public TimetableCellGUI() {
		this(Type.CHECKBOX, "default name", 120, 40);
	}

	public TimetableCellGUI(Type type, String name, int width, int height) {
		this.type = type;
		border = new Rectangle(width, height);
		hbox = new HBox();

		switch(type) {
			case CHECKBOX:
				selectable = new CheckBox(name);
				this.getChildren().addAll(border, hbox, selectable);
			break;
			case RADIOBUTTON:
				selectable = new RadioButton(name);
				this.getChildren().addAll(border, hbox, selectable);
			break;
			case NONE:
				this.getChildren().addAll(hbox, border);
			break;
		}
		 //border.widthProperty().bind(hbox.widthProperty());
		 //border.heightProperty().bind(hbox.heightProperty());
	}

}