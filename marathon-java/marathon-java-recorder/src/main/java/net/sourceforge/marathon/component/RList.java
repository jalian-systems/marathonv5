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

import javax.swing.JList;

import net.sourceforge.marathon.javaagent.components.JListItemJavaElement;
import net.sourceforge.marathon.javaagent.components.JListJavaElement;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RList extends RComponent {

    public static final Logger LOGGER = Logger.getLogger(RList.class.getName());

    private String cellInfo;
    private int index;

    public RList(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        JList list = (JList) source;
        index = JListJavaElement.getIndexAt(list, point);
        cellInfo = index != -1 ? JListItemJavaElement.getText(list, index) : null;
    }

    @Override public void focusGained(RComponent prev) {
        cellInfo = index != -1 ? JListItemJavaElement.getText((JList) component, index) : null;
    }

    @Override public void focusLost(RComponent next) {
        JList list = (JList) component;
        Object[] selectedValues = list.getSelectedValues();
        if (next == null || getComponent() != next.getComponent()) {
            if (selectedValues == null || selectedValues.length == 0) {
                recorder.recordSelect(this, "[]");
            } else if (selectedValues.length > 1) {
                String currentListSelectionText = JListJavaElement.getSelectionText((JList) component);
                recorder.recordSelect(this, currentListSelectionText);
            }
        }
    }

    @Override public String getText() {
        if (index == -1)
            return JListJavaElement.getSelectionText((JList) component);
        return JListItemJavaElement.getText((JList) component, index);
    }

    @Override public String[][] getContent() {
        return JListJavaElement.getContent((JList) component);
    }

    @Override protected void mousePressed(MouseEvent me) {
        // Ignore Ctrl+Clicks used to select the nodes
        if (me.getButton() == MouseEvent.BUTTON1 && isMenuShortcutKeyDown(me)) {
            return;
        }
        if (me.getButton() != MouseEvent.BUTTON1) {
            focusLost(null);
        }
        super.mousePressed(me);
    }

    @Override public String getCellInfo() {
        return cellInfo;
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + index;
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        RList other = (RList) obj;
        if (index != other.index)
            return false;
        return true;
    }

    @Override protected void mouseClicked(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON1 && isMenuShortcutKeyDown(me)) {
            return;
        }
        recorder.recordClick2(this, me, true);
    }
}
