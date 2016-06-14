package net.sourceforge.marathon.runtime.api;

import java.io.OutputStream;

public interface ITestLauncher {

    void destroy();

    void copyOutputTo(OutputStream writerOutputStream);

    int start();

    void setMessageArea(OutputStream writerOutputStream);

}
