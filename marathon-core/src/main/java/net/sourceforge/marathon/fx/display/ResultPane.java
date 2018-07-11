/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.fx.display;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fxdocking.DockKey;
import net.sourceforge.marathon.fxdocking.DockKey.TabPolicy;
import net.sourceforge.marathon.fxdocking.Dockable;
import net.sourceforge.marathon.runtime.api.Failure;
import net.sourceforge.marathon.runtime.api.PlaybackResult;
import net.sourceforge.marathon.runtime.api.SourceLine;

public class ResultPane extends Dockable {

    public static final Logger LOGGER = Logger.getLogger(ResultPane.class.getName());

    private BorderPane resultPaneLayout = new BorderPane();

    private ToolBar toolBar = new ToolBar();
    private Button showMessageButton = FXUIUtils.createButton("show_message", "Show Message");
    private Button clearButton = FXUIUtils.createButton("clear", "Clear Messages");
    private TableView<Failure> resultTable = new TableView<Failure>();
    private ObservableList<Failure> failuresList = FXCollections.observableArrayList();
    private Label tableLabel = new Label();
    private List<IResultPaneSelectionListener> listeners;

    public static interface IResultPaneSelectionListener {
        public void resultSelected(SourceLine line);
    }

    public ResultPane() {
        listeners = new ArrayList<IResultPaneSelectionListener>();
        initComponents();
    }

    public void addSelectionListener(IResultPaneSelectionListener l) {
        listeners.add(l);
    }

    private void initComponents() {
        initToolBar();
        initResultTable();
    }

    @SuppressWarnings("unchecked")
    private void initResultTable() {
        resultTable.setId("resultTable");
        setLabel();
        TableColumn<Failure, String> messageColumn = new TableColumn<>("Message");
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        messageColumn.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.50));

        TableColumn<Failure, String> fileNameColumn = new TableColumn<>("File");
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileNameColumn.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.245));

        TableColumn<Failure, String> locationColumn = new TableColumn<>("Location");
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("lineNumber"));
        locationColumn.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.249));

        failuresList.addListener(new ListChangeListener<Failure>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends Failure> c) {
                if (failuresList.size() == 0) {
                    clearButton.setDisable(true);
                } else {
                    clearButton.setDisable(false);
                }
            }
        });

        resultTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getMessage() != null) {
                showMessageButton.setDisable(false);
            } else {
                showMessageButton.setDisable(true);
            }
        });

        resultTable.setRowFactory(e -> {
            TableRow<Failure> tableRow = new TableRow<>();
            tableRow.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !tableRow.isEmpty()) {
                    SourceLine[] traceback = tableRow.getItem().getTraceback();
                    if (traceback.length > 0) {
                        fireResultPaneSelectedEvent(traceback[0]);
                    }
                }
            });
            return tableRow;
        });

        resultTable.setItems(failuresList);
        resultTable.getColumns().addAll(messageColumn, fileNameColumn, locationColumn);
        VBox tableContent = new VBox(tableLabel, resultTable);
        VBox.setVgrow(tableContent, Priority.ALWAYS);
        VBox.setVgrow(resultTable, Priority.ALWAYS);
        resultPaneLayout.setCenter(tableContent);
    }

    private void setLabel() {
        String text;
        if (failuresList == null) {
            text = "";
        } else if (failuresList.size() > 0) {
            String errorString = failuresList.size() == 1 ? " error" : " errors";
            text = failuresList.size() + errorString;
            tableLabel.setTextFill(Color.RED);
        } else {
            text = "No Errors";
            tableLabel.setTextFill(Color.GREEN);
        }

        tableLabel.setText(text);
        tableLabel.setFont(Font.font(tableLabel.getFont().toString(), FontWeight.BOLD, 12));
        tableLabel.setLabelFor(resultTable);
    }

    private void initToolBar() {
        toolBar.setId("toolBar");
        toolBar.getItems().addAll(clearButton, showMessageButton);
        toolBar.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        resultPaneLayout.setTop(toolBar);

        if (failuresList.isEmpty()) {
            clearButton.setDisable(true);
        }
        clearButton.setOnAction(e -> clear());
        showMessageButton.setDisable(true);
        showMessageButton.setOnAction(e -> showMessage());
    }

    private void showMessage() {
        Failure selectedItem = resultTable.getSelectionModel().getSelectedItem();
        showMessage(selectedItem);
    }

    private void showMessage(Failure selectedItem) {
        if (selectedItem.getMessage() != null) {
            MessageStage messageStage = new MessageStage(
                    new MessageInfo(selectedItem.getMessage(), "Failure Message", new TextArea()));
            messageStage.getStage().showAndWait();
        }
    }

    public void addResult(PlaybackResult pbResult) {
        addFailures(pbResult);
        setLabel();
    }

    private void addFailures(PlaybackResult pbResult) {
        if (pbResult.failureCount() > 0) {
            Failure[] failures = pbResult.failures();
            for (Failure failure : failures) {
                failuresList.add(failure);
            }
            resultTable.refresh();
        }
    }

    private void fireResultPaneSelectedEvent(SourceLine line) {
        for (IResultPaneSelectionListener listener : listeners) {
            listener.resultSelected(line);
        }
    }

    public void clear() {
        failuresList.clear();
        resultTable.refresh();
        setLabel();
    }

    private static final DockKey DOCK_KEY = new DockKey("Results", "Results", "Test results", FXUIUtils.getIcon("showreport"),
            TabPolicy.NotClosable, Side.BOTTOM);

    @Override
    public DockKey getDockKey() {
        return DOCK_KEY;
    }

    @Override
    public Node getComponent() {
        return resultPaneLayout;
    }
}
