package net.sourceforge.marathon.runtime.api;

import java.io.File;
import java.util.Properties;

public interface IScript {
    public abstract IPlayer getPlayer(IPlaybackListener playbackListener, PlaybackResult result);

    public abstract IDebugger getDebugger();

    public abstract void setDataVariables(Properties dataVariables);

    public abstract void attachPlaybackListener(IPlaybackListener marathonPlayer);

    public abstract Runnable playbackBody(boolean shouldRunFixture, Thread playbackThread);

    public abstract void quit();

    public abstract Module getModuleFunctions();

    public abstract File getScreenCapture();

    public abstract String evaluate(String code);

    public abstract void runFixtureSetup();

    public abstract void setDriverURL(String driverURL);

    public abstract void exec(String function);

}
