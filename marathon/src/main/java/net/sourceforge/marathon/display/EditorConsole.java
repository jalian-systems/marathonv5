package net.sourceforge.marathon.display;

import net.sourceforge.marathon.runtime.api.AbstractFileConsole;

public class EditorConsole extends AbstractFileConsole {
    private IDisplayView display;

    public EditorConsole(IDisplayView display) {
        this.display = display;
    }

    public void writeScriptOut(char cbuf[], int off, int len) {
        display.getOutputPane().append(String.valueOf(cbuf, off, len), IStdOut.SCRIPT_OUT);
        writeToFile(String.valueOf(cbuf, off, len));
    }

    public void writeScriptErr(char cbuf[], int off, int len) {
        display.getOutputPane().append(String.valueOf(cbuf, off, len), IStdOut.SCRIPT_ERR);
        writeToFile(String.valueOf(cbuf, off, len));
    }

    public void writeStdOut(char cbuf[], int off, int len) {
        char[] buf = new char[len];
        for (int i = off; i < off + len; i++) {
            buf[i - off] = cbuf[i];
        }
        display.getOutputPane().append(String.valueOf(cbuf, off, len), IStdOut.STD_OUT);
        writeToFile(String.valueOf(cbuf, off, len));
    }

    public void writeStdErr(char cbuf[], int off, int len) {
        char[] buf = new char[len];
        for (int i = off; i < off + len; i++) {
            buf[i - off] = cbuf[i];
        }
        display.getOutputPane().append(String.valueOf(cbuf, off, len), IStdOut.STD_ERR);
        writeToFile(String.valueOf(cbuf, off, len));
    }

    public void clear() {
        display.getOutputPane().clear();
    }
}
