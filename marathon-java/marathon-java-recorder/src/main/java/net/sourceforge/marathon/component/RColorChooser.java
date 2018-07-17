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

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.util.logging.Logger;

import javax.swing.JColorChooser;

import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RColorChooser extends RComponent {

    public static final Logger LOGGER = Logger.getLogger(RColorChooser.class.getName());

    private String color;

    public RColorChooser(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override
    public void focusLost(RComponent next) {
        String currentColor = getColorCode(((JColorChooser) component).getColor());
        if (!currentColor.equals(color)) {
            recorder.recordSelect(this, currentColor);
        }
    }

    @Override
    public void focusGained(RComponent prev) {
        JColorChooser colorChooser = (JColorChooser) component;
        color = getColorCode(colorChooser.getColor());
    }

    private String getColorCode(Color color) {
        return "#" + Integer.toHexString(color.getRGB() & 0x00FFFFFF | 0x1000000).substring(1);
    }
}
