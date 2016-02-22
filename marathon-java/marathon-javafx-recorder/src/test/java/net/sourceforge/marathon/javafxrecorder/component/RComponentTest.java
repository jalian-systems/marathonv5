package net.sourceforge.marathon.javafxrecorder.component;

import javax.swing.SwingUtilities;

public class RComponentTest {

    public RComponentTest() {
        super();
    }

    public void siw(Runnable doRun) {
        try {
            SwingUtilities.invokeAndWait(doRun);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}