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
import javafx.scene.control.ComboBox;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXComboBox extends RFXComponent {

    public static final Logger LOGGER = Logger.getLogger(RFXComboBox.class.getName());

    private Object prevSelectedItem;

    public RFXComboBox(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusGained(RFXComponent prev) {
        prevSelectedItem = ((ComboBox<?>) node).getSelectionModel().getSelectedItem();
    }

    @Override public void focusLost(RFXComponent next) {
        ComboBox<?> comboBox = (ComboBox<?>) node;
        Object selectedItem = comboBox.getSelectionModel().getSelectedItem();
        if (comboBox.isEditable() && comboBox.getEditor().getText()
                .equals(getComboBoxText(comboBox, comboBox.getItems().indexOf(prevSelectedItem), false))) {
            return;
        } else if (!comboBox.isEditable() && selectedItem != null && selectedItem.equals(prevSelectedItem)) {
            return;
        }
        if (!comboBox.isEditable()) {
            recorder.recordSelect(this, getComboBoxText(comboBox, comboBox.getSelectionModel().getSelectedIndex(), true));
        } else {
            String editorText = comboBox.getEditor().getText();
            String selectedItemText = getComboBoxText(comboBox, comboBox.getSelectionModel().getSelectedIndex(), false);
            if (editorText.equals(selectedItemText)) {
                recorder.recordSelect(this, getComboBoxText(comboBox, comboBox.getSelectionModel().getSelectedIndex(), true));
            } else {
                recorder.recordSelect(this, editorText);
            }
        }
    }

    @Override public String[][] getContent() {
        return getContent((ComboBox<?>) node);
    }

    @Override public String _getText() {
        return getComboBoxText((ComboBox<?>) node, ((ComboBox<?>) node).getSelectionModel().getSelectedIndex(), true);
    }

}
