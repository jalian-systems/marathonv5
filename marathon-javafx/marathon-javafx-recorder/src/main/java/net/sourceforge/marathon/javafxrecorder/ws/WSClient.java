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
package net.sourceforge.marathon.javafxrecorder.ws;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class WSClient extends WebSocketClient {

    private WSRecorder recorder;

    public WSClient(int port, WSRecorder recorder) throws URISyntaxException {
        super(new URI("http://localhost:" + port));
        this.recorder = recorder;
        connect();
    }

    @Override public void onOpen(ServerHandshake handshakedata) {
        recorder.onOpen();
    }

    @Override public void onMessage(String message) {
        recorder.onMessage(message);
    }

    @Override public void onClose(int code, String reason, boolean remote) {
    }

    @Override public void onError(Exception ex) {
    }

    public void post(String method, String data) {
        JSONObject o = new JSONObject();
        o.put("method", method);
        o.put("data", data);
        send(o.toString());
    }

}
