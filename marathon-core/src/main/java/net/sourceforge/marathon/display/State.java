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
package net.sourceforge.marathon.display;

public class State {
    public final static State STOPPED_WITH_APP_CLOSED = new State("Stopped with application closed");
    public final static State STOPPED_WITH_APP_OPEN = new State("Stopped with application open");
    public final static State PLAYING = new State("Playing");
    public final static State RECORDING = new State("Recording");
    public static final State RECORDINGPAUSED = new State("Recording Paused");
    public static final State PLAYINGPAUSED = new State("Playing Paused");
    public static final State RECORDING_ABOUT_TO_START = new State("Recording started");
    private String name;

    private State(String name) {
        this.name = name;
    }

    public boolean isStoppedWithAppClosed() {
        return this == STOPPED_WITH_APP_CLOSED;
    }

    public boolean isStoppedWithAppOpen() {
        return this == STOPPED_WITH_APP_OPEN;
    }

    public boolean isStopped() {
        return isStoppedWithAppClosed() || isStoppedWithAppOpen();
    }

    public boolean isPlaying() {
        return this == PLAYING;
    }

    public boolean isRecording() {
        return this == RECORDING;
    }

    public boolean isRecordingPaused() {
        return this == RECORDINGPAUSED;
    }

    public boolean isPlayingPaused() {
        return this == PLAYINGPAUSED;
    }

    public String toString() {
        return name;
    }
}
