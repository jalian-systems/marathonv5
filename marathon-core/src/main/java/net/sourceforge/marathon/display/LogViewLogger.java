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
package net.sourceforge.marathon.display;

import java.util.Date;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import net.sourceforge.marathon.api.LogRecord;
import net.sourceforge.marathon.fx.display.LogView;
import net.sourceforge.marathon.runtime.api.ILogger;

public class LogViewLogger implements ILogger {

    public static final Logger LOGGER = Logger.getLogger(LogViewLogger.class.getName());

    private LogView logView;
    private int level;

    /**
     * @param displayWindow
     */
    public LogViewLogger(LogView logView) {
        this.logView = logView;
        setLogLevel();
    }

    private void setLogLevel() {
        Preferences p = Preferences.userNodeForPackage(LogViewLogger.class);
        setLogLevel(p.getInt("loglevel", WARN));
    }

    private void log(LogRecord r) {
        logView.addLog(r);
    }

    @Override
    public void info(String module, String message) {
        log(new LogRecord(ILogger.INFO, message, null, module, new Date()));
    }

    @Override
    public void info(String module, String message, String description) {
        log(new LogRecord(ILogger.INFO, message, description, module, new Date()));
    }

    @Override
    public void warning(String module, String message) {
        log(new LogRecord(ILogger.WARN, message, null, module, new Date()));
    }

    @Override
    public void warning(String module, String message, String description) {
        log(new LogRecord(ILogger.WARN, message, description, module, new Date()));
    }

    @Override
    public void error(String module, String message) {
        log(new LogRecord(ILogger.ERROR, message, null, module, new Date()));
    }

    @Override
    public void error(String module, String message, String description) {
        log(new LogRecord(ILogger.ERROR, message, description, module, new Date()));
    }

    @Override
    public void setLogLevel(int level) {
        Preferences p = Preferences.userNodeForPackage(LogViewLogger.class);
        p.putInt("loglevel", level);
        try {
            p.flush();
        } catch (BackingStoreException e) {
        }
        this.level = level;
    }

    @Override
    public int getLogLevel() {
        return level;
    }

    @Override
    public void msg(String module, String message) {
        log(new LogRecord(ILogger.MESSAGE, message, null, module, new Date()));
    }

    @Override
    public void msg(String module, String message, String description) {
        log(new LogRecord(ILogger.MESSAGE, message, description, module, new Date()));
    }

}
