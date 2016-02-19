package net.sourceforge.marathon.providers;

import net.sourceforge.marathon.runtime.api.PlaybackResult;

import com.google.inject.Provider;

public class PlaybackResultProvider implements Provider<PlaybackResult> {

    public PlaybackResult get() {
        return new PlaybackResult();
    }

}
