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

import java.text.DateFormat;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import net.sourceforge.marathon.api.LogRecord;
import net.sourceforge.marathon.display.IErrorListener;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fxdocking.DockKey;
import net.sourceforge.marathon.fxdocking.DockKey.TabPolicy;
import net.sourceforge.marathon.fxdocking.Dockable;
import net.sourceforge.marathon.runtime.api.ILogger;

public class LogView extends Dockable {

    public static final Logger LOGGER = Logger.getLogger(LogView.class.getName());

    private DateFormat dateTimeInstance = DateFormat.getDateTimeInstance();

    private VBox logViewLayout = new VBox();
    private TableView<LogRecord> logTable = new TableView<>();
    private ToggleGroup toggleGroup = new ToggleGroup();
    private ToolBar toolBar = new ToolBar();

    private ToggleButton infoButton = FXUIUtils.createToggleButton("info", "Show information messages");
    private ToggleButton warnButton = FXUIUtils.createToggleButton("warn", "Show warning messages");
    private ToggleButton errorButton = FXUIUtils.createToggleButton("error", "Show error messages");
    private Button showMessageButton = FXUIUtils.createButton("show_message", "Show Message");
    private Button clearButton = FXUIUtils.createButton("clear", "Clear Messages");

    private static final DockKey DOCK_KEY = new DockKey("Log", "Record & Playback Log", "Log", FXUIUtils.getIcon("warn"),
            TabPolicy.NotClosable, Side.BOTTOM);

    private ObservableList<LogRecord> logList = FXCollections.observableArrayList();
    private IErrorListener errorListener;

    public LogView() {
        this.logList = FXCollections.observableArrayList();
        initComponents();
    }

    private void initComponents() {
        initToolBar();
        initLogTable();
        logViewLayout.getChildren().addAll(toolBar, logTable);
        VBox.setVgrow(logTable, Priority.ALWAYS);
    }

    private void initToolBar() {
        infoButton.setId("infoButton");
        infoButton.setToggleGroup(toggleGroup);
        infoButton.setTooltip(new Tooltip("Show all messages"));
        infoButton.setOnAction((e) -> {
            FilteredList<LogRecord> filtered = logList.filtered(new Predicate<LogRecord>() {
                @Override public boolean test(LogRecord t) {
                    if (t.getType() == ILogger.INFO || t.getType() == ILogger.MESSAGE) {
                        return true;
                    }
                    return false;
                }
            });
            logTable.setItems(filtered);
            logTable.refresh();
        });

        warnButton.setId("warnButton");
        warnButton.setToggleGroup(toggleGroup);
        warnButton.setTooltip(new Tooltip("Show only warnings and errors"));
        warnButton.setOnAction((e) -> {
            FilteredList<LogRecord> filtered = logList.filtered(new Predicate<LogRecord>() {
                @Override public boolean test(LogRecord t) {
                    if (t.getType() == ILogger.WARN || t.getType() == ILogger.MESSAGE || t.getType() == ILogger.ERROR) {
                        return true;
                    }
                    return false;
                }
            });
            logTable.setItems(filtered);
            logTable.refresh();
        });

        errorButton.setId("errorButton");
        errorButton.setToggleGroup(toggleGroup);
        errorButton.setTooltip(new Tooltip("Show only errors"));
        errorButton.setOnAction((e) -> {
            FilteredList<LogRecord> filtered = logList.filtered(new Predicate<LogRecord>() {
                @Override public boolean test(LogRecord t) {
                    if (t.getType() == ILogger.ERROR || t.getType() == ILogger.MESSAGE) {
                        return true;
                    }
                    return false;
                }
            });
            logTable.setItems(filtered);
            logTable.refresh();
        });
        toggleGroup.selectToggle(infoButton);
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                toggleGroup.selectToggle(oldValue);
            }
        });

        showMessageButton.setId("showMessageButton");
        showMessageButton.setDisable(true);
        showMessageButton.setTooltip(new Tooltip("Show message"));
        showMessageButton.setOnAction((e) -> onShowMessage());
        clearButton.setId("clearButton");
        clearButton.setTooltip(new Tooltip("Clear"));
        if (logList.isEmpty()) {
            clearButton.setDisable(true);
        }
        clearButton.setOnAction((e) -> clear());

        Separator separator = new Separator(Orientation.VERTICAL);
        toolBar.setId("toolBar");
        toolBar.getItems().addAll(clearButton, showMessageButton, separator, errorButton, warnButton, infoButton);
        toolBar.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
    }

    private void onShowMessage() {
        LogRecord selectedItem = logTable.getSelectionModel().getSelectedItem();
        showMessage(selectedItem);
    }

    private void showMessage(LogRecord selectedItem) {
        if (selectedItem.getDescription() != null) {
            String title = "Log @" + dateTimeInstance.format(selectedItem.getDate()) + " >" + selectedItem.getModule();
            MessageStage messageStage = new MessageStage(new MessageInfo(selectedItem.getDescription(), title, new TextArea()));
            messageStage.getStage().showAndWait();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) private void initLogTable() {
        logTable.setId("logTable");
        TableColumn<LogRecord, Integer> iconColumn = new TableColumn<>("");
        iconColumn.prefWidthProperty().bind(logTable.widthProperty().multiply(0.05));
        iconColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        iconColumn.setCellFactory(new Callback<TableColumn<LogRecord, Integer>, TableCell<LogRecord, Integer>>() {
            @Override public TableCell call(TableColumn<LogRecord, Integer> param) {
                return new IconTableCell();
            }
        });

        TableColumn<LogRecord, String> messageColumn = new TableColumn<>("Message");
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        messageColumn.prefWidthProperty().bind(logTable.widthProperty().multiply(0.50));

        TableColumn<LogRecord, String> moduleColumn = new TableColumn<>("Module");
        moduleColumn.setCellValueFactory(new PropertyValueFactory<>("module"));
        moduleColumn.prefWidthProperty().bind(logTable.widthProperty().multiply(0.195));

        TableColumn<LogRecord, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.prefWidthProperty().bind(logTable.widthProperty().multiply(0.25));

        logList.addListener(new ListChangeListener<LogRecord>() {
            @Override public void onChanged(javafx.collections.ListChangeListener.Change<? extends LogRecord> c) {
                if (logList.size() == 0) {
                    clearButton.setDisable(true);
                } else {
                    clearButton.setDisable(false);
                }
            }
        });

        logTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getDescription() != null) {
                showMessageButton.setDisable(false);
            } else {
                showMessageButton.setDisable(true);
            }
        });
        logTable.setRowFactory(e -> {
            TableRow<LogRecord> tableRow = new TableRow<>();
            tableRow.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !tableRow.isEmpty()) {
                    LogRecord rowData = tableRow.getItem();
                    if (rowData.getDescription() != null) {
                        showMessage(rowData);
                    }
                }
            });
            return tableRow;
        });

        errorButton.setSelected(true);
        FilteredList<LogRecord> filtered = logList.filtered(new Predicate<LogRecord>() {
            @Override public boolean test(LogRecord t) {
                if (t.getType() == ILogger.ERROR || t.getType() == ILogger.MESSAGE) {
                    return true;
                }
                return false;
            }
        });
        logTable.setItems(filtered);
        logTable.refresh();
        logTable.getColumns().addAll(iconColumn, messageColumn, moduleColumn, dateColumn);
    }

    public void clear() {
        logList.clear();
        logTable.refresh();
    }

    public void addLog(LogRecord result) {
        logList.add(result);
        if (errorListener != null && result.getType() == ILogger.ERROR) {
            errorListener.addError(result);
        }
    }

    public ObservableList<LogRecord> getLogList() {
        return logList;
    }

    public void setLogList(ObservableList<LogRecord> logList) {
        this.logList = logList;
    }

    public void setErrorListener(IErrorListener controller) {
        this.errorListener = controller;
    }

    public class IconTableCell extends TableCell<LogRecord, Integer> {

        @Override protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                Node value = null;
                if (item == ILogger.INFO) {
                    value = FXUIUtils.getIcon("info");
                } else if (item == ILogger.ERROR) {
                    value = FXUIUtils.getIcon("error");
                } else if (item == ILogger.WARN) {
                    value = FXUIUtils.getIcon("warn");
                } else if (item == ILogger.MESSAGE) {
                    value = null;
                }
                setGraphic(value);
                setText(null);
            }
        }

    }

    @Override public DockKey getDockKey() {
        return DOCK_KEY;
    }

    @Override public Node getComponent() {
        return logViewLayout;
    }

}
