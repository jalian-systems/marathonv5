package net.sourceforge.marathon.runtime;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import net.sourceforge.marathon.runtime.api.Constants.MarathonMode;
import net.sourceforge.marathon.runtime.api.IConsole;
import net.sourceforge.marathon.runtime.api.IMarathonRuntime;
import net.sourceforge.marathon.runtime.api.IRecorder;
import net.sourceforge.marathon.runtime.api.IScript;
import net.sourceforge.marathon.runtime.api.IScriptModel;
import net.sourceforge.marathon.runtime.api.Module;
import net.sourceforge.marathon.runtime.api.ScriptModel;
import net.sourceforge.marathon.runtime.api.WindowId;

public class BareRuntime implements IMarathonRuntime {
    private IScriptModel scriptModel;
    private IScript script;
    private static boolean mergeOutput = Boolean.getBoolean("marathon.merge.output");

    private static class ScriptOutput extends ConsoleWriter {
        public ScriptOutput(final IConsole console) {
            super(new Writer() {
                @Override public void write(char cbuf[], int off, int len) throws IOException {
                    if (mergeOutput) {
                        System.out.print(new String(cbuf, off, len));
                    }
                    console.writeScriptOut(cbuf, off, len);
                }

                @Override public void flush() throws IOException {
                }

                @Override public void close() throws IOException {
                }
            });
        }
    }

    private static class ScriptError extends ConsoleWriter {
        public ScriptError(final IConsole console) {
            super(new Writer() {
                @Override public void write(char cbuf[], int off, int len) throws IOException {
                    if (mergeOutput) {
                        System.err.print(new String(cbuf, off, len));
                    }
                    console.writeScriptErr(cbuf, off, len);
                }

                @Override public void flush() throws IOException {
                }

                @Override public void close() throws IOException {
                }
            });
        }
    }

    @Override public IScript createScript(MarathonMode mode, IConsole console, String scriptText, String filePath,
            boolean isRecording, boolean isDebugging, Properties dataVariables) {
        scriptModel = ScriptModel.getModel();
        script = scriptModel.createScript(new ScriptOutput(console), new ScriptError(console), scriptText, filePath, isRecording,
                isDebugging, dataVariables, "bare");
        script.setDriverURL("");
        return script;
    }

    @Override public void startRecording(IRecorder recorder) {
    }

    @Override public void stopRecording() {
    }

    @Override public void startApplication() {
    }

    @Override public void stopApplication() {
    }

    @Override public void destroy() {
    }

    @Override public Module getModuleFunctions() {
        return null;
    }

    @Override public void setRawRecording(boolean selected) {
    }

    @Override public String evaluate(String code) {
        return null;
    }

    @Override public WindowId getTopWindowId() {
        return null;
    }

    @Override public File getScreenCapture() {
        return null;
    }

    @Override public void insertScript(String function) {
    }

    @Override public void releaseInterpreters() {
        if (script != null)
            script.releaseInterpreters();
    }
}