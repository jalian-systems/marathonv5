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
package net.sourceforge.marathon.javafxrecorder.component;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

/**
 * @author vinay
 *
 */
public class RFXTableView extends RFXComponent {

    private int column = -1;
    private int row = -1;
    private String cellValue;
    private String cellText;
    private String tableText;

    public RFXTableView(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        TableView<?> table = (TableView<?>) source;
        if (source == null) {
            return;
        }
        if (table.getEditingCell() != null) {
            TablePosition<?, ?> editingCell = table.getEditingCell();
            row = editingCell.getRow();
            column = editingCell.getColumn();
        } else {
            if (point != null && point.getX() > 0 && point.getY() > 0) {
                column = getColumnAt(table, point);
                row = getRowAt(table, point);
            } else {
                @SuppressWarnings("rawtypes")
                ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();
                for (TablePosition<?, ?> tablePosition : selectedCells) {
                    column = tablePosition.getColumn();
                    row = tablePosition.getRow();
                }
            }
        }
        if (row == -1 || column == -1) {
            row = column = -1;
        }
    }

    @Override public void focusGained(RFXComponent prev) {
        TableView<?> tableView = (TableView<?>) node;
        if (row != -1 && column != -1) {
            cellValue = getTableCellValueAt(tableView, row, column);
            cellText = getTableCellText(tableView, row, column);
            tableText = getSelection(tableView);
        }
    }

    public String getTableCellValueAt(TableView<?> tableView, int row, int column) {
        if (row == -1 || column == -1) {
            return null;
        }
        TableCell<?, ?> tableCell = getCellAt(tableView, row, column);
        if (tableCell == null) {
            throw new RuntimeException("Got a null tableCell for " + new Point2D(row, column));
        }
        RFXComponent cellComponent = getFinder().findRawRComponent(tableCell, null, recorder);
        if (cellComponent == null) {
            throw new RuntimeException("Got a null RFXComponent for " + tableCell.getClass().getName());
        }
        String ctext = cellComponent.getValue();
        return ctext;
    }

    @Override public void focusLost(RFXComponent next) {
        TableView<?> tableView = (TableView<?>) getComponent();
        String currentCellValue = getTableCellValueAt(tableView, row, column);
        if (currentCellValue != null && !currentCellValue.equals(cellValue)) {
            recorder.recordSelect2(this, currentCellValue, true);
        }
        if (next == null || next.getComponent() != getComponent()) {
            String currentTableText = getSelection(tableView);
            if (!currentTableText.equals(tableText)) {
                recorder.recordSelect(this, getSelection(tableView));
            }
        }
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + column;
        result = prime * result + row;
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RFXTableView other = (RFXTableView) obj;
        if (column != other.column) {
            return false;
        }
        if (row != other.row) {
            return false;
        }
        return true;
    }

    @Override public String getCellInfo() {
        TableView<?> tableView = (TableView<?>) node;
        if (row != -1 && column != -1) {
            cellValue = getTableCellValueAt(tableView, row, column);
            cellText = getTableCellText(tableView, row, column);
        }
        return cellText;
    }

    @Override public String _getText() {
        TableView<?> tableView = (TableView<?>) node;
        if (row != -1 && column != -1) {
            return getTableCellValueAt(tableView, row, column);
        }
        return getSelection((TableView<?>) getComponent());
    }

    @Override public String[][] getContent() {
        return getContent((TableView<?>) node);
    }

    /*
     * NOTE: Same code exits in JavaFXTableViewElement class. So in case if you
     * want to modify. Modify both.
     */
    protected String[][] getContent(TableView<?> tableView) {
        int rows = tableView.getItems().size();
        int cols = tableView.getColumns().size();
        String[][] content = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String valueAt = getTableCellValueAt(tableView, i, j);
                if (valueAt == null) {
                    valueAt = "";
                }
                content[i][j] = valueAt;
            }
        }
        return content;
    }

}
