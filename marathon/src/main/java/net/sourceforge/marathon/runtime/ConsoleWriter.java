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

    public void close() throws IOException {
    }

    public void flush() throws IOException {
        synchronized (lock) {
            writer.write(cb, 0, nextChar);
            nextChar = 0;
        }
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = 0; i < len; i++)
            addCharToBuffer(cbuf[i]);
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
