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
package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JSpinner;

import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RSpinner extends RComponent {

    public static final Logger LOGGER = Logger.getLogger(RSpinner.class.getName());

    private String oldValue;

    public RSpinner(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusGained(RComponent prev) {
        oldValue = getSpinnerText();
    }

    @Override public void focusLost(RComponent next) {
        String newValue = getSpinnerText();
        if (oldValue == null || !oldValue.equals(newValue)) {
            recorder.recordSelect(this, newValue);
        }
    }

    private String getSpinnerText() {
        JComponent editor = ((JSpinner) component).getEditor();

        if (editor == null) {
        } else {
            RComponentFactory finder = new RComponentFactory(omapConfig);
            if (editor instanceof JSpinner.DefaultEditor) {
                RComponent rComponent = finder.findRawRComponent(editor, null, recorder);
                return rComponent.getText();
            }
        }
        return null;
    }

    @Override protected void mousePressed(MouseEvent me) {
    }

    @Override protected void keyPressed(KeyEvent ke) {
    }
}
