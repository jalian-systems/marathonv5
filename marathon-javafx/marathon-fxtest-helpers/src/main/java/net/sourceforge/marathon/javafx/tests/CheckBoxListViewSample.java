package net.sourceforge.marathon.javafx.tests;

import ensemble.Sample;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;

public class CheckBoxListViewSample extends Sample {

    public CheckBoxListViewSample() {
        ListView<Item> listView = new ListView<>();
        for (int i = 1; i <= 20; i++) {
            Item item = new Item("Item " + i, false);

            // observe item's on property and display message if it changes:
            item.onProperty().addListener((obs, wasOn, isNowOn) -> {
                System.out.println(item.getName() + " changed on state from " + wasOn + " to " + isNowOn);
            });

            listView.getItems().add(item);
        }

        listView.setCellFactory(CheckBoxListCell.forListView(new Callback<Item, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Item item) {
                return item.onProperty();
            }
        }));

        getChildren().add(listView);
    }

    public static class Item {
        private final StringProperty name = new SimpleStringProperty();
        private final BooleanProperty on = new SimpleBooleanProperty();

        public Item(String name, boolean on) {
            setName(name);
            setOn(on);
        }

        public final StringProperty nameProperty() {
            return this.name;
        }

        public final String getName() {
            return this.nameProperty().get();
        }

        public final void setName(final String name) {
            this.nameProperty().set(name);
        }

        public final BooleanProperty onProperty() {
            return this.on;
        }

        public final boolean isOn() {
            return this.onProperty().get();
        }

        public final void setOn(final boolean on) {
            this.onProperty().set(on);
        }

        @Override
        public String toString() {
            return getName();
        }

    }

}
