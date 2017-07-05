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
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.JToggleButton;

import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RToggleButton extends RComponent {
    
    public static final Logger LOGGER = Logger.getLogger(RToggleButton.class.getName());

    private Boolean prevSelection;

    public RToggleButton(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override protected void mouseEntered(MouseEvent me) {
        prevSelection = ((JToggleButton) component).isSelected();
    }

    @Override protected void mouseClicked(MouseEvent me) {
        boolean selected = ((JToggleButton) component).isSelected();
        if (prevSelection == null || selected != prevSelection.booleanValue()) {
            recorder.recordSelect(this, Boolean.toString(selected));
        }
        prevSelection = selected;
    }
}
