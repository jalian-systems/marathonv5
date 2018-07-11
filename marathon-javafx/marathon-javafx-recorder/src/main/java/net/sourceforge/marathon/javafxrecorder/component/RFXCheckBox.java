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
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxagent.components.JavaFXCheckBoxElement;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXCheckBox extends RFXComponent {

    public static final Logger LOGGER = Logger.getLogger(RFXCheckBox.class.getName());

    private Integer prevSelection; // 0 - unchecked, 1 - indeterminate, 2 -
                                   // checked

    public RFXCheckBox(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override
    protected void mouseEntered(MouseEvent me) {
        prevSelection = getSelection((CheckBox) node);
    }

    @Override
    protected void mouseClicked(MouseEvent me) {
        int selection = getSelection((CheckBox) node);
        if (prevSelection == null || selection != prevSelection) {
            recorder.recordSelect(this, JavaFXCheckBoxElement.states[selection]);
        }
        prevSelection = selection;
    }

    @Override
    public String _getValue() {
        int selection = getSelection((CheckBox) node);
        return JavaFXCheckBoxElement.states[selection];
    }

    @Override
    public String _getText() {
        return getCheckBoxText((CheckBox) node);
    }

    @Override
    protected void keyPressed(KeyEvent ke) {
        if (ke.getCode() == KeyCode.SPACE) {
            recorder.recordRawKeyEvent(this, ke);
        }
    }

}
