package net.sourceforge.marathon.fxcontextmenu;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.PopupWindow;
import net.sourceforge.marathon.fxcontextmenu.AssertionTreeView.PropertyWrapper;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponent;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponentFactory;

public class AssertionPanel extends GridPane {

	private AssertionTreeView attributes;
	private RFXComponentFactory finder;
	private IJSONRecorder recorder;
	private RFXComponent current;

	class Delta {
		double x, y;
	}

	public AssertionPanel(PopupWindow popup, IJSONRecorder r, RFXComponentFactory f) {
		this.recorder = r;
		this.finder = f;
		setAlignment(Pos.CENTER);
		setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: black;"
				+ "-fx-background-color:white;");
		BorderPane toolbar = new BorderPane();
		Button closeButton = new Button("X");
		closeButton.setOnAction((ActionEvent e) -> popup.hide());
		toolbar.setLeft(closeButton);
		Label label = new Label("Assertions");
		label.setAlignment(Pos.CENTER);
		label.setMaxWidth(Double.MAX_VALUE);
		BorderPane.setAlignment(label, Pos.CENTER_LEFT);
		toolbar.setCenter(label);
		final Delta dragDelta = new Delta();
		label.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				// record a delta distance for the drag and drop operation.
				dragDelta.x = mouseEvent.getScreenX();
				dragDelta.y = mouseEvent.getScreenY();
				label.setCursor(Cursor.MOVE);
			}
		});
		label.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				popup.setX(popup.getX() + mouseEvent.getScreenX() - dragDelta.x);
				popup.setY(popup.getY() + mouseEvent.getScreenY() - dragDelta.y);
				dragDelta.x = mouseEvent.getScreenX();
				dragDelta.y = mouseEvent.getScreenY();
				label.setCursor(Cursor.HAND);
			}
		});
		label.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				popup.setX(popup.getX() + mouseEvent.getScreenX() - dragDelta.x);
				popup.setY(popup.getY() + mouseEvent.getScreenY() - dragDelta.y);
				dragDelta.x = mouseEvent.getScreenX();
				dragDelta.y = mouseEvent.getScreenY();
			}
		});
		label.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				label.setCursor(Cursor.HAND);
			}
		});
		add(toolbar, 0, 0);
		attributes = new AssertionTreeView();
		add(attributes, 0, 1);
		TextArea textArea = new TextArea();
		add(textArea, 0, 2);
		attributes.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<TreeItem<PropertyWrapper>>() {
					@Override
					public void changed(ObservableValue<? extends TreeItem<PropertyWrapper>> observable,
							TreeItem<PropertyWrapper> oldValue, TreeItem<PropertyWrapper> newValue) {
						if (newValue != null)
							textArea.setText(newValue.getValue().value.toString());
					}
				});
		ButtonBar bar = new ButtonBar();
		Button assertButton = new Button("Add Assertion");
		assertButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				recordAction(event, "assert");
			}
		});
		Button insertWaitButton = new Button("Insert Wait");
		insertWaitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				recordAction(event, "wait");
			}

		});
		bar.getButtons().addAll(assertButton, insertWaitButton);
		add(bar, 0, 3);
		addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE) {
					popup.hide();
				}
			}
		});
	}

	private void recordAction(ActionEvent event, String action) {
		TreeItem<PropertyWrapper> selectedItem = attributes.getSelectionModel().getSelectedItem();
		if (selectedItem == null)
			return;
		StringBuilder sb = new StringBuilder();
		TreeItem<PropertyWrapper> w = selectedItem;
		while (w.getParent() != null) {
			sb.insert(0, w.getValue().property + ".");
			w = w.getParent();
		}
		sb.setLength(sb.length() - 1);
		recorder.recordAction(current, action, sb.toString(), selectedItem.getValue().value);
	}

	public void setContent(Event event) {
		current = finder.findRComponent((Node) event.getTarget(), null, recorder);
		attributes.setRootObject(current);
	}
}
