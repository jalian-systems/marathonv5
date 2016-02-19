package net.sourceforge.marathon.javadriver.recorder;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.marathon.runtime.api.IRecorder;
import net.sourceforge.marathon.runtime.api.IScriptElement;
import net.sourceforge.marathon.runtime.api.WindowId;
import net.sourceforge.marathon.runtime.http.HTTPRecordingServer;

public class RecordingTest {

    protected HTTPRecordingServer recordingServer;
    protected List<IScriptElement> scriptElements = new ArrayList<IScriptElement>();

    protected int startRecordingServer() {
        int port = findPort();
        recordingServer = new HTTPRecordingServer(port);
        recordingServer.start();
        recordingServer.startRecording(new IRecorder() {
            @Override public void record(IScriptElement element) {
                scriptElements.add(element);
            }
    
            @Override public void abortRecording() {
            }
    
            @Override public void insertChecklist(String name) {
            }
    
            @Override public String recordInsertScriptElement(WindowId windowId, String script) {
                return null;
            }
    
            @Override public void recordInsertChecklistElement(WindowId windowId, String fileName) {
            }
    
            @Override public void recordShowChecklistElement(WindowId windowId, String fileName) {
            }
    
            @Override public boolean isCreatingObjectMap() {
                return false;
            }
    
            @Override public void updateScript() {
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
            if (socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                }
        }
    }

}
