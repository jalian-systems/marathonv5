package net.sourceforge.marathon.junit;

import net.sourceforge.marathon.runtime.api.AbstractFileConsole;

public class StdOutConsole extends AbstractFileConsole {
    public void write(char cbuf[], int off, int len) {
        byte[] buf = new byte[len];
        for (int i = off; i < off + len; i++) {
            buf[i - off] = (byte) cbuf[i];
        }
        System.out.write(buf, 0, len);
        writeToFile(String.valueOf(cbuf, off, len));
    }

    public void writeStdOut(char cbuf[], int off, int len) {
        write(cbuf, 0, len);
    }

    public void writeStdErr(char cbuf[], int off, int len) {
        write(cbuf, 0, len);
    }

    public void writeScriptOut(char cbuf[], int off, int len) {
        write(cbuf, 0, len);
    }

    public void writeScriptErr(char cbuf[], int off, int len) {
        write(cbuf, 0, len);
    }

    public void clear() {
    }
}
