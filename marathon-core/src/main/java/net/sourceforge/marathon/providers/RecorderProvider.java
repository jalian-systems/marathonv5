package net.sourceforge.marathon.providers;

import net.sourceforge.marathon.recorder.IScriptListener;
import net.sourceforge.marathon.recorder.ScriptRecorder;
import net.sourceforge.marathon.runtime.api.IRecorder;

import com.google.inject.Provider;

public class RecorderProvider implements Provider<IRecorder> {

    private IScriptListener scriptListener;

    public void setScriptListener(IScriptListener scriptListener) {
        this.scriptListener = scriptListener;
    }

    public IRecorder get() {
        return new ScriptRecorder(scriptListener);
    }

}
