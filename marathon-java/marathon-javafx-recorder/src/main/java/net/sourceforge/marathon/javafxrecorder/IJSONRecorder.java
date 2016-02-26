package net.sourceforge.marathon.javafxrecorder;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxrecorder.component.RComponent;

public interface IJSONRecorder {

    public abstract void recordSelect(RComponent r, String state);

    public abstract void recordClick(RComponent r, MouseEvent e);

    public abstract void recordClick2(RComponent r, MouseEvent e, boolean withCellInfo);

    public abstract void recordRawMouseEvent(RComponent r, MouseEvent e);

    public abstract void recordRawKeyEvent(RComponent r, KeyEvent e);

    public abstract void recordSelect2(RComponent r, String state, boolean withCellInfo);

    public abstract boolean isCreatingObjectMap();

    public abstract void recordAction(RComponent r, String action, String property, Object value);

    public abstract void recordSelectMenu(RComponent r, String selection);

    public abstract void recordWindowClosing(RComponent r);

    public abstract void recordWindowState(RComponent r, Rectangle2D bounds);

    public abstract JSONOMapConfig getObjectMapConfiguration() throws IOException;

    public abstract JSONObject getContextMenuTriggers() throws JSONException, IOException;

    public abstract boolean isRawRecording() throws IOException;

    public abstract void recordMenuItem(RComponent rComponent);

    public abstract void recordFocusedWindow(RComponent r) throws IOException;

}