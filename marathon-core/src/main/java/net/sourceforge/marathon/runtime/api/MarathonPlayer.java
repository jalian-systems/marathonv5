/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.runtime.api;

import net.sourceforge.marathon.api.TestAttributes;
import net.sourceforge.marathon.junit.IHasFullname;

/**
 * This handle the play back threading issue and immediated playback stop
 * (interrupt) handling
 */
public final class MarathonPlayer implements IPlayer, Runnable, IPlaybackListener {
    private IPlaybackListener listener;
    private Thread playbackThread;
    private boolean paused = true;
    private IScript script;
    private PlaybackResult result;
    private boolean shouldRunFixture;
    private boolean acceptChecklist;

    public MarathonPlayer(IScript script, IPlaybackListener listener, PlaybackResult result) {
        this.listener = listener;
        this.script = script;
        this.result = result;
        script.attachPlaybackListener(this);
        playbackThread = new Thread(this, "Marathon Playback Thread");
        synchronized (this) {
            playbackThread.start();
            InterruptionError.wait(this);
        }
    }

    public void halt() {
        try {
            playbackThread.interrupt();
        } catch (Throwable e) {
            throw new MarathonRuntimeException("Interrupting playbackthread caused an exception", e);
        }
    }

    public synchronized void play(boolean shouldRunFixture) {
        paused = false;
        this.shouldRunFixture = shouldRunFixture;
        notify();
    }

    public void run() {
        TestAttributes.put("marathon.capture.prefix", ((IHasFullname) listener).getFullName()); // YUK!!!
        synchronized (this) {
            notify();
            InterruptionError.wait(this);
        }
        try {
            script.playbackBody(shouldRunFixture, playbackThread).run();
        } catch (Throwable t) {
            result.addFailure(t.getMessage(), new SourceLine[0], t);
        } finally {
            playbackFinished(result, false);
        }
    }

    public void playbackFinished(PlaybackResult result, boolean shutdown) {
        listener.playbackFinished(result, shutdown);
    }

    public synchronized int lineReached(SourceLine line) {
        while (paused) {
            InterruptionError.wait(this);
        }
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptionError();
        }
        return listener.lineReached(line);
    }

    public int methodReturned(SourceLine line) {
        while (paused) {
            InterruptionError.wait(this);
        }
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptionError();
        }
        return listener.methodReturned(line);
    }

    public int methodCalled(SourceLine line) {
        while (paused) {
            InterruptionError.wait(this);
        }
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptionError();
        }
        return listener.methodCalled(line);
    }

    public int acceptChecklist(String fileName) {
        if (acceptChecklist) {
            listener.acceptChecklist(fileName);
            return PAUSE;
        }
        return CONTINUE;
    }

    public int showChecklist(String fileName) {
        return listener.showChecklist(fileName);
    }

    public void setAcceptCheckList(boolean b) {
        acceptChecklist = b;
    }
}
