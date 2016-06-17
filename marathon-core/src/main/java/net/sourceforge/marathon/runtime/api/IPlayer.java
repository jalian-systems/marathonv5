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
