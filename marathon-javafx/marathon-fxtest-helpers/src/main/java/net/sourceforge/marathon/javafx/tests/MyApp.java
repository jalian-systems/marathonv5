package net.sourceforge.marathon.javafx.tests;

import java.util.Set;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MyApp extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		TreeViewSample root = new TreeViewSample();
		primaryStage.setScene(new Scene(root));
//		primaryStage.getScene().getRoot().addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>() {
//			@Override
//			public void handle(Event event) {
//				Node source = (Node) event.getSource();
//				MouseEvent me = (MouseEvent) event ;
//				EventTarget target = event.getTarget();
//				Node hit = getTarget((Node) event.getSource(), me.getX(), me.getY());
//				if(hit != target) {
//					System.err.println("Did not match");
//					System.err.println("event-target = " + target);
//					System.err.println("hit-target = " + hit);
//				}
//			}
//
//			private Node getTarget(Node source, double x, double y) {
//				List<Node> hits = new ArrayList();
//				if(!(source instanceof Parent))
//					return source;
//				ObservableList<Node> children = ((Parent) source).getChildrenUnmodifiable();
//				for (Node child : children) {
//					Bounds boundsInParent = child.getBoundsInParent();
//					x -= boundsInParent.getMinX();
//					y -= boundsInParent.getMinY();
//					if (x < 0.0 || y < 0.0)
//						continue;
//					checkHit(child, x, y, hits);
//				}
//				return hits.size() > 0 ? hits.get(hits.size() - 1) : source;
//			}
//
//			private void checkHit(Node child, double x, double y, List<Node> hits) {
//				Bounds boundsInParent = child.getBoundsInParent();
//				if(boundsInParent.contains(x, y)) {
//					hits.add(child);
//					if(!(child instanceof Parent))
//						return ;
//					x -= boundsInParent.getMinX();
//					y -= boundsInParent.getMinY();
//					ObservableList<Node> childrenUnmodifiable = ((Parent)child).getChildrenUnmodifiable();
//					for (Node node : childrenUnmodifiable) {
//						checkHit(node, x, y, hits);
//					}
//				}
//			}
//		});
		primaryStage.show();
		Set<Node> lookupAll = root.lookupAll(".text");
		System.out.println("MyApp.start(): " + lookupAll.size());
		for (Node node : lookupAll) {
			node.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					MouseEvent me = (MouseEvent) event ;
					System.out.println("Bounds: " + ((Node)me.getSource()).getBoundsInParent());
					System.out.println("Source Coords: " + me.getX() + ", " + me.getY());
					System.out.println("Scene Coords: " + me.getSceneX() + ", " + me.getSceneY());
					System.out.println("Screen Coords: " + me.getScreenX() + ", " + me.getScreenY());
				}
				
			});
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}