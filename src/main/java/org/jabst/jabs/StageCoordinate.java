package org.jabst.jabs;

import javafx.stage.Stage;

public class StageCoordinate {
	public int x;
	public int y;
	public int width;
	public int height;

	public StageCoordinate(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public StageCoordinate(Stage s) {
		this((int)s.getX(), (int)s.getY(), (int)s.getWidth(), (int)s.getHeight());
	}

	public StageCoordinate() {
		this(500,500,300,300);
	}

	public void setStage(Stage s) {
		s.setX(x);
		s.setY(y);
		s.setWidth(width);
		s.setHeight(height);
	}
}