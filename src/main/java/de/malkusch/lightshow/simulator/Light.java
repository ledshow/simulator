package de.malkusch.lightshow.simulator;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public final class Light {

	private final Rectangle box;
	private final VBox node;
	private final int startAddress;

	public Light(int startAddress, String label) {
		this.startAddress = startAddress;
		box = new Rectangle(50, 50);
		box.setFill(Color.BLACK);
		node = new VBox(box, new Label(label));
		node.setAlignment(Pos.TOP_CENTER);
	}

	public Node node() {
		return node;
	}

	public void update(byte[] dmx) {
		var r = toColor(dmx[startAddress + 0]);
		var g = toColor(dmx[startAddress + 1]);
		var b = toColor(dmx[startAddress + 2]);
		var color = Color.color(r, g, b);
		box.setFill(color);
	}

	private static double toColor(byte value) {
		return Byte.toUnsignedInt(value) / (double) 255;
	}

}
