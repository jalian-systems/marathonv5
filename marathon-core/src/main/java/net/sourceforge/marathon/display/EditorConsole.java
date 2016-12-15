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

import net.sourceforge.marathon.runtime.api.AbstractFileConsole;

public class EditorConsole extends AbstractFileConsole {
    private IDisplayView display;

    public EditorConsole(IDisplayView display) {
        this.display = display;
    }

    @Override public void writeScriptOut(char cbuf[], int off, int len) {
        display.getOutputPane().append(String.valueOf(cbuf, off, len), IStdOut.SCRIPT_OUT);
        writeToFile(String.valueOf(cbuf, off, len));
    }

    @Override public void writeScriptErr(char cbuf[], int off, int len) {
        display.getOutputPane().append(String.valueOf(cbuf, off, len), IStdOut.SCRIPT_ERR);
        writeToFile(String.valueOf(cbuf, off, len));
    }

    @Override public void writeStdOut(char cbuf[], int off, int len) {
        char[] buf = new char[len];
        for (int i = off; i < off + len; i++) {
            buf[i - off] = cbuf[i];
        }
        display.getOutputPane().append(String.valueOf(cbuf, off, len), IStdOut.STD_OUT);
        writeToFile(String.valueOf(cbuf, off, len));
    }

    @Override public void writeStdErr(char cbuf[], int off, int len) {
        char[] buf = new char[len];
        for (int i = off; i < off + len; i++) {
            buf[i - off] = cbuf[i];
        }
        display.getOutputPane().append(String.valueOf(cbuf, off, len), IStdOut.STD_ERR);
        writeToFile(String.valueOf(cbuf, off, len));
    }

    @Override public void clear() {
        display.getOutputPane().clear();
    }
}
