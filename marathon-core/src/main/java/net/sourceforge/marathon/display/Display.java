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
package net.sourceforge.marathon.display;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;

import javafx.application.Platform;
import javafx.stage.Stage;
import net.sourceforge.marathon.api.ApplicationLaunchException;
import net.sourceforge.marathon.api.TestAttributes;
import net.sourceforge.marathon.checklist.CheckList;
import net.sourceforge.marathon.junit.DDTestRunner;
import net.sourceforge.marathon.junit.IHasFullname;
import net.sourceforge.marathon.junit.MarathonTestCase;
import net.sourceforge.marathon.providers.PlaybackResultProvider;
import net.sourceforge.marathon.providers.RecorderProvider;
import net.sourceforge.marathon.recorder.IScriptListener;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.Constants.MarathonMode;
import net.sourceforge.marathon.runtime.api.IConsole;
import net.sourceforge.marathon.runtime.api.IMarathonRuntime;
import net.sourceforge.marathon.runtime.api.IPlaybackListener;
import net.sourceforge.marathon.runtime.api.IPlayer;
import net.sourceforge.marathon.runtime.api.IRecorder;
import net.sourceforge.marathon.runtime.api.IRuntimeFactory;
import net.sourceforge.marathon.runtime.api.IRuntimeLauncherModel;
import net.sourceforge.marathon.runtime.api.IScript;
import net.sourceforge.marathon.runtime.api.IScriptElement;
import net.sourceforge.marathon.runtime.api.MarathonRuntimeException;
import net.sourceforge.marathon.runtime.api.Module;
import net.sourceforge.marathon.runtime.api.PlaybackResult;
import net.sourceforge.marathon.runtime.api.ScriptException;
import net.sourceforge.marathon.runtime.api.ScriptModel;
import net.sourceforge.marathon.runtime.api.SourceLine;
import net.sourceforge.marathon.runtime.api.WindowId;
import net.sourceforge.marathon.util.LauncherModelHelper;

public class Display implements IPlaybackListener, IScriptListener, IExceptionReporter, IHasFullname {

    public static final Logger LOGGER = Logger.getLogger(Display.class.getName());

    public static final class DummyRecorder implements IRecorder {
        @Override public void record(IScriptElement element) {
        }

        @Override public void abortRecording() {
        }

        @Override public void insertChecklist(String name) {
        }

        @Override public String recordInsertScriptElement(WindowId windowId, String script) {
            return null;
        }

        @Override public void recordInsertChecklistElement(WindowId windowId, String fileName) {
        }

        @Override public void recordShowChecklistElement(WindowId windowId, String fileName) {
        }

        @Override public boolean isCreatingObjectMap() {
            return true;
        }

        @Override public void updateScript() {
        }
    }

    @Retention(RetentionPolicy.RUNTIME) @BindingAnnotation public @interface IDisplayProperties {
    }

    public static final int LINE_REACHED = 1;
    public static final int METHOD_RETURNED = 2;
    public static final int METHOD_CALLED = 3;
    private static final Logger logger = Logger.getLogger(Display.class.getName());

    private @Inject IRuntimeFactory runtimeFactory;
    private @Inject RecorderProvider recorderProvider;
    private @Inject PlaybackResultProvider playbackResultProvider;

    private IMarathonRuntime runtime;
    private IPlayer player;
    private IDisplayView displayView;
    private State state = State.STOPPED_WITH_APP_CLOSED;
    protected boolean shouldClose = true;
    private IRecorder recorder;
    private String fixture;
    private IScript script;
    private boolean acceptingChecklists;
    private DDTestRunner ddTestRunner;
    private boolean autShutdown = false;
    private boolean playbackStopped;
    private boolean reuseFixture;
    private boolean ignoreReuse = false;
    private boolean debugging;

    public Display() {
    }

    public void setView(IDisplayView pView) {
        recorderProvider.setScriptListener(this);
        this.displayView = pView;
        setState(State.STOPPED_WITH_APP_CLOSED);
    }

    public void destroy() {
    }

    public DDTestRunner getDDTestRunner() {
        return ddTestRunner;
    }

    public void play(IConsole console, boolean debugging) {
        try {
            String scriptText = displayView.getScript();
            if (!validTestCase(scriptText)) {
                reportException(new Exception("No test() function or fixture found in the script"));
                return;
            }
            try {
                playbackStopped = false;
                ddTestRunner = new DDTestRunner(console, scriptText);
            } catch (Exception e) {
                reportException(new Exception(e.getMessage()));
                return;
            }
            if (ddTestRunner.hasNext()) {
                logHeader(console, "Play");
                ddTestRunner.next();
                displayView.startTestRun();
                runTest(debugging);
                return;
            }
        } catch (Throwable t) {
            reportException(t);
            destroyRuntime();
            return;
        }
        reportException(new Exception("No test() function or fixture found in the script"));
    }

    private void logHeader(IConsole console, String type) {
        String filePath = displayView.getFilePath();
        int indexOf = filePath.indexOf(Constants.DIR_TESTCASES);
        if (indexOf != -1) {
            filePath.substring(indexOf + Constants.DIR_TESTCASES.length() + 1);
            String cbuf = "*****  " + type + " " + filePath + " (" + new SimpleDateFormat().format(new Date()) + ") *****\n";
            console.writeScriptOut(cbuf.toCharArray(), 0, cbuf.length());
        }
    }

    private void runTest(boolean debugging) {
        if (ddTestRunner == null) {
            return;
        }
        this.debugging = debugging;
        displayView.startTest();
        createRuntime(ddTestRunner.getScriptText(), MarathonMode.PLAYING);
        script = runtime.createScript(MarathonMode.PLAYING, ddTestRunner.getConsole(), ddTestRunner.getScriptText(),
                displayView.getFilePath(), false, debugging, ddTestRunner.getDataVariables());
        player = script.getPlayer(this, playbackResultProvider.get());
        player.setAcceptCheckList(acceptingChecklists);
        boolean shouldRunFixture = state.isStoppedWithAppClosed();
        setState(State.PLAYING);
        displayView.startInserting();
        player.play(shouldRunFixture);
    }

    private boolean validTestCase(String scriptText) {
        BufferedReader br = new BufferedReader(new StringReader(scriptText));
        String line;
        boolean testFound = false;
        boolean fixtureFound = false;
        try {
            while ((line = br.readLine()) != null) {
                if (line.matches("^def.*test.*().*")) {
                    testFound = true;
                }
                if (line.matches("^#\\{\\{\\{ Marathon")) {
                    fixtureFound = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (testFound && fixtureFound) {
            return true;
        }
        return false;
    }

    public void showResult(PlaybackResult result) {
        if (result.failureCount() == 0) {
            shouldClose = !reuseFixture;
            displayView.trackProgress();
            ignoreReuse = false;
        } else {
            ignoreReuse = true;
            shouldClose = false;
        }
        stopApplicationIfNecessary();
        showResults(result);
    }

    protected void showResults(PlaybackResult result) {
        displayView.setResult(result);
    }

    public void record(IConsole console) {
        logHeader(console, "Record");
        String scriptText = displayView.getScript();
        if (!validTestCase(scriptText)) {
            scriptText = getFixtureHeader() + scriptText;
        }
        try {
            createRuntime(scriptText, MarathonMode.RECORDING);
            Platform.runLater(() -> displayView.startInserting());
            runtime.createScript(MarathonMode.RECORDING, console, scriptText, displayView.getFilePath(), false, true, null);
            recorder = recorderProvider.get();
            startApplicationIfNecessary();
            runtime.startRecording(recorder);
            setState(State.RECORDING);
        } catch (Throwable e) {
            setState(State.STOPPED_WITH_APP_CLOSED);
            destroyRuntime();
            displayView.stopInserting();
            stopApplicationIfNecessary();
            reportException(e);
        }
    }

    private String getFixtureHeader() {
        return ScriptModel.getModel().getFixtureHeader(fixture);
    }

    public void resume() {
        if (state.isRecordingPaused()) {
            runtime.startRecording(recorder);
            setState(State.RECORDING);
        } else {
            script.getDebugger().resume();
            setState(State.PLAYING);
        }
    }

    private void startApplicationIfNecessary() {
        if (state.isStoppedWithAppClosed()) {
            runtime.startApplication();
        }
    }

    public void stop() {
        if (state.isRecording() || state.isRecordingPaused()) {
            try {
                runtime.stopRecording();
            } catch (MarathonRuntimeException e) {
                setState(State.STOPPED_WITH_APP_CLOSED);
                destroyRuntime();
                throw e;
            } finally {
                displayView.stopInserting();
            }
            stopApplicationIfNecessary();
            displayView.updateOMapFile();
        } else if (state.isPlaying()) {
            try {
                player.halt();
            } catch (MarathonRuntimeException e) {
                reportException(e);
            } finally {
                playbackStopped = true;
                playbackFinished(playbackResultProvider.get(), false);
            }
        } else {
            throw new IllegalStateException("must be recording or playing to stop, not '" + state + "'");
        }
    }

    public void pauseRecording() {
        if (state.isRecording()) {
            try {
                runtime.stopRecording();
                setState(State.RECORDINGPAUSED);
            } catch (MarathonRuntimeException e) {
                setState(State.STOPPED_WITH_APP_CLOSED);
                destroyRuntime();
                throw e;
            }
        } else {
            throw new IllegalStateException("must be recording for the pause to happen");
        }
    }

    protected void stopApplicationIfNecessary() {
        boolean closeApplicationNeeded = state.isPlaying() == false;
        if (autShutdown) {
            setState(State.STOPPED_WITH_APP_CLOSED);
            runtime = null;
        } else {
            setState(State.STOPPED_WITH_APP_OPEN);
            if (runtime != null)
                runtime.releaseInterpreters();
        }
        if (shouldClose || playbackStopped) {
            closeApplication(closeApplicationNeeded);
        }
    }

    public void openApplication(IConsole console) {
        logHeader(console, "Open");
        createRuntime(getFixtureHeader(), MarathonMode.RECORDING);
        runtime.createScript(MarathonMode.RECORDING, console, getFixtureHeader(), "", false, false, null);
        startApplicationIfNecessary();
        setState(State.STOPPED_WITH_APP_OPEN);
        shouldClose = false;
    }

    public void closeApplication(boolean closeApplicationNeeded) throws RuntimeException {
        /*
         * We need to actually call the stopApplication that calls the teardown
         * on the fixture. However, the fixture that created the application (by
         * using setup) is already lost and we can't communicate with the app
         * using fixture when manually starting the application. For making this
         * work, we need changes in the semantics of the Script and Runtime
         */
        try {
            if (closeApplicationNeeded && runtime != null && !autShutdown) {
                runtime.stopApplication();
            }
        } catch (Exception e) {
            displayView.setError(e, "Application Under Test Aborted");
        } finally {
            destroyRuntime();
            shouldClose = true;
            setState(State.STOPPED_WITH_APP_CLOSED);
        }
    }

    public void destroyRuntime() {
        if (runtime != null) {
            logger.info("Destroying VM. autShutdown = " + autShutdown);
            try {
                runtime.releaseInterpreters();
                if (!autShutdown) {
                    runtime.destroy();
                }
            } finally {
                runtime = null;
            }
        }
        WaitMessageDialog.setVisible(false);
    }

    public State getState() {
        return state;
    }

    public void setState(State pState) {
        state = pState;
        displayView.setState(state);
    }

    private void createRuntime(String scriptText, MarathonMode mode) {
        IRuntimeFactory rf = getRuntimeFactory(scriptText);
        if (runtime == null || !reuseFixture || ignoreReuse) {
            if (runtime != null) {
                closeApplication(true);
            }
            runtime = rf.createRuntime();
        }
        assert runtime != null;
        this.autShutdown = false;
    }

    public Module getModuleFuctions() {
        if (runtime == null) {
            return null;
        }
        return runtime.getModuleFunctions();
    }

    public String insertScript(String function) {
        WindowId topWindowId = runtime.getTopWindowId();
        runtime.insertScript(function);
        String s = recorder.recordInsertScriptElement(topWindowId, function);
        return s;
    }

    public boolean canOpenFile() {
        return state == State.STOPPED_WITH_APP_CLOSED || state == State.STOPPED_WITH_APP_OPEN;
    }

    public void setDefaultFixture(String pFixture) {
        fixture = pFixture;
    }

    public void setRawRecording(boolean selected) {
        runtime.setRawRecording(selected);
    }

    public void pausePlay() {
        if (state.isPlaying()) {
            displayView.setState(State.PLAYINGPAUSED);
        }
    }

    public String evaluateScript(String code) {
        if (state.isRecordingPaused()) {
            return runtime.evaluate(code);
        } else {
            return script.getDebugger().evaluateScriptWhenPaused(code);
        }
    }

    public void insertChecklist(String name) {
        recorder.recordInsertChecklistElement(runtime.getTopWindowId(), name);
    }

    public File getScreenCapture() {
        return runtime.getScreenCapture();
    }

    public void setAcceptChecklist(boolean selected) {
        this.acceptingChecklists = selected;
    }

    public void recordShowChecklist(String fileName) {
        recorder.recordShowChecklistElement(runtime.getTopWindowId(), fileName);
    }

    public String getTopWindowName() {
        if (runtime == null || runtime.getTopWindowId() == null) {
            return null;
        }
        return runtime.getTopWindowId().getTitle();
    }

    /* Implementation of IPlaybackListener * */
    @Override public void playbackFinished(final PlaybackResult result, boolean shutdown) {
        this.autShutdown = shutdown;
        displayView.endTest(result);
        if (ddTestRunner != null && ddTestRunner.hasNext() && !playbackStopped) {
            ddTestRunner.next();
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    if (result.failureCount() == 0) {
                        shouldClose = !reuseFixture;
                        displayView.trackProgress();
                        ignoreReuse = false;
                    } else {
                        ignoreReuse = true;
                        shouldClose = false;
                    }
                    stopApplicationIfNecessary();
                    runTest(debugging);
                }
            });
            return;
        }
        displayView.stopInserting();
        if (Platform.isFxApplicationThread()) {
            showResult(result);
        } else {
            Platform.runLater(() -> showResult(result));
        }
        displayView.endTestRun();
        ddTestRunner = null;
    }

    @Override public int lineReached(SourceLine line) {
        return displayView.trackProgress(line, LINE_REACHED);
    }

    @Override public int methodReturned(SourceLine line) {
        return displayView.trackProgress(line, METHOD_RETURNED);
    }

    @Override public int methodCalled(SourceLine line) {
        return displayView.trackProgress(line, METHOD_CALLED);
    }

    @Override public int acceptChecklist(String fileName) {
        return displayView.acceptChecklist(fileName);
    }

    @Override public int showChecklist(String fileName) {
        return displayView.showChecklist(fileName);
    }

    /** Implementation of IScriptListener **/
    @Override public void setScript(String script) {
        displayView.insertScript(script);
    }

    @Override public void abortRecording() {
        if (state.isRecording()) {
            displayView.stopInserting();
        }
        shouldClose = true;
        runtime = null;
        displayView.updateOMapFile();
        setState(State.STOPPED_WITH_APP_CLOSED);
    }

    @Override public void insertChecklistAction(String name) {
        displayView.insertChecklistAction(name);
    }

    /** Implementation IExceptionReporter **/
    @Override public void reportException(Throwable e) {
        if (e instanceof ApplicationLaunchException) {
            destroyRuntime();
        }
        displayView.setError(e,
                e.getClass().getName().substring(e.getClass().getName().lastIndexOf('.') + 1) + " : " + e.getMessage());
    }

    @Override public void addImportStatement(String ims) {
        displayView.addImport(ims);
    }

    public IRuntimeFactory getRuntimeFactory(String scriptText) {
        Map<String, Object> fixtureProperties = ScriptModel.getModel().getFixtureProperties(scriptText);
        if (fixtureProperties == null || fixtureProperties.size() == 0) {
            return runtimeFactory;
        }
        reuseFixture = Boolean.valueOf((String) fixtureProperties.get(Constants.FIXTURE_REUSE));
        TestAttributes.put("reuseFixture", reuseFixture && (TestAttributes.get("reuseFixture") == null));
        String launcherModel = (String) fixtureProperties.get(Constants.PROP_PROJECT_LAUNCHER_MODEL);
        IRuntimeLauncherModel lm = LauncherModelHelper.getLauncherModel(launcherModel);
        if (lm == null) {
            return runtimeFactory;
        }
        return lm.getRuntimeFactory();
    }

    public CheckList fillUpChecklist(MarathonTestCase testCase, File file, Stage view) {
        return testCase.showAndEnterChecklist(file, runtime, view);
    }

    public void omapCreate(IConsole console) {
        try {
            createRuntime(getFixtureHeader(), MarathonMode.RECORDING);
            runtime.createScript(MarathonMode.RECORDING, console, getFixtureHeader(), "Objectmap Creation", false, true, null);
            startApplicationIfNecessary();
            runtime.startRecording(new DummyRecorder());
            setState(State.STOPPED_WITH_APP_OPEN);
        } catch (ScriptException e) {
            setState(State.STOPPED_WITH_APP_CLOSED);
            destroyRuntime();
            stopApplicationIfNecessary();
            reportException(e);
        }
    }

    @Override public String getFullName() {
        return "Display";
    }

}
