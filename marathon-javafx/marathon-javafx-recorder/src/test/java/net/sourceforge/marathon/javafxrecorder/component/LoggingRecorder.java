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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public final class LoggingRecorder implements IJSONRecorder {

    public static class Recording {
        private String call;
        private Object[] parameters;

        public Recording(String call, Object... parameters) {
            this.call = call;
            this.parameters = parameters;
        }

        public String getCall() {
            return call;
        }

        public Object[] getParameters() {
            return parameters;
        }
    }

    private List<Recording> recordings = new ArrayList<>();

    @Override public void recordSelect(RFXComponent r, String state) {
        recordings.add(new Recording("recordSelect", state));
    }

    @Override public void recordClick(RFXComponent r, MouseEvent e) {
        recordings.add(new Recording("click", ""));
    }

    @Override public void recordClick2(RFXComponent r, MouseEvent e, boolean withCellInfo) {
        recordings.add(new Recording("click", r.getCellInfo()));
    }

    @Override public void recordRawMouseEvent(RFXComponent r, MouseEvent e) {
    }

    @Override public void recordRawKeyEvent(RFXComponent r, KeyEvent e) {
    }

    @Override public void recordSelect2(RFXComponent r, String state, boolean withCellInfo) {
        recordings.add(new Recording("recordSelect", state, r.getCellInfo()));
    }

    @Override public boolean isCreatingObjectMap() {
        return false;
    }

    @Override public void recordAction(RFXComponent r, String action, String property, Object value) {
    }

    @Override public void recordSelectMenu(RFXComponent r, String menuType, String selection) {
        // TODO Auto-generated method stub

    }

    @Override public void recordWindowClosing(RFXComponent r) {
    }

    @Override public void recordWindowState(RFXComponent r, Rectangle2D bounds) {
    }

    @Override public JSONOMapConfig getObjectMapConfiguration() {
        return null;
    }

    @Override public JSONObject getContextMenuTriggers() {
        return null;
    }

    @Override public boolean isRawRecording() {
        return false;
    }

    @Override public void recordMenuItem(RFXComponent rComponent) {
    }

    @Override public void recordFocusedWindow(RFXComponent r) throws IOException {
    }

    public List<Recording> getRecordings() {
        return recordings;
    }

    public List<Recording> waitAndGetRecordings(int count) {
        if (recordings.size() >= count) {
            return recordings;
        }
        new Wait("Waiting for " + count + " recordings") {
            @Override public boolean until() {
                return recordings.size() >= count;
            }
        };
        return recordings;
    }

    @Override public void recordFileChooser(String state) {
    }

    @Override public void recordFolderChooser(String state) {
    }

    @Override public void recordWindowClosing(String title) {
    }

    @Override public void recordWindowState(String title, int x, int y, int width, int height) {
    }

    @Override public boolean isPaused() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override public void recordSelect3(RFXComponent r, String state, String info) {
        // TODO Auto-generated method stub

    }

    @Override public void recordClick3(RFXComponent r, String info) {
        // TODO Auto-generated method stub

    }
}
