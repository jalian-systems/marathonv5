package net.sourceforge.marathon.runtime.api;

public class RuntimeLogger {

    private static ILogger runtimeLogger;

    public static void setRuntimeLogger(ILogger logViewLogger) {
        runtimeLogger = logViewLogger;
    }

    public static ILogger getRuntimeLogger() {
        if (runtimeLogger == null)
            return new NullLogger();
        return runtimeLogger;
    }
}
