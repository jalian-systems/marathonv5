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

import java.util.logging.Logger;

public abstract class AbstractDebugger implements IDebugger {

    public static final Logger LOGGER = Logger.getLogger(AbstractDebugger.class.getName());

    private String commandToExecute;
    private String returnValue = null;
    private Object commandLock = new Object();

    @Override public void pause() {
        while (true) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (commandToExecute == null) {
                    break;
                }
                try {
                    returnValue = run(commandToExecute);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                synchronized (commandLock) {
                    commandLock.notifyAll();
                }
                commandToExecute = null;
            }
        }
    }

    @Override public void resume() {
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override public String evaluateScriptWhenPaused(String script) {
        commandToExecute = script;
        returnValue = "";
        synchronized (commandLock) {
            resume();
            try {
                commandLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return returnValue;
    }
}
