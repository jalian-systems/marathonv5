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
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXTreeTableView extends RFXComponent {

    private int column = -1;
    private int row = -1;
    private String cellValue;
    private String cellText;
    private String treeTableText;

    public RFXTreeTableView(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        TreeTableView<?> treeTableView = (TreeTableView<?>) source;
        if (source == null) {
            return;
        }
        if (treeTableView.getEditingCell() != null) {
            TreeTablePosition<?, ?> editingCell = treeTableView.getEditingCell();
            row = editingCell.getRow();
            column = editingCell.getColumn();
        } else {
            if (point != null) {
                column = getTreeTableColumnAt(treeTableView, point);
                row = getTreeTableRowAt(treeTableView, point);
            } else {
                ObservableList<?> selectedCells = treeTableView.getSelectionModel().getSelectedCells();
                for (Object cell : selectedCells) {
                    TreeTablePosition<?, ?> tablePosition = (TreeTablePosition<?, ?>) cell;
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
        TreeTableView<?> treeTableView = (TreeTableView<?>) node;
        if (row != -1 && column != -1) {
            cellValue = getTreeTableCellValueAt(treeTableView, row, column);
            cellText = getTreeTableCellText(treeTableView, row, column);
            treeTableText = getTreeTableSelection(treeTableView);
        }
    }

    public String getTreeTableCellValueAt(TreeTableView<?> treeTableView, int row, int column) {
        if (row == -1 || column == -1) {
            return null;
        }
        TreeTableCell<?, ?> tableCell = getCellAt(treeTableView, row, column);
        RFXComponent cellComponent = getFinder().findRawRComponent(tableCell, null, recorder);
        String ctext = cellComponent.getValue();
        return ctext;
    }

    @Override public void focusLost(RFXComponent next) {
        TreeTableView<?> treeTableView = (TreeTableView<?>) node;
        String currentCellValue = getTreeTableCellValueAt(treeTableView, row, column);
        if (currentCellValue != null && !currentCellValue.equals(cellValue)) {
            recorder.recordSelect2(this, currentCellValue, true);
        }
        if (next == null || next.getComponent() != getComponent()) {
            String currentTreeTableText = getTreeTableSelection(treeTableView);
            if (!currentTreeTableText.equals(treeTableText)) {
                recorder.recordSelect(this, getTreeTableSelection(treeTableView));
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
        RFXTreeTableView other = (RFXTreeTableView) obj;
        if (column != other.column) {
            return false;
        }
        if (row != other.row) {
            return false;
        }
        return true;
    }

    @Override public String getCellInfo() {
        TreeTableView<?> treeTableView = (TreeTableView<?>) node;
        if (row != -1 && column != -1) {
            cellValue = getTreeTableCellValueAt(treeTableView, row, column);
            cellText = getTreeTableCellText(treeTableView, row, column);
        }
        return cellText;
    }

    @Override public String _getText() {
        if (row != -1 && column != -1) {
            return getTreeTableCellValueAt((TreeTableView<?>) node, row, column);
        }
        return getTreeTableSelection((TreeTableView<?>) node);
    }

    @Override public String[][] getContent() {
        return getContent((TreeTableView<?>) getComponent());
    }

    /*
     * NOTE: Same code exits in JavaFXTreeTableViewElement class. So in case if
     * you want to modify. Modify both.
     */
    private String[][] getContent(TreeTableView<?> treeTableView) {
        int rows = treeTableView.getExpandedItemCount();
        int cols = treeTableView.getColumns().size();
        String[][] content = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String valueAt = getTreeTableCellValueAt(treeTableView, i, j);
                if (valueAt == null) {
                    valueAt = "";
                }
                content[i][j] = valueAt;
            }
        }
        return content;
    }
}
