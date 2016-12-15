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
import java.io.FileWriter;
import java.io.IOException;

public abstract class AbstractFileConsole implements IConsole {

    private static boolean consoleLogNeeded = true;
    private static FileWriter consoleLogWriter;

    public AbstractFileConsole() {
    }

    static {
        try {
            if (consoleLogNeeded) {
                renameFile();
                consoleLogWriter = new FileWriter(new File(Constants.getMarathonProjectDirectory(), "console.log"));
            }
        } catch (Exception e) {
        }
    }

    public void writeToFile(String text) {
        try {
            if (!consoleLogNeeded) {
                return;
            }
            consoleLogWriter.append(text);
            consoleLogWriter.flush();
        } catch (IOException e) {
        }
    }

    public static void renameFile() {
        File mpdDir = Constants.getMarathonProjectDirectory();
        File file = new File(Constants.getMarathonProjectDirectory(), "console.log.5");
        if (file.exists()) {
            file.delete();
        }
        for (int i = 4; i >= 0; i--) {
            if (i == 0) {
                file = new File(mpdDir, "console.log");
            } else {
                file = new File(mpdDir, createLogFileName(i));
            }
            if (file.exists()) {
                file.renameTo(new File(mpdDir, createLogFileName(i + 1)));
            }
        }

    }

    private static String createLogFileName(int index) {
        return "console" + ".log." + index;
    }

    public static void setConsoleLogNeeded(boolean c) {
        consoleLogNeeded = c;
    }
}
