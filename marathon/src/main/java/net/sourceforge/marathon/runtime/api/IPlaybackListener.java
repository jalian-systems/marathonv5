package net.sourceforge.marathon.runtime.api;

/**
 * Receive feedback about certain key events which occur while a script is
 * playing
 */
public interface IPlaybackListener {
    public static final int PAUSE = 1;
    public static final int CONTINUE = 2;

    /**
     * Called after the last statement in this script has been executed
     * 
     * @param result
     *            - the result object containing all playback events generated
     *            by this script
     * @param shutdown
     */
    void playbackFinished(PlaybackResult result, boolean shutdown);

    /**
     * Called when a breakpoint has been reached, and execution has stopped just
     * before <code>line</code>
     * 
     * @param line
     */
    int lineReached(SourceLine line);

    /**
     * Called when a function returns in the script code
     * 
     * @param line
     */
    int methodReturned(SourceLine line);

    /**
     * Called when a function is called in the script code
     * 
     * @param line
     */
    int methodCalled(SourceLine line);

    int acceptChecklist(String fileName);

    int showChecklist(String filename);
}
