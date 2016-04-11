package net.sourceforge.marathon.junit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.google.inject.Inject;
import com.google.inject.Injector;

import junit.framework.Test;
import junit.framework.TestCase;
import net.sourceforge.marathon.Main;
import net.sourceforge.marathon.checklist.CheckList;
import net.sourceforge.marathon.checklist.CheckListDialog;
import net.sourceforge.marathon.checklist.CheckListForm;
import net.sourceforge.marathon.checklist.CheckListForm.Mode;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.Constants.MarathonMode;
import net.sourceforge.marathon.runtime.api.IConsole;
import net.sourceforge.marathon.runtime.api.IMarathonRuntime;
import net.sourceforge.marathon.runtime.api.IPlaybackListener;
import net.sourceforge.marathon.runtime.api.IPlayer;
import net.sourceforge.marathon.runtime.api.IRuntimeFactory;
import net.sourceforge.marathon.runtime.api.IRuntimeLauncherModel;
import net.sourceforge.marathon.runtime.api.IScript;
import net.sourceforge.marathon.runtime.api.PlaybackResult;
import net.sourceforge.marathon.runtime.api.ScriptModel;
import net.sourceforge.marathon.runtime.api.SourceLine;
import net.sourceforge.marathon.runtime.api.UIUtils;
import net.sourceforge.marathon.screencapture.AnnotateScreenCapture;
import net.sourceforge.marathon.util.LauncherModelHelper;

public class MarathonTestCase extends TestCase implements IPlaybackListener, Test, IHasFullname {

    private static final Logger logger = Logger.getLogger(MarathonTestCase.class.getCanonicalName());

    private @Inject IRuntimeFactory runtimeFactory;

    private File file;
    private static IMarathonRuntime runtime = null;
    private final Object waitLock = new Object();
    private PlaybackResult result;
    private String suffix;
    private IScript script;
    private ArrayList<CheckList> checkLists = new ArrayList<CheckList>();
    private ArrayList<File> screenCaptures = new ArrayList<File>();
    private boolean acceptChecklist;
    private IConsole console;
    private Properties dataVariables;
    private String nameSuffix = "";

    private String fullName;

    private boolean reuseFixture;
    private boolean ignoreReuse;

    private boolean shouldRunFixture;

    public MarathonTestCase(File file, boolean acceptChecklist, IConsole console) {
        this(file, null);
        this.acceptChecklist = acceptChecklist;
        this.console = console;
    }

    MarathonTestCase(File file, IMarathonRuntime runtime) {
        this.file = file;
        if (runtime != null)
            MarathonTestCase.runtime = runtime;
        suffix = ScriptModel.getModel().getSuffix();
        this.acceptChecklist = false;
        Injector injector = Main.getInjector();
        injector.injectMembers(this);
    }

    public MarathonTestCase(File file, boolean acceptChecklist, IConsole console, Properties dataVariables, String name) {
        this(file, acceptChecklist, console);
        this.dataVariables = dataVariables;
        this.nameSuffix = name;
    }

    public String getName() {
        String name = file.getName();

        if (name.endsWith(suffix)) {
            name = name.substring(0, name.length() - suffix.length());
        }
        return name + nameSuffix;
    }

    protected synchronized void runTest() throws Throwable {
        if (script == null)
            initialize();
        try {
            IPlayer player = script.getPlayer(MarathonTestCase.this, new PlaybackResult());
            player.setAcceptCheckList(acceptChecklist);
            synchronized (waitLock) {
                player.play(shouldRunFixture);
                waitLock.wait();
            }
            confirmResult();
        } catch (Throwable t) {
            throw t;
        } finally {
            if (runtime != null && (!reuseFixture || ignoreReuse)) {
                logger.info("Destroying VM");
                runtime.destroy();
                runtime = null;
            }
            script = null;
        }
    }

    private String getScriptContents() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringWriter sWriter = new StringWriter(8192);
        PrintWriter writer = new PrintWriter(sWriter);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
        } finally {
            writer.close();
            reader.close();
        }
        return sWriter.toString();
    }

    public void playbackFinished(PlaybackResult result, boolean shutdown) {
        this.result = result;
        synchronized (waitLock) {
            waitLock.notify();
        }
    }

    public int lineReached(SourceLine line) {
        return CONTINUE;
    }

    private void confirmResult() {
        ignoreReuse = false;
        if (result.failureCount() == 0)
            return;
        ignoreReuse = true;
        String dirName = System.getProperty(Constants.PROP_IMAGE_CAPTURE_DIR);
        if (dirName != null) {
            File dir = new File(dirName);
            File[] files = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.matches(Pattern.quote(getFullName()) + "-error[0-9]*.png");
                }
            });
            for (File file : files) {
                addErrorScreenCapture(file);
            }
        }
        MarathonAssertion e = new MarathonAssertion(result.failures(), this.getName());
        throw e;
    }

    private void addErrorScreenCapture(File file) {
        screenCaptures.add(file);
    }

    public File getFile() {
        return file;
    }

    public int methodReturned(SourceLine line) {
        return CONTINUE;
    }

    public int methodCalled(SourceLine line) {
        return CONTINUE;
    }

    public int acceptChecklist(final String fileName) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                File file = new File(System.getProperty(Constants.PROP_CHECKLIST_DIR), fileName);
                showAndEnterChecklist(file, runtime, null);
                script.getDebugger().resume();
            }
        });
        return 0;
    }

    public int showChecklist(final String fileName) {
        return 0;
    }

    public ArrayList<CheckList> getChecklists() {
        return checkLists;
    }

    public void addChecklist(CheckList checkList) {
        checkLists.add(checkList);
    }

    public File[] getScreenCaptures() {
        return screenCaptures.toArray(new File[screenCaptures.size()]);
    }

    public void addScreenCapture(File newFile) {
        screenCaptures.add(newFile);
    }

    public CheckList showAndEnterChecklist(File file, final IMarathonRuntime runtime, final JFrame instance) {
        final CheckList checklist;
        try {
            checklist = CheckList.read(file);
            CheckListForm checklistForm = new CheckListForm(checklist, Mode.ENTER);
            final CheckListDialog dialog = new CheckListDialog((JFrame) null, checklistForm);

            JButton screenCapture = UIUtils.createScreenCaptureButton();
            screenCapture.addActionListener(new ActionListener() {
                File captureFile = null;

                public void actionPerformed(ActionEvent e) {
                    boolean iconify = false;
                    int state = -1;
                    try {
                        if (instance != null && instance.getState() != JFrame.ICONIFIED) {
                            iconify = true;
                            state = instance.getState();
                        }
                        if (iconify)
                            instance.setState(JFrame.ICONIFIED);
                        dialog.setVisible(false);
                        if (captureFile == null)
                            captureFile = runtime.getScreenCapture();
                        if (captureFile == null) {
                            JOptionPane.showMessageDialog(null, "Could not create a screen capture");
                            return;
                        }
                        try {
                            AnnotateScreenCapture annotate = new AnnotateScreenCapture(captureFile, true);
                            if (annotate.showDialog() == AnnotateScreenCapture.APPROVE_OPTION) {
                                annotate.saveToFile(captureFile);
                                checklist.setCaptureFile(captureFile.getName());
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } finally {
                        if (iconify)
                            instance.setState(state);
                        dialog.setVisible(true);
                    }
                }
            });
            JButton saveButton = UIUtils.createSaveButton();
            saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });
            dialog.setActionButtons(new JButton[] { screenCapture, saveButton });
            dialog.setVisible(true);
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, "Unable to read the checklist file");
            return null;
        }
        addChecklist(checklist);
        return checklist;
    }

    @Override public String toString() {
        return getName();
    }

    public synchronized void setDataVariables(Properties dataVariables) {
        this.dataVariables = dataVariables;
    }

    public IRuntimeFactory getRuntimeFactory(String scriptText) {
        Map<String, Object> fixtureProperties = ScriptModel.getModel().getFixtureProperties(scriptText);
        if (fixtureProperties == null || fixtureProperties.size() == 0)
            return runtimeFactory;
        reuseFixture = Boolean.valueOf((String) fixtureProperties.get(Constants.FIXTURE_REUSE)).booleanValue();
        String launcherModel = (String) fixtureProperties.get(Constants.PROP_PROJECT_LAUNCHER_MODEL);
        IRuntimeLauncherModel lm = LauncherModelHelper.getLauncherModel(launcherModel);
        if (lm == null)
            return runtimeFactory;
        return lm.getRuntimeFactory();
    }

    public void setFullName(String name) {
        this.fullName = name;
    }

    public String getFullName() {
        return fullName;
    }

    public static void reset() {
        if (runtime != null)
            runtime.destroy();
        runtime = null;
    }

    public void initialize() throws Exception {
        checkLists.clear();
        screenCaptures.clear();
        String scriptText = getScriptContents();
        IRuntimeFactory rf = getRuntimeFactory(scriptText);
        shouldRunFixture = false;
        if (runtime == null || !reuseFixture) {
            // This condition is added for Unit Testing purposes.
            if (runtime == null || !runtime.getClass().getName().equals("net.sourceforge.marathon.runtime.RuntimeStub")) {
                if (runtime != null)
                    runtime.destroy();
                shouldRunFixture = true;
                runtime = rf.createRuntime();
            }
        }
        script = runtime.createScript(MarathonMode.PLAYING, console, scriptText, file.getAbsolutePath(), false, true,
                dataVariables);
    }
}
