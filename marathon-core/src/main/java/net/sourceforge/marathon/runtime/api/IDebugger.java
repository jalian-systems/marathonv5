package net.sourceforge.marathon.runtime.api;

public interface IDebugger {

    public abstract String run(String script);

    public abstract void setListener(IPlaybackListener listener);

    public abstract void pause();

    public abstract void resume();

    public abstract String evaluateScriptWhenPaused(String script);
}
