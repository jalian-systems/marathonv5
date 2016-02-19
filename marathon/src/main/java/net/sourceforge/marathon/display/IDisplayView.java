package net.sourceforge.marathon.display;

import net.sourceforge.marathon.runtime.api.PlaybackResult;
import net.sourceforge.marathon.runtime.api.SourceLine;

public interface IDisplayView {
    void setError(Throwable exception, String message);

    void setState(State state);

    IStdOut getOutputPane();

    void setResult(PlaybackResult result);

    int trackProgress(SourceLine line, int line_reached);

    String getScript();

    String getFilePath();

    void insertScript(String script);

    void trackProgress();

    void startInserting();

    void stopInserting();

    boolean isDebugging();

    int acceptChecklist(String fileName);

    int showChecklist(String fileName);

    void insertChecklistAction(String name);

    void endTestRun();

    void endTest(PlaybackResult result);

    void startTestRun();

    void startTest();

    void addImport(String ims);

    void updateOMapFile();
}
