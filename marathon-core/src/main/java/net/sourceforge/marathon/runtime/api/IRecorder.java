package net.sourceforge.marathon.runtime.api;

public interface IRecorder {
    void record(IScriptElement element);

    void abortRecording();

    void insertChecklist(String name);

    String recordInsertScriptElement(WindowId windowId, String script);

    void recordInsertChecklistElement(WindowId windowId, String fileName);

    void recordShowChecklistElement(WindowId windowId, String fileName);

    boolean isCreatingObjectMap();

    void updateScript();
}
