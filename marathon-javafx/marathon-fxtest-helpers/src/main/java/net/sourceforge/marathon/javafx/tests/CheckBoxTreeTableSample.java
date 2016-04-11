package net.sourceforge.marathon.javafx.tests;

import java.util.Arrays;
import java.util.List;

import ensemble.Sample;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;

public class CheckBoxTreeTableSample extends Sample {

    List<Employee> employees = Arrays.<Employee> asList(new Employee(false, "Ethan Williams", "ethan.williams@example.com"),
            new Employee(true, "Emma Jones", "emma.jones@example.com"),
            new Employee(true, "Michael Brown", "michael.brown@example.com"),
            new Employee(false, "Anna Black", "anna.black@example.com"),
            new Employee(false, "Rodger York", "roger.york@example.com"),
            new Employee(true, "Susan Collins", "susan.collins@example.com"));

    final TreeItem<Employee> root = new TreeItem<>(new Employee(false, "Sales Department", ""));

    public CheckBoxTreeTableSample() {
        root.setExpanded(true);
        employees.stream().forEach((employee) -> {
            root.getChildren().add(new TreeItem<>(employee));
        });

        TreeTableColumn invitedCol = new TreeTableColumn<Employee, Boolean>();
        invitedCol.setText("Invited");
        invitedCol.setMinWidth(50);
        invitedCol.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(invitedCol));

        TreeTableColumn<Employee, String> empColumn = new TreeTableColumn<>("Employee");
        empColumn.setPrefWidth(150);
        empColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Employee, String> param) -> new ReadOnlyStringWrapper(
                param.getValue().getValue().getName()));

        TreeTableColumn<Employee, String> emailColumn = new TreeTableColumn<>("Email");
        emailColumn.setPrefWidth(190);
        emailColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Employee, String> param) -> new ReadOnlyStringWrapper(
                param.getValue().getValue().getEmail()));

        TreeTableView<Employee> treeTableView = new TreeTableView<>(root);
        treeTableView.getColumns().setAll(empColumn, emailColumn, invitedCol);
        treeTableView.setEditable(true);
        getChildren().add(treeTableView);
    }

    public class Employee {

        private BooleanProperty invited;
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

        private Employee(boolean invited, String name, String email) {
            this.invited = new SimpleBooleanProperty(invited);
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
        }

        public boolean doesInvited() {
            return invited.get();
        }

        public void setInvited(boolean invite) {
            invited.set(invite);
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
