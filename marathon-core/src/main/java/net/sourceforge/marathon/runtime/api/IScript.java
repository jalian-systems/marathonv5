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

    public abstract void onWSConnectionClose(int port);

    public abstract boolean isDriverAvailable();

    public abstract void releaseInterpreters();

}
