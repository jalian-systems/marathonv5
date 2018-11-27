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
package net.sourceforge.marathon.javadriver.recorder;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import net.sourceforge.marathon.fx.display.FXContextMenuTriggers;
import net.sourceforge.marathon.runtime.NamingStrategyFactory;
import net.sourceforge.marathon.runtime.api.IRecorder;
import net.sourceforge.marathon.runtime.api.IScriptElement;
import net.sourceforge.marathon.runtime.api.WindowId;
import net.sourceforge.marathon.runtime.ws.WSRecordingServer;

public class RecordingTest {

    protected WSRecordingServer recordingServer;
    protected List<IScriptElement> scriptElements = new ArrayList<IScriptElement>();

    protected int startRecordingServer() {
        int port = findPort();
        recordingServer = new WSRecordingServer(port, NamingStrategyFactory.get()) {

            public JSONObject getContextMenuTriggers() {
                return new JSONObject().put("contextMenuKeyModifiers", FXContextMenuTriggers.getContextMenuKeyModifiers())
                        .put("contextMenuKey", FXContextMenuTriggers.getContextMenuKeyCode())
                        .put("menuModifiers", FXContextMenuTriggers.getContextMenuModifiers());
            }

        };
        recordingServer.start();
        recordingServer.startRecording(new IRecorder() {
            @Override
            public void record(IScriptElement element) {
                scriptElements.add(element);
            }

            @Override
            public void abortRecording() {
            }

            @Override
            public void insertChecklist(String name) {
            }

            @Override
            public String recordInsertScriptElement(WindowId windowId, String script) {
                return null;
            }

            @Override
            public void recordInsertChecklistElement(WindowId windowId, String fileName) {
            }

            @Override
            public void recordShowChecklistElement(WindowId windowId, String fileName) {
            }

            @Override
            public boolean isCreatingObjectMap() {
                return false;
            }

            @Override
            public void updateScript() {
            }

            @Override
            public void recordInsertScreenShotElement(WindowId windowId, String description) {
            }

        });
        return port;
    }

    private int findPort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } catch (IOException e1) {
            throw new RuntimeException("Could not allocate a port: " + e1.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
