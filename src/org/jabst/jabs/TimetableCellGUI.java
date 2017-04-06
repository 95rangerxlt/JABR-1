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
	private RadioButton radioButton;
	private CheckBox checkBox;
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
				checkBox = new CheckBox(name);
				this.getChildren().addAll(border, hbox, checkBox);
			break;
			case RADIOBUTTON:
				radioButton = new RadioButton(name);
				this.getChildren().addAll(border, hbox, radioButton);
			break;
			case NONE:
				this.getChildren().addAll(hbox, border);
			break;
		}
		 //border.widthProperty().bind(hbox.widthProperty());
		 //border.heightProperty().bind(hbox.heightProperty());
	}

}