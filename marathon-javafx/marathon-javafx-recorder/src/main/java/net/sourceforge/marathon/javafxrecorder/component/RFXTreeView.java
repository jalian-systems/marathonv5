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
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXTreeView extends RFXComponent {

    public static final Logger LOGGER = Logger.getLogger(RFXTreeView.class.getName());

    private int row = -1;
    private String treeText;
    private String cellValue;
    private String cellInfo;

    public RFXTreeView(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        row = getRowAt((TreeView<?>) node, point);
        cellInfo = getTextForNode((TreeView<?>) node, ((TreeView<?>) node).getTreeItem(row));
    }

    @Override public void focusGained(RFXComponent prev) {
        TreeView<?> treeView = (TreeView<?>) node;
        treeText = getSelectedTreeNodeText(treeView, treeView.getSelectionModel().getSelectedItems());
        cellValue = getTreeCellValue(treeView, row);
        cellInfo = getTextForNode((TreeView<?>) node, ((TreeView<?>) node).getTreeItem(row));
    }

    private String getTreeCellValue(TreeView<?> treeView, int index) {
        if (index == -1) {
            return null;
        }
        TreeCell<?> treeCell = getCellAt(treeView, index);
        RFXComponent cellComponent = getFinder().findRCellComponent(treeCell, null, recorder);
        return cellComponent == null ? null : cellComponent.getValue();
    }

    @Override public void focusLost(RFXComponent next) {
        TreeView<?> treeView = (TreeView<?>) getComponent();
        ObservableList<?> selectedItems = treeView.getSelectionModel().getSelectedItems();
        String currentCellText = getTreeCellValue(treeView, row);
        if (currentCellText != null && !currentCellText.equals(cellValue)) {
            recorder.recordSelect2(this, currentCellText, true);
        }
        if ((next == null || getComponent() != next.getComponent()) && treeView.getSelectionModel().getSelectedItems().size() > 1) {
            String currListText = getSelectedTreeNodeText(treeView, selectedItems);
            if (!currListText.equals(treeText)) {
                recorder.recordSelect(this, currListText);
            }
        }
    }

    @Override public String getCellInfo() {
        return cellInfo;
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
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
        RFXTreeView other = (RFXTreeView) obj;
        if (row != other.row) {
            return false;
        }
        return true;
    }

    @Override public String _getText() {
        if (row != -1)
            return getTextForNode((TreeView<?>) node, ((TreeView<?>) node).getTreeItem(row));
        return getSelectedTreeNodeText((TreeView<?>) node, ((TreeView<?>) node).getSelectionModel().getSelectedItems());
    }

    @Override protected void mousePressed(MouseEvent me) {
    }

    @Override protected void mouseClicked(MouseEvent me) {
        if (me.isControlDown() || me.isAltDown() || me.isMetaDown() || onCheckBox((Node) me.getTarget()))
            return;
        recorder.recordClick2(this, me, true);
    }
}
