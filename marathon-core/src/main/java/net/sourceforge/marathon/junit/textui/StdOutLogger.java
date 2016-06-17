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
package net.sourceforge.marathon.junit.textui;

import net.sourceforge.marathon.runtime.api.ILogger;

public class StdOutLogger implements ILogger {
    private int level;

    public void info(String module, String message) {
        info(module, message, null);
    }

    public void info(String module, String message, String description) {
        if (level <= ILogger.INFO)
            p("INFO", module, message, description);
    }

    public void warning(String module, String message) {
        warning(module, message, null);
    }

    public void warning(String module, String message, String description) {
        if (level <= ILogger.WARN)
            p("WARNING", module, message, description);
    }

    public void error(String module, String message) {
        error(module, message, null);
    }

    public void error(String module, String message, String description) {
        if (level <= ILogger.ERROR)
            p("ERROR", module, message, description);
    }

    private void p(String type, String module, String message, String description) {
        System.err.println(type + "<" + module + ">: " + message);
        if (description != null)
            System.err.println(description);
    }

    public void setLogLevel(int level) {
        this.level = level;
    }

    public int getLogLevel() {
        return level;
    }

    public void msg(String module, String message) {
        msg(module, message, null);
    }

    public void msg(String module, String message, String description) {
        p("MESSAGE", module, message, description);
    }

}
