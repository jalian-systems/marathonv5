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

    public void run() {
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
