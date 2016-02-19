package net.sourceforge.marathon.runtime;

import java.io.IOException;

import net.sourceforge.marathon.runtime.api.IRecorder;
import net.sourceforge.marathon.runtime.api.WindowId;

public interface IRecordingServer {

    public abstract void start();

    public abstract void startRecording(IRecorder recorder);

    public abstract void stopRecording();

    public abstract void setRawRecording(boolean selected) throws IOException;

    public abstract void pauseRecording();

    public abstract void resumeRecording();

    public abstract WindowId getFocusedWindowId();

}
