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
package net.sourceforge.marathon.javarecorder;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

import org.json.JSONObject;

import net.sourceforge.marathon.component.RComponent;

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

    public abstract void recordWindowState(RComponent r, Rectangle bounds);

    public abstract JSONOMapConfig getObjectMapConfiguration();

    public abstract JSONObject getContextMenuTriggers();

    public abstract boolean isRawRecording();

    public abstract void recordMenuItem(RComponent rComponent);

    public abstract void recordFocusedWindow(RComponent r) throws IOException;

}
