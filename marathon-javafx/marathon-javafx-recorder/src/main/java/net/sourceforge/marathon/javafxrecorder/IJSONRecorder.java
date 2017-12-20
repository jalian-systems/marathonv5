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
package net.sourceforge.marathon.javafxrecorder;

import java.io.IOException;

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

    public abstract JSONOMapConfig getObjectMapConfiguration();

    public abstract JSONObject getContextMenuTriggers();

    public abstract boolean isRawRecording();

    public abstract void recordMenuItem(RFXComponent rComponent);

    public abstract void recordFocusedWindow(RFXComponent r) throws IOException;

    public abstract void recordFileChooser(String state);

    public abstract void recordFolderChooser(String state);

    public abstract void recordWindowClosing(String title);

    public abstract void recordWindowState(String title, int x, int y, int width, int height);

    public abstract void recordSelectMenu(RFXComponent r, String menuType, String menuPath);

    public abstract boolean isPaused();

	public abstract void recordSelect3(RFXComponent r, String state, String info);

	public abstract void recordClick3(RFXComponent r, String info);
}
