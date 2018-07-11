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

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

/**
 * @author vinay
 *
 */
public class RFXTableView extends RFXComponent {

    public static final Logger LOGGER = Logger.getLogger(RFXTableView.class.getName());

    private int column = -1;
    private int row = -1;
    private String cellValue;
    private String cellInfo;
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
        cellInfo = getTableCellText((TableView<?>) node, row, column);
        if (row == -1 || column == -1) {
            row = column = -1;
        }
    }

    @Override
    public void focusGained(RFXComponent prev) {
        TableView<?> tableView = (TableView<?>) node;
        if (row != -1 && column != -1) {
            cellValue = getTableCellValueAt(tableView, row, column);
            cellInfo = getTableCellText(tableView, row, column);
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
        RFXComponent cellComponent = getFinder().findRCellComponent(tableCell, null, recorder);
        return cellComponent == null ? null : cellComponent.getValue();
    }

    @Override
    public void focusLost(RFXComponent next) {
        TableView<?> tableView = (TableView<?>) getComponent();
        String currentCellValue = getTableCellValueAt(tableView, row, column);
        if (currentCellValue != null && !currentCellValue.equals(cellValue)) {
            recorder.recordSelect2(this, currentCellValue, true);
        }
        if ((next == null || next.getComponent() != getComponent())
                && (tableView.getSelectionModel().getSelectedItems().size() > 1)) {
            String currentTableText = getSelection(tableView);
            if (!currentTableText.equals(tableText)) {
                recorder.recordSelect(this, getSelection(tableView));
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + column;
        result = prime * result + row;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
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

    @Override
    public String getCellInfo() {
        return cellInfo;
    }

    @Override
    public String _getText() {
        TableView<?> tableView = (TableView<?>) node;
        if (row != -1 && column != -1) {
            TableCell<?, ?> tableCell = getCellAt(tableView, row, column);
            if (tableCell != null) {
                return tableCell.getText();
            }
        }
        return getSelection((TableView<?>) getComponent());
    }

    @Override
    protected void mousePressed(MouseEvent me) {
    }

    @Override
    protected void mouseClicked(MouseEvent me) {
        if (me.isControlDown() || me.isAltDown() || me.isMetaDown() || onCheckBox((Node) me.getTarget()))
            return;
        recorder.recordClick2(this, me, true);
    }

}
