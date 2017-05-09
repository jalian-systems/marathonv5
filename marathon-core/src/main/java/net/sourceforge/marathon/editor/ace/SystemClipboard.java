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
package net.sourceforge.marathon.editor.ace;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.util.Duration;
import net.sourceforge.marathon.fx.api.EventListenerList;

public class SystemClipboard implements EventHandler<ActionEvent> {

    public static final Logger LOGGER = Logger.getLogger(SystemClipboard.class.getName());

    private EventListenerList listeners = new EventListenerList();
    private Clipboard clipboard;
    private String prevData;

    public SystemClipboard() {
        clipboard = Clipboard.getSystemClipboard();
        Timeline monitorTask = new Timeline(new KeyFrame(Duration.millis(200), this));
        monitorTask.setCycleCount(Animation.INDEFINITE);
        monitorTask.play();
        prevData = null;
    }

    public void addListener(ClipboardListener l) {
        listeners.add(ClipboardListener.class, l);
    }

    @Override public void handle(ActionEvent event) {
        String data = clipboard.getString();
        if (prevData == null && data == null) {
            return;
        }
        if (prevData != null && data != null && prevData.equals(data)) {
            return;
        }
        ClipboardListener[] ls = listeners.getListeners(ClipboardListener.class);
        for (ClipboardListener clipboardListener : ls) {
            clipboardListener.clipboardChanged(data);
        }
        prevData = data;
    }

    public void setData(String text) {
        Map<DataFormat, Object> content = new HashMap<>();
        content.put(DataFormat.PLAIN_TEXT, text);
        clipboard.setContent(content);
    }

    public String getData() {
        return clipboard.getString();
    }
}
