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
package net.sourceforge.marathon.runtime;

import java.io.IOException;
import java.io.Writer;

public abstract class ConsoleWriter extends Writer {
    private char[] cb;
    private int nChars = 1024, nextChar = 0;
    private Writer writer;

    public ConsoleWriter(Writer adapter) {
        super();
        cb = new char[nChars];
        writer = adapter;
    }

    @Override public void close() throws IOException {
    }

    @Override public void flush() throws IOException {
        synchronized (lock) {
            writer.write(cb, 0, nextChar);
            nextChar = 0;
        }
    }

    @Override public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            addCharToBuffer(cbuf[i]);
        }
    }

    private void addCharToBuffer(char c) throws IOException {
        synchronized (lock) {
            cb[nextChar] = c;
            nextChar++;
            if (nextChar == nChars || c == '\n') {
                flush();
            }
        }
    }
}
