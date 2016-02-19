package net.sourceforge.marathon.recorder;

public interface IScriptListener {
    void setScript(String script);

    void abortRecording();

    void insertChecklistAction(String name);

    void addImportStatement(String ims);
}
