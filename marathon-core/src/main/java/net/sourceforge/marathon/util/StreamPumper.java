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
package net.sourceforge.marathon.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

public class StreamPumper implements Runnable {
    private InputStreamReader in;
    private Writer writer;
    private Thread pumpingThread;

    public StreamPumper(InputStream in, Writer writer) {
        pumpingThread = new Thread(this, "Stream Pumper ");
        this.in = new InputStreamReader(in);
        this.writer = writer == null ? new BitBucket() : writer;
    }

    @Override public void run() {
        char[] cbuf = new char[1024];
        try {
            while (true) {
                int n = in.read(cbuf);
                if (n != -1) {
                    writeChar(cbuf, n);
                } else {
                    return;
                }
            }
        } catch (IOException e) {
            // No need to print stack trace - the application must have quit
            // e.printStackTrace();
        }
    }

    private void writeChar(char[] cbuf, int n) throws IOException {
        synchronized (this.writer) {
            writer.write(cbuf, 0, n);
        }
    }

    public void start() {
        pumpingThread.start();
    }

    public void setWriter(Writer writer) {
        synchronized (this) {
            this.writer = writer;
        }
    }
}
