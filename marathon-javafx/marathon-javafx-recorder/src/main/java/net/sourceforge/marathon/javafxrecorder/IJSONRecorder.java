package net.sourceforge.marathon.javafxrecorder;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponent;

public interface IJSONRecorder {

    public abstract void recordSelect(RFXComponent r, String state);

    public abstract void recordClick(RFXComponent r, MouseEvent e);

    public abstract void recordClick2(RFXComponent r, MouseEvent e, boolean withCellInfo);

    public abstract void recordRawMouseEvent(RFXComponent r, MouseEvent e);

    public abstract void recordRawKeyEvent(RFXComponent r, KeyEvent e);

    public abstract void recordSelect2(RFXComponent r, String state, boolean withCellInfo);

    public abstract boolean isCreatingObjectMap();

    public abstract void recordAction(RFXComponent r, String action, String property, Object value);

    public abstract void recordWindowClosing(RFXComponent r);

    public abstract void recordWindowState(RFXComponent r, Rectangle2D bounds);

    public abstract JSONOMapConfig getObjectMapConfiguration() throws IOException;

    public abstract JSONObject getContextMenuTriggers() throws JSONException, IOException;

    public abstract boolean isRawRecording() throws IOException;

    public abstract void recordMenuItem(RFXComponent rComponent);

    public abstract void recordFocusedWindow(RFXComponent r) throws IOException;

    public abstract void recordFileChooser(String state);

    public abstract void recordFolderChooser(String state);

    public abstract void recordWindowClosing(String title);

    public abstract void recordWindowState(String title, int x, int y, int width, int height);

    public abstract void recordSelectMenu(RFXComponent r, String menuType, String menuPath);
}
