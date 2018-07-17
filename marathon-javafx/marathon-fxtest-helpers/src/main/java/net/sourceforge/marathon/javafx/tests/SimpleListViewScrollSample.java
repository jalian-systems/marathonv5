/*
 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation nor the names of its
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

import java.lang.reflect.Method;

import ensemble.Sample;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * A simple implementation of the ListView control, in which a list of items is
 * displayed vertically. ListView is a powerful multirow control, in which each
 * of a virtually unlimited number of horizontal or vertical rows is defined as
 * a cell. The control also supports dynamically variable nonhomogenous row
 * heights.
 *
 * This is a simple list view sample.
 *
 * @see javafx.scene.control.ListView
 * @see javafx.scene.control.SelectionModel
 * @related controls/list/HorizontalListView
 */
public class SimpleListViewScrollSample extends Sample {
    public SimpleListViewScrollSample() {
        final ListView<String> listView = new ListView<String>();
        listView.setItems(FXCollections.observableArrayList("Row 1", "Row 2", "Long Row 3", "Row 4", "Row 5", "Row 6", "Row 7",
                "Row 8", "Row 9", "Row 10", "Row 11", "Row 12", "Row 13", "Row 14", "Row 15", "Row 16", "Row 17", "Row 18",
                "Row 19", "Row 20", "Row 21", "Row 22", "Row 23", "Row 24", "Row 25"));
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        getChildren().add(listView);
    }

    public static class SimpleListViewScrollSampleApp extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            final ListView<String> listView = new ListView<String>();
            listView.setItems(FXCollections.observableArrayList("Row 1", "Row 2", "Long Row 3", "Row 4", "Row 5", "Row 6", "Row 7",
                    "Row 8", "Row 9", "Row 10", "Row 11", "Row 12", "Row 13", "Row 14", "Row 15", "Row 16", "Row 17", "Row 18",
                    "Row 19", "Row 20", "Row 21", "Row 22", "Row 23", "Row 24", "Row 25"));
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            Button button = new Button("Debug");
            button.setOnAction((e) -> {
                ObservableList<Integer> selectedIndices = listView.getSelectionModel().getSelectedIndices();
                for (Integer index : selectedIndices) {
                    ListCell cellAt = getCellAt(listView, index);
                    System.out.println("SimpleListViewScrollSample.SimpleListViewScrollSampleApp.start(" + cellAt + ")");
                }
            });
            VBox root = new VBox(listView, button);
            primaryStage.setScene(new Scene(root, 300, 400));
            primaryStage.show();
        }

        public ListCell getCellAt(ListView listView, Integer index) {
            try {
                Callback<ListView, ListCell> cellFactory = listView.getCellFactory();
                ListCell listCell = null;
                if (cellFactory == null) {
                    listCell = new ListCell() {
                        @Override
                        public void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);

                            if (empty) {
                                setText(null);
                                setGraphic(null);
                            } else if (item instanceof Node) {
                                setText(null);
                                Node currentNode = getGraphic();
                                Node newNode = (Node) item;
                                if (currentNode == null || !currentNode.equals(newNode)) {
                                    setGraphic(newNode);
                                }
                            } else {
                                /**
                                 * This label is used if the item associated
                                 * with this cell is to be represented as a
                                 * String. While we will lazily instantiate it
                                 * we never clear it, being more afraid of
                                 * object churn than a minor "leak" (which will
                                 * not become a "major" leak).
                                 */
                                setText(item == null ? "null" : item.toString());
                                setGraphic(null);
                            }
                        }
                    };
                } else {
                    listCell = cellFactory.call(listView);
                }
                Object value = listView.getItems().get(index);
                Method updateItem = listCell.getClass().getDeclaredMethod("updateItem", new Class[] { Object.class, Boolean.TYPE });
                updateItem.invoke(listCell, value, false);
                return listCell;
            } catch (Throwable t) {
                return null;
            }
        }

        private static <T> ListCell<T> createDefaultCellImpl() {
            return new ListCell<T>() {
                @Override
                public void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else if (item instanceof Node) {
                        setText(null);
                        Node currentNode = getGraphic();
                        Node newNode = (Node) item;
                        if (currentNode == null || !currentNode.equals(newNode)) {
                            setGraphic(newNode);
                        }
                    } else {
                        /**
                         * This label is used if the item associated with this
                         * cell is to be represented as a String. While we will
                         * lazily instantiate it we never clear it, being more
                         * afraid of object churn than a minor "leak" (which
                         * will not become a "major" leak).
                         */
                        setText(item == null ? "null" : item.toString());
                        setGraphic(null);
                    }
                }
            };
        }

    }

    public static void main(String[] args) {
        Application.launch(SimpleListViewScrollSampleApp.class, args);
    }

}
