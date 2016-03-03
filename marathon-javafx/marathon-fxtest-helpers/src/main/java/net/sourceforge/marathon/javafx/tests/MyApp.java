package net.sourceforge.marathon.javafx.tests;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MyApp extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		TreeViewSample root = new TreeViewSample();
		primaryStage.setScene(new Scene(root));
		primaryStage.getScene().getRoot().addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				root.printCells();
			}
		});
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}