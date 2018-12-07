package de.malkusch.lightshow.simulator;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import ch.bildspur.artnet.ArtNet;
import ch.bildspur.artnet.ArtNetException;
import ch.bildspur.artnet.events.ArtNetServerEventAdapter;
import ch.bildspur.artnet.packets.ArtDmxPacket;
import ch.bildspur.artnet.packets.ArtNetPacket;
import ch.bildspur.artnet.packets.PacketType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class Simulator extends Application {

	private final ArtNet artnet = new ArtNet();
	private final List<Light> lights;
	private final BorderPane pane = new BorderPane();

	public Simulator() {
		var leftCenter = new Light(0, "leftCenter");
		var leftFront = new Light(3, "leftFront");
		var frontLeft = new Light(6, "frontLeft");
		var frontCenter = new Light(9, "frontCenter");
		var frontRight = new Light(12, "frontRight");
		var rightFront = new Light(15, "rightFront");
		var rightCenter = new Light(18, "rightCenter");

		lights = Arrays.asList(leftCenter, leftFront, frontLeft, frontCenter, frontRight, rightFront, rightCenter);

		var left = new VBox(50, leftFront.node(), leftCenter.node());
		pane.setLeft(left);

		var top = new HBox(100, frontLeft.node(), frontCenter.node(), frontRight.node());
		top.setAlignment(Pos.CENTER);
		pane.setTop(top);

		var right = new VBox(50, rightFront.node(), rightCenter.node());
		pane.setRight(right);
	}

	@Override
	public void start(Stage stage) throws SocketException, ArtNetException {
		Scene scene = new Scene(pane, 640, 480);
		stage.setScene(scene);
		stage.show();

		artnet.start(InetAddress.getLoopbackAddress());
		artnet.addServerListener(new ArtNetServerEventAdapter() {
			@Override
			public void artNetPacketReceived(ArtNetPacket packet) {
				if (packet.getType() != PacketType.ART_OUTPUT) {
					return;
				}

				var dmx = (ArtDmxPacket) packet;
				byte[] data = dmx.getDmxData();

				Platform.runLater(() -> lights.forEach(l -> l.update(data)));
			}
		});
	}

	@Override
	public void stop() {
		artnet.stop();
	}

	public static void main(String[] args) throws SocketException, ArtNetException, UnknownHostException {
		launch();
	}

}