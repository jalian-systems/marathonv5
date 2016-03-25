package net.sourceforge.marathon.javafxrecorder.component;

import java.io.File;
import java.util.List;

import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;

public class RFXFileChooser extends ChooserHelper {
    private IJSONRecorder recorder;

    public RFXFileChooser(IJSONRecorder recorder) {
        this.recorder = recorder;
    }

    public void record(List<File> selectedFiles) {
        recorder.recordFileChooser(encode(selectedFiles.toArray(new File[selectedFiles.size()])));
    }
}
