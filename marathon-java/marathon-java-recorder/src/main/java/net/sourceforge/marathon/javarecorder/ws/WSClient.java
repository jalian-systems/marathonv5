package net.sourceforge.marathon.javarecorder.ws;

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
        if (data != null)
            o.put("data", data);
        send(o.toString());
    }

}
