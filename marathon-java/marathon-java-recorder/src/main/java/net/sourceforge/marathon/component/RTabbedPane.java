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

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;

import net.sourceforge.marathon.javaagent.components.JTabbedPaneJavaElement;
import net.sourceforge.marathon.javaagent.components.JTabbedPaneTabJavaElement;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RTabbedPane extends RComponent {

    public static final Logger LOGGER = Logger.getLogger(RTabbedPane.class.getName());

    public RTabbedPane(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void stateChanged(ChangeEvent e) {
        JTabbedPane tp = (JTabbedPane) component;
        int selectedIndex = tp.getSelectedIndex();
        if (selectedIndex != -1) {
            recorder.recordSelect(this, JTabbedPaneTabJavaElement.getText(tp, selectedIndex));
        }
    }

    @Override public String[][] getContent() {
        return JTabbedPaneJavaElement.getContent((JTabbedPane) component);
    }

    @Override public String getText() {
        return JTabbedPaneJavaElement.getSelectedItemText((JTabbedPane) component);
    }

}
