package net.sourceforge.marathon.javafxrecorder.component;

import java.io.File;

import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;

public class RFXFolderChooser extends ChooserHelper {

    private IJSONRecorder recorder;

    public RFXFolderChooser(IJSONRecorder recorder) {
        this.recorder = recorder;
    }

    public void record(File folder) {
        recorder.recordFolderChooser(encode(folder));
    }
}
