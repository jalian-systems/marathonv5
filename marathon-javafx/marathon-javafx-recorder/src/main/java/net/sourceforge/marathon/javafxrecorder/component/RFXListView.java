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

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXListView extends RFXComponent {

    public static final Logger LOGGER = Logger.getLogger(RFXListView.class.getName());

    private int index = -1;
    private String listSelectionText;
    private String cellValue;
    private String cellInfo;

    public RFXListView(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        index = getIndexAt((ListView<?>) node, point);
        cellInfo = getListSelectionText((ListView<?>) node, index);
    }

    @Override
    public void focusGained(RFXComponent prev) {
        listSelectionText = getListSelectionText((ListView<?>) node);
        cellValue = getListCellValue((ListView<?>) node, index);
        cellInfo = getListSelectionText((ListView<?>) node, index);
    }

    private String getListCellValue(ListView<?> listView, int index) {
        if (index == -1) {
            return null;
        }
        ListCell<?> listCell = getCellAt(listView, index);
        RFXComponent cellComponent = getFinder().findRCellComponent(listCell, null, recorder);
        return cellComponent == null ? null : cellComponent.getValue();
    }

    @Override
    public void focusLost(RFXComponent next) {
        ListView<?> listView = (ListView<?>) node;
        String currentCellValue = getListCellValue(listView, index);
        if (currentCellValue != null && !currentCellValue.equals(cellValue)) {
            recorder.recordSelect2(this, currentCellValue, true);
        }
        if ((next == null || getComponent() != next.getComponent())
                && listView.getSelectionModel().getSelectedIndices().size() > 1) {
            String currListText = getListSelectionText(listView);
            if (!currListText.equals(listSelectionText)) {
                recorder.recordSelect(this, currListText);
            }
        }
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

    @Override
    public String getCellInfo() {
        return cellInfo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + index;
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
        RFXListView other = (RFXListView) obj;
        if (index != other.index) {
            return false;
        }
        return true;
    }

    @Override
    public String _getText() {
        if (index != -1) {
            return getListSelectionText((ListView<?>) node, index);
        }
        return getListSelectionText((ListView<?>) node);
    }

}