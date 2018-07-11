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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.sourceforge.marathon.runtime.api.AbstractFileConsole;

public class EditorConsole extends AbstractFileConsole {

    public static final Logger LOGGER = Logger.getLogger(EditorConsole.class.getName());

    private IDisplayView display;

    private Map<Integer, StringBuilder> hold = new HashMap<>();

    public EditorConsole(IDisplayView display) {
        this.display = display;
        hold.put(IStdOut.STD_OUT, new StringBuilder());
        hold.put(IStdOut.STD_ERR, new StringBuilder());
        hold.put(IStdOut.SCRIPT_OUT, new StringBuilder());
        hold.put(IStdOut.SCRIPT_ERR, new StringBuilder());
    }

    @Override
    public void writeScriptOut(char cbuf[], int off, int len) {
        appendToDisplay(cbuf, off, len, IStdOut.SCRIPT_OUT);
        writeToFile(String.valueOf(cbuf, off, len));
    }

    @Override
    public void writeScriptErr(char cbuf[], int off, int len) {
        appendToDisplay(cbuf, off, len, IStdOut.SCRIPT_ERR);
        writeToFile(String.valueOf(cbuf, off, len));
    }

    @Override
    public void writeStdOut(char cbuf[], int off, int len) {
        appendToDisplay(cbuf, off, len, IStdOut.STD_OUT);
        writeToFile(String.valueOf(cbuf, off, len));
    }

    @Override
    public void writeStdErr(char cbuf[], int off, int len) {
        appendToDisplay(cbuf, off, len, IStdOut.STD_ERR);
        writeToFile(String.valueOf(cbuf, off, len));
    }

    private void appendToDisplay(char[] cbuf, int off, int len, int type) {
        for (int i = off; i < off + len; i++) {
            StringBuilder sb = hold.get(type);
            sb.append(cbuf[i]);
            if (cbuf[i] == '\n') {
                display.getOutputPane().append(sb.toString(), type);
                sb.setLength(0);
            }
        }
    }

    @Override
    public void clear() {
        display.getOutputPane().clear();
    }
}
