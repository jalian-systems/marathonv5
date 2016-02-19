package net.sourceforge.marathon.runtime;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import net.sourceforge.marathon.runtime.api.Constants.MarathonMode;
import net.sourceforge.marathon.runtime.api.IConsole;
import net.sourceforge.marathon.runtime.api.IMarathonRuntime;
import net.sourceforge.marathon.runtime.api.IRecorder;
import net.sourceforge.marathon.runtime.api.IScript;
import net.sourceforge.marathon.runtime.api.IScriptModel;
import net.sourceforge.marathon.runtime.api.MPFUtils;
import net.sourceforge.marathon.runtime.api.Module;
import net.sourceforge.marathon.runtime.api.ScriptModel;
import net.sourceforge.marathon.runtime.api.WindowId;
import net.sourceforge.marathon.runtime.http.HTTPRecordingServer;

import org.openqa.selenium.WebDriver;

public class WebDriverRuntime implements IMarathonRuntime {

    private static class ScriptOutput extends ConsoleWriter {
        public ScriptOutput(final IConsole console) {
            super(new Writer() {
                public void write(char cbuf[], int off, int len) throws IOException {
                    console.writeScriptOut(cbuf, off, len);
                }

                public void flush() throws IOException {
                }

                public void close() throws IOException {
                }
            });
        }
    }

    private static class ScriptError extends ConsoleWriter {
        public ScriptError(final IConsole console) {
            super(new Writer() {
                public void write(char cbuf[], int off, int len) throws IOException {
                    console.writeScriptErr(cbuf, off, len);
                }

                public void flush() throws IOException {
                }

                public void close() throws IOException {
                }
            });
        }
    }

    private static class CommandOutput extends ConsoleWriter {
        public CommandOutput(final IConsole console) {
            super(new Writer() {
                public void write(char cbuf[], int off, int len) throws IOException {
                    console.writeStdOut(cbuf, off, len);
                }

                public void flush() throws IOException {
                }

                public void close() throws IOException {
                }
            });
        }

    }

    @SuppressWarnings("unused") private static class CommandError extends ConsoleWriter {
        public CommandError(final IConsole console) {
            super(new Writer() {
                public void write(char cbuf[], int off, int len) throws IOException {
                    console.writeStdErr(cbuf, off, len);
                }

                public void flush() throws IOException {
                }

                public void close() throws IOException {
                }
            });
        }

    }

    private IScriptModel scriptModel;
    private String driverURL;
    private IRecordingServer recordingServer;
    private IWebDriverRuntimeLauncherModel launcherModel;
    private IScript script;
    // We don't need this variable. Keep it so that the connection is not
    // closed.
    protected WebDriver driver;

    public WebDriverRuntime(IWebDriverRuntimeLauncherModel launcherModel) {
        this.launcherModel = launcherModel;
    }

    public String createDriver(Map<String, Object> props, MarathonMode mode, IConsole console) {
        int port = -1;
        if (mode == MarathonMode.RECORDING) {
            port = startRecordingServer();
        }
        WriterOutputStream outputStream = new WriterOutputStream(new CommandOutput(console));
        IWebdriverProxy proxy = launcherModel.createDriver(props, port, outputStream);
        driver = proxy.getDriver();
        return proxy.getURL();
    }

    private int startRecordingServer() {
        int port = findPort();
        recordingServer = new HTTPRecordingServer(port);
        recordingServer.start();
        return port;
    }

    private int findPort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } catch (IOException e1) {
            throw new RuntimeException("Could not allocate a port: " + e1.getMessage());
        } finally {
            if (socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                }
        }
    }

    private void replaceEnviron(Map<String, Object> props) {
        Iterator<Entry<String, Object>> iterator = props.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, Object> entry = iterator.next();
            if (entry.getValue() instanceof String) {
                props.put(entry.getKey(), MPFUtils.getUpdatedValue((String) entry.getValue()));
            }
        }
    }

    @Override public void startRecording(IRecorder recorder) {
        recordingServer.startRecording(recorder);
    }

    @Override public void stopRecording() {
        recordingServer.stopRecording();
    }

    @Override public void startApplication() {
        script.runFixtureSetup();
    }

    @Override public void stopApplication() {
        if (recordingServer != null)
            recordingServer.stopRecording();
    }

    @Override public void destroy() {
        if (script != null)
            script.quit();
    }

    @Override public Module getModuleFunctions() {
        return script.getModuleFunctions();
    }

    @Override public void setRawRecording(boolean selected) {
        if (recordingServer != null)
            try {
                recordingServer.setRawRecording(selected);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override public String evaluate(String code) {
        return script.evaluate(code);
    }

    @Override public WindowId getTopWindowId() {
        return recordingServer.getFocusedWindowId();
    }

    @Override public File getScreenCapture() {
        return script.getScreenCapture();
    }

    @Override public void insertScript(String function) {
        try {
            recordingServer.pauseRecording();
            script.exec(function);
        } finally {
            recordingServer.resumeRecording();
        }
    }

    @Override public IScript createScript(MarathonMode mode, IConsole console, String scriptText, String filePath,
            boolean isRecording, boolean isDebugging, Properties dataVariables) {
        scriptModel = ScriptModel.getModel();
        script = scriptModel.createScript(new ScriptOutput(console), new ScriptError(console), scriptText, filePath, isRecording,
                isDebugging, dataVariables);
        if (driverURL == null) {
            Map<String, Object> fixtureProperties = scriptModel.getFixtureProperties(scriptText);
            if (launcherModel.needReplaceEnviron())
                replaceEnviron(fixtureProperties);
            driverURL = createDriver(fixtureProperties, mode, console);
        }
        script.setDriverURL(driverURL);
        return script;
    }

}
