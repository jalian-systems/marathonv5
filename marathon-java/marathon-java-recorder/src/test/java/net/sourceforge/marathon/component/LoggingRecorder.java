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

import java.awt.AWTEvent;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public final class LoggingRecorder implements IJSONRecorder {

    public static class Call {
        private String function;
        private String state;
        private boolean withCellInfo;
        private AWTEvent event;
        private String cellInfo;

        public Call(String function, String state, boolean withCellInfo, AWTEvent event, String cellInfo) {
            this.function = function;
            this.state = state;
            this.withCellInfo = withCellInfo;
            this.event = event;
            this.cellInfo = cellInfo;
        }

        public String getFunction() {
            return function;
        }

        public String getState() {
            return state;
        }

        public boolean isWithCellInfo() {
            return withCellInfo;
        }

        public AWTEvent getEvent() {
            return event;
        }

        public String getCellinfo() {
            return cellInfo;
        }

    }

    private List<LoggingRecorder.Call> calls = new ArrayList<LoggingRecorder.Call>();

    @Override public void recordSelect2(RComponent r, String state, boolean withCellInfo) {
        calls.add(new Call("select", state, withCellInfo, null, r.getCellInfo()));
    }

    @Override public void recordSelect(RComponent r, String state) {
        calls.add(new Call("select", state, false, null, r.getCellInfo()));
    }

    @Override public void recordRawKeyEvent(RComponent r, KeyEvent e) {
        calls.add(new Call("raw", "", false, e, r.getCellInfo()));
    }

    @Override public void recordRawMouseEvent(RComponent r, MouseEvent e) {
        calls.add(new Call("raw", "", false, e, r.getCellInfo()));
    }

    @Override public void recordClick2(RComponent r, MouseEvent e, boolean withCellInfo) {
        calls.add(new Call("click", "", withCellInfo, e, r.getCellInfo()));
    }

    @Override public void recordClick(RComponent r, MouseEvent e) {
        calls.add(new Call("click", "", false, e, r.getCellInfo()));
    }

    @Override public void recordAction(RComponent r, String action, String property, Object value) {
    }

    @Override public boolean isCreatingObjectMap() {
        return false;
    }

    public List<LoggingRecorder.Call> getCalls() {
        return calls;
    }

    public LoggingRecorder.Call getCall() {
        return calls.get(calls.size() - 1);
    }

    public void clear() {
        calls.clear();
    }

    @Override public void recordSelectMenu(RComponent r, String selection) {
        calls.add(new Call("select_menu", selection, false, null, r.getCellInfo()));
    }

    @Override public void recordWindowClosing(RComponent r) {
    }

    @Override public void recordWindowState(RComponent r, Rectangle bounds) {
    }

    @Override public JSONOMapConfig getObjectMapConfiguration() {
        return null;
    }

    @Override public JSONObject getContextMenuTriggers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public boolean isRawRecording() {
        return false;
    }

    @Override public void recordMenuItem(RComponent rComponent) {
    }

    @Override public void recordFocusedWindow(RComponent r) {
    }

    @Override public void recordFileDialog(String string) {
        // TODO Auto-generated method stub

    }

    @Override public boolean isPaused() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override public boolean isInsertingScript() {
        // TODO Auto-generated method stub
        return false;
    }

}
