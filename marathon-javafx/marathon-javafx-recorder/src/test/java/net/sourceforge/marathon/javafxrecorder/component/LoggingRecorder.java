package net.sourceforge.marathon.javafxrecorder.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
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
        // TODO Auto-generated method stub

    }

    @Override public void recordRawKeyEvent(RFXComponent r, KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override public void recordSelect2(RFXComponent r, String state, boolean withCellInfo) {
        // TODO Auto-generated method stub

    }

    @Override public boolean isCreatingObjectMap() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override public void recordAction(RFXComponent r, String action, String property, Object value) {
        // TODO Auto-generated method stub

    }

    @Override public void recordSelectMenu(RFXComponent r, String selection) {
        // TODO Auto-generated method stub

    }

    @Override public void recordWindowClosing(RFXComponent r) {
        // TODO Auto-generated method stub

    }

    @Override public void recordWindowState(RFXComponent r, Rectangle2D bounds) {
        // TODO Auto-generated method stub

    }

    @Override public JSONOMapConfig getObjectMapConfiguration() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public JSONObject getContextMenuTriggers() throws JSONException, IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public boolean isRawRecording() throws IOException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override public void recordMenuItem(RFXComponent rComponent) {
        // TODO Auto-generated method stub

    }

    @Override public void recordFocusedWindow(RFXComponent r) throws IOException {
        // TODO Auto-generated method stub

    }

    public List<Recording> getRecordings() {
        return recordings;
    }

    public List<Recording> waitAndGetRecordings(int count) {
        if (recordings.size() >= count)
            return recordings;
        new Wait("Waiting for " + count + " recordings") {
            @Override public boolean until() {
                return recordings.size() >= count;
            }
        };
        return recordings;
    }
}