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

import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.util.Callback;
import net.sourceforge.marathon.javafxagent.components.JavaFXCheckBoxElement;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXCheckBoxTreeTableCell extends RFXComponent {

    public static final Logger LOGGER = Logger.getLogger(RFXCheckBoxTreeTableCell.class.getName());

    public RFXCheckBoxTreeTableCell(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) @Override public String _getValue() {
        CheckBoxTreeTableCell cell = (CheckBoxTreeTableCell) node;
        Callback selectedStateCallback = cell.getSelectedStateCallback();
        String cbText;
        if (selectedStateCallback != null) {
            ObservableValue<Boolean> call = (ObservableValue<Boolean>) selectedStateCallback.call(cell.getItem());
            int selection = call.getValue() ? 2 : 0;
            cbText = JavaFXCheckBoxElement.states[selection];
        } else {
            Node cb = cell.getGraphic();
            RFXComponent comp = getFinder().findRawRComponent(cb, null, null);
            cbText = comp._getValue();

        }
        String cellText = cell.getText();
        if (cellText == null) {
            cellText = "";
        }
        String text = cellText + ":" + cbText;
        return text;
    }
}
