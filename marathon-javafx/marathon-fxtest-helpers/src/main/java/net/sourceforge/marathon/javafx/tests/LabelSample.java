/*
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.marathon.javafx.tests;

import javafx.scene.Group;
import javafx.scene.Node;

import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import treeviewsample.TreeViewSample.Employee;

public class LabelSample extends Application {

	Label label3 = new Label("A label that needs to be wrapped");

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		Scene scene = new Scene(new Group());
		stage.setTitle("Label Sample");
		stage.setWidth(420);
		stage.setHeight(180);

		HBox hbox = new HBox();
		Image image = new Image(getClass().getResourceAsStream("labels.jpg"));

		Label label1 = new Label("Search");
		label1.setGraphic(new ImageView(image));
		label1.setFont(new Font("Arial", 30));
		label1.setTextFill(Color.web("#0076a3"));
		label1.setTextAlignment(TextAlignment.JUSTIFY);
		label1.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Popup popup = new Popup();
				TreeViewSample sample = new TreeViewSample();
				VBox vBox = sample.getVBox();
				vBox.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
					@Override
					public void handle(KeyEvent event) {
						if(event.getCode() == KeyCode.ESCAPE) {
							popup.hide();
						}
					}
				});
				popup.getScene().setRoot(vBox);
				popup.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
			}
		});

		Label label2 = new Label("Values");
		label2.setFont(Font.font("Cambria", 32));
		label2.setRotate(270);
		label2.setTranslateY(50);

		label3.setWrapText(true);
		label3.setTranslateY(50);
		label3.setPrefWidth(100);

		label3.setOnMouseEntered((MouseEvent e) -> {
			label3.setScaleX(1.5);
			label3.setScaleY(1.5);
		});

		label3.setOnMouseExited((MouseEvent e) -> {
			label3.setScaleX(1);
			label3.setScaleY(1);
		});

		hbox.setSpacing(10);
		hbox.getChildren().add((label1));
		hbox.getChildren().add(label2);
		hbox.getChildren().add(label3);
		((Group) scene.getRoot()).getChildren().add(hbox);

		stage.setScene(scene);
		stage.show();
	}

	public class TreeViewSample extends Application {

		private final Node rootIcon = new ImageView(new Image(getClass().getResourceAsStream("root.png")));
		private final Image depIcon = new Image(getClass().getResourceAsStream("department.png"));
		List<Employee> employees = Arrays.<Employee> asList(new Employee("Jacob Smith", "Accounts Department"),
				new Employee("Isabella Johnson", "Accounts Department"),
				new Employee("Ethan Williams", "Sales Department"), new Employee("Emma Jones", "Sales Department"),
				new Employee("Michael Brown", "Sales Department"), new Employee("Anna Black", "Sales Department"),
				new Employee("Rodger York", "Sales Department"), new Employee("Susan Collins", "Sales Department"),
				new Employee("Mike Graham", "IT Support"), new Employee("Judy Mayer", "IT Support"),
				new Employee("Gregory Smith", "IT Support"));
		TreeItem<String> rootNode = new TreeItem<>("MyCompany Human Resources", rootIcon);

		@Override
		public void start(Stage stage) {
			VBox box = getVBox();

			stage.setTitle("Tree View Sample");
			final Scene scene = new Scene(box, 400, 300);
			scene.setFill(Color.LIGHTGRAY);
			stage.setScene(scene);
			stage.show();
		}

		private VBox getVBox() {
			rootNode.setExpanded(true);
			for (Employee employee : employees) {
				TreeItem<String> empLeaf = new TreeItem<>(employee.getName());
				boolean found = false;
				for (TreeItem<String> depNode : rootNode.getChildren()) {
					if (depNode.getValue().contentEquals(employee.getDepartment())) {
						depNode.getChildren().add(empLeaf);
						found = true;
						break;
					}
				}
				if (!found) {
					TreeItem depNode = new TreeItem(employee.getDepartment(), new ImageView(depIcon));
					rootNode.getChildren().add(depNode);
					depNode.getChildren().add(empLeaf);
				}
			}

			VBox box = new VBox();

			TreeView<String> treeView = new TreeView<>(rootNode);
			treeView.setShowRoot(true);
			treeView.setEditable(true);
			treeView.setCellFactory((TreeView<String> p) -> new TextFieldTreeCellImpl());

			box.getChildren().add(treeView);
			return box;
		}

		private final class TextFieldTreeCellImpl extends TreeCell<String> {

			private TextField textField;
			private final ContextMenu addMenu = new ContextMenu();

			public TextFieldTreeCellImpl() {
				MenuItem addMenuItem = new MenuItem("Add Employee");
				addMenu.getItems().add(addMenuItem);
				addMenuItem.setOnAction((ActionEvent t) -> {
					TreeItem newEmployee = new TreeItem<>("New Employee");
					getTreeItem().getChildren().add(newEmployee);
				});
			}

			@Override
			public void startEdit() {
				super.startEdit();

				if (textField == null) {
					createTextField();
				}
				setText(null);
				setGraphic(textField);
				textField.selectAll();
			}

			@Override
			public void cancelEdit() {
				super.cancelEdit();

				setText((String) getItem());
				setGraphic(getTreeItem().getGraphic());
			}

			@Override
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);

				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					if (isEditing()) {
						if (textField != null) {
							textField.setText(getString());
						}
						setText(null);
						setGraphic(textField);
					} else {
						setText(getString());
						setGraphic(getTreeItem().getGraphic());
						if (!getTreeItem().isLeaf() && getTreeItem().getParent() != null) {
							setContextMenu(addMenu);
						}
					}
				}
			}

			private void createTextField() {
				textField = new TextField(getString());
				textField.setOnKeyReleased((KeyEvent t) -> {
					if (t.getCode() == KeyCode.ENTER) {
						commitEdit(textField.getText());
					} else if (t.getCode() == KeyCode.ESCAPE) {
						cancelEdit();
					}
				});

			}

			private String getString() {
				return getItem() == null ? "" : getItem().toString();
			}
		}

	}

	public static class Employee {

		private final SimpleStringProperty name;
		private final SimpleStringProperty department;

		private Employee(String name, String department) {
			this.name = new SimpleStringProperty(name);
			this.department = new SimpleStringProperty(department);
		}

		public String getName() {
			return name.get();
		}

		public void setName(String fName) {
			name.set(fName);
		}

		public String getDepartment() {
			return department.get();
		}

		public void setDepartment(String fName) {
			department.set(fName);
		}
	}
}
