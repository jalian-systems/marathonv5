package net.sourceforge.marathon.component;

import java.io.File;
import java.util.logging.Logger;

import net.sourceforge.marathon.javaagent.ChooserHelper;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;

public class RFileDialog {

    public static final Logger LOGGER = Logger.getLogger(RFileDialog.class.getName());

    private IJSONRecorder recorder;

    public RFileDialog(IJSONRecorder recorder) {
        this.recorder = recorder;
    }

    public void record(String file) {
        if ("".equals(file)) {
            recorder.recordFileDialog("");
        } else {
            recorder.recordFileDialog(ChooserHelper.encode(new File(file)));
        }
    }

}
