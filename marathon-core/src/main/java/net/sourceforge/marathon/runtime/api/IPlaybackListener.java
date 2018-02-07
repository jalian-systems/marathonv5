/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.runtime.api;

import java.util.List;

import junit.framework.AssertionFailedError;

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

    void addErrorScreenShotEntry(AssertionFailedError error, String fileName);

    void addScreenShotEntry(String title, String filePath, List<UsedAssertion> assertions);
}
