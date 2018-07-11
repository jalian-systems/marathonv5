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
import java.util.logging.Logger;

import javax.swing.JSlider;

import net.sourceforge.marathon.javaagent.components.JSliderJavaElement;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RSlider extends RComponent {

    public static final Logger LOGGER = Logger.getLogger(RSlider.class.getName());

    private int value = -1;

    public RSlider(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override
    public void focusLost(RComponent next) {
        int current = ((JSlider) component).getValue();
        if (current != value) {
            recorder.recordSelect(this, "" + current);
        }
    }

    @Override
    public void focusGained(RComponent prev) {
        value = ((JSlider) component).getValue();
    }

    @Override
    public String getText() {
        return JSliderJavaElement.getCurrentValue((JSlider) component);
    }
}
