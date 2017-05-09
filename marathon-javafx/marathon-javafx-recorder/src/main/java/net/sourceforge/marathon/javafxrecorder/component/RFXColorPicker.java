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
import javafx.scene.control.ColorPicker;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXColorPicker extends RFXComponent {

    public static final Logger LOGGER = Logger.getLogger(RFXColorPicker.class.getName());

    private String prevColor;

    public RFXColorPicker(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusGained(RFXComponent prev) {
        ColorPicker colorPicker = (ColorPicker) node;
        prevColor = getColorCode(colorPicker.getValue());
    }

    @Override public void focusLost(RFXComponent next) {
        String currentColor = getColorCode(((ColorPicker) node).getValue());
        if (!currentColor.equals(prevColor)) {
            recorder.recordSelect(this, currentColor);
        }
    }

    @Override public String _getText() {
        return getColorCode(((ColorPicker) node).getValue());
    }
}
