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
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXListView extends RFXComponent {

    public static final Logger LOGGER = Logger.getLogger(RFXListView.class.getName());

    private int index = -1;
    private Point2D point;
    private String cellValue;
    private String listText;
    private String cellText;

    public RFXListView(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        this.point = point;
        index = getIndexAt((ListView<?>) node, point);
    }

    @Override public void focusGained(RFXComponent prev) {
        ListView<?> listView = (ListView<?>) node;
        cellValue = getListCellValue(listView, index);
        cellText = getListSelectionText(listView, index);
        listText = getListSelectionText(listView);
    }

    private String getListCellValue(ListView<?> listView, int index) {
        if (index == -1) {
            return null;
        }
        ListCell<?> listCell = getCellAt(listView, index);
        RFXComponent cellComponent = getFinder().findRawRComponent(listCell, null, recorder);
        String ctext = cellComponent.getValue();
        return ctext;
    }

    @Override public void focusLost(RFXComponent next) {
        ListView<?> listView = (ListView<?>) node;
        String currCellText = getListCellValue(listView, index);
        if (currCellText != null && !currCellText.equals(cellValue)) {
            recorder.recordSelect2(this, currCellText, true);
        }
        if (next == null || getComponent() != next.getComponent()) {
            String currListText = getListSelectionText(listView);
            if (!currListText.equals(listText)) {
                recorder.recordSelect(this, currListText);
            }
        }
    }

    @Override public String getCellInfo() {
        ListView<?> listView = (ListView<?>) node;
        index = getIndexAt(listView, point);
        cellValue = getListCellValue(listView, index);
        cellText = getListSelectionText(listView, index);
        return cellText;
    }

    @Override public String[][] getContent() {
        return getContent((ListView<?>) node);
    }

    /*
     * NOTE: Same code exits in JavaFXListViewElement class. So in case if you
     * want to modify. Modify both.
     */
    private String[][] getContent(ListView<?> listView) {
        int nItems = listView.getItems().size();
        String[][] content = new String[1][nItems];
        for (int i = 0; i < nItems; i++) {
            content[0][i] = getListCellValue(listView, i);
        }
        return content;
    }

    @Override public String _getText() {
        if (index != -1) {
            return getListSelectionText((ListView<?>) node, index);
        }
        return getListSelectionText((ListView<?>) node);
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + index;
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
        RFXListView other = (RFXListView) obj;
        if (index != other.index) {
            return false;
        }
        return true;
    }

    @Override public String toString() {
        return "RFXListView [index=" + index + ", cellText=" + cellText + ", listText=" + listText + "]";
    }

}
