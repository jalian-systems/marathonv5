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
            if (!consoleLogNeeded)
                return;
            consoleLogWriter.append(text);
            consoleLogWriter.flush();
        } catch (IOException e) {
        }
    }

    public static void renameFile() {
        File mpdDir = Constants.getMarathonProjectDirectory();
        File file = new File(Constants.getMarathonProjectDirectory(), "console.log.5");
        if (file.exists())
            file.delete();
        for (int i = 4; i >= 0; i--) {
            if (i == 0)
                file = new File(mpdDir, "console.log");
            else
                file = new File(mpdDir, createLogFileName(i));
            if (file.exists())
                file.renameTo(new File(mpdDir, createLogFileName(i + 1)));
        }

    }

    private static String createLogFileName(int index) {
        return "console" + ".log." + index;
    }

    public static void setConsoleLogNeeded(boolean c) {
        consoleLogNeeded = c;
    }
}
