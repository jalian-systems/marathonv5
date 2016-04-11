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

package treetableviewsample;

import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TreeTableViewSample extends Application {

    List<Employee> employees = Arrays.<Employee>asList(
        new Employee("Ethan Williams", "ethan.williams@example.com"),
        new Employee("Emma Jones", "emma.jones@example.com"),
        new Employee("Michael Brown", "michael.brown@example.com"),
        new Employee("Anna Black", "anna.black@example.com"),
        new Employee("Rodger York", "roger.york@example.com"),
        new Employee("Susan Collins", "susan.collins@example.com"));
    private final ImageView depIcon = new ImageView (
            new Image(getClass().getResourceAsStream("department.png"))
    );
    
    final TreeItem<Employee> root = 
        new TreeItem<>(new Employee("Sales Department", ""), depIcon);

    public static void main(String[] args) {
        Application.launch(TreeTableViewSample.class, args);
    }

    @Override
    public void start(Stage stage) {
        root.setExpanded(true);
        employees.stream().forEach((employee) -> {
            root.getChildren().add(new TreeItem<>(employee));
        });
        stage.setTitle("Tree Table View Sample");
        final Scene scene = new Scene(new Group(), 400, 400);
        scene.setFill(Color.LIGHTGRAY);
        Group sceneRoot = (Group) scene.getRoot();

        TreeTableColumn<Employee, String> empColumn = 
            new TreeTableColumn<>("Employee");
        empColumn.setPrefWidth(150);
        empColumn.setCellValueFactory(
            (TreeTableColumn.CellDataFeatures<Employee, String> param) -> 
            new ReadOnlyStringWrapper(param.getValue().getValue().getName())
        );
        TreeTableColumn<Employee, String> emailColumn = 
            new TreeTableColumn<>("Email");
        emailColumn.setPrefWidth(190);
        emailColumn.setCellValueFactory(
            (TreeTableColumn.CellDataFeatures<Employee, String> param) -> 
            new ReadOnlyStringWrapper(param.getValue().getValue().getEmail())
        );
        emailColumn.setSortType(TreeTableColumn.SortType.ASCENDING);

        TreeTableView<Employee> treeTableView = new TreeTableView<>(root);
        treeTableView.getColumns().setAll(empColumn, emailColumn);
        treeTableView.setSortMode(TreeSortMode.ALL_DESCENDANTS);
        treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        treeTableView.getSelectionModel().setCellSelectionEnabled(true);
        sceneRoot.getChildren().add(treeTableView);

        stage.setScene(scene);
        stage.show();
    }

    public class Employee {

        private SimpleStringProperty name;
        private SimpleStringProperty email;
        public SimpleStringProperty nameProperty() {
            if (name == null) {
                name = new SimpleStringProperty(this, "name");
            }
            return name;
        }
        public SimpleStringProperty emailProperty() {
            if (email == null) {
                email = new SimpleStringProperty(this, "email");
            }
            return email;
        }
        private Employee(String name, String email) {
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
        }
        public String getName() {
            return name.get();
        }
        public void setName(String fName) {
            name.set(fName);
        }
        public String getEmail() {
            return email.get();
        }
        public void setEmail(String fName) {
            email.set(fName);
        }
    }
}
