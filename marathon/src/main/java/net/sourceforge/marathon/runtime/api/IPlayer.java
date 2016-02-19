package net.sourceforge.marathon.runtime.api;

/**
 * A Player is used to control the execution of a script as it is played back.
 */
public interface IPlayer {
    /**
     * halt the execution of the script immediately. Once this method is
     * invoked, playback cannot be started again
     */
    void halt();

    /**
     * Begin playing the controlled script if it is currently paused (as is the
     * case when it is first created). This call will not wait until the script
     * has finished execution, rather it will always return immediately.
     */
    void play(boolean shouldRunFixture);

    void setAcceptCheckList(boolean b);
}
