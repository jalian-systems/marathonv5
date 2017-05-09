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
package net.sourceforge.marathon;

/*******************************************************************************
 *
 *  Copyright (C) 2010 Jalian Systems Private Ltd.
 *  Copyright (C) 2010 Contributors to Marathon OSS Project
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Project website: http://www.marathontesting.com
 *  Help: Marathon help forum @ http://groups.google.com/group/marathon-testing
 *
 *******************************************************************************/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.junit.runner.Result;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import net.sourceforge.marathon.api.GuiceInjector;
import net.sourceforge.marathon.display.DisplayWindow;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.display.VersionInfo;
import net.sourceforge.marathon.fx.projectselection.EditProjectHandler;
import net.sourceforge.marathon.fx.projectselection.NewProjectHandler;
import net.sourceforge.marathon.fx.projectselection.ProjectInfo;
import net.sourceforge.marathon.fx.projectselection.ProjectSelection;
import net.sourceforge.marathon.junit.textui.TestRunner;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.MPFUtils;
import net.sourceforge.marathon.runtime.api.NullLogger;
import net.sourceforge.marathon.runtime.api.OSUtils;
import net.sourceforge.marathon.runtime.api.Preferences;
import net.sourceforge.marathon.runtime.api.ProjectFile;
import net.sourceforge.marathon.runtime.api.RuntimeLogger;

/**
 * Main entry point into Marathon application.
 */
public class RealMain {

    public static final Logger LOGGER = Logger.getLogger(RealMain.class.getName());

    private static ArgumentProcessor argProcessor = new ArgumentProcessor();

    /**
     * Entry point into Marathon application. Invoke main on the command line
     * using the <code>net.sourceforge.marathon.Main</code> class.
     *
     * @param args
     *            - Arguments passed on the command line. Invoke with
     *            <code>-help</code> to see available options.
     */
    public static void realmain(String[] args) {
        String vers = System.getProperty("mrj.version");
        if (vers == null) {
            System.setProperty("mrj.version", "1070.1.6.0_26-383");
        }
        argProcessor.process(args);
        if (!argProcessor.isBatchMode()) {
            runGUIMode();
        } else {
            runBatchMode();
        }
    }

    /**
     * Run Marathon in batch mode.
     */
    private static void runBatchMode() {
        String projectDir = argProcessor.getProjectDirectory();
        if (projectDir == null) {
            argProcessor.help("No project directory");
            return;
        }
        if (projectDir.endsWith(".mpf") && new File(projectDir).isFile()) {
            argProcessor.help("A marathon project file is given.\nUse project directory instead");
            return;
        }
        processMPF(projectDir);
        initializeInjector();
        OSUtils.setLogConfiguration(projectDir);
        RuntimeLogger.setRuntimeLogger(new NullLogger());
        cleanResultFolder();
        TestRunner aTestRunner = createTestRunner();
        try {
            Result r = aTestRunner.runTests(argProcessor);
            if (!r.wasSuccessful()) {
                System.exit(junit.textui.TestRunner.FAILURE_EXIT);
            }
            System.exit(junit.textui.TestRunner.SUCCESS_EXIT);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            System.exit(junit.textui.TestRunner.EXCEPTION_EXIT);
        }
    }

    private static void cleanResultFolder() {
        File folder = new File(argProcessor.getReportDir());
        deleteFolder(folder);
        folder.mkdirs();
    }

    private static void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] listFiles = folder.listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    deleteFolder(file);
                }
            }
        }
        folder.delete();
    }

    private static TestRunner createTestRunner() {
        return new TestRunner();
    }

    /**
     * Run Marathon in GUI mode with all bells and whistles.
     *
     * @param licensee
     */
    private static void runGUIMode() {
        showSplash();
        String projectDir = getProjectDirectory(argProcessor.getProjectDirectory());
        if (projectDir == null) {
            System.exit(0);
        }
        processMPF(projectDir);
        initializeInjector();
        OSUtils.setLogConfiguration(projectDir);
        Injector injector = GuiceInjector.get();
        final DisplayWindow display = injector.getInstance(DisplayWindow.class);
        display.setVisible(true);
    }

    private static void initializeInjector() {
        GuiceInjector.set(Guice.createInjector(new MarathonGuiceModule()));
    }

    /**
     * Show the splash screen. The splash can be suppressed by giving
     * <code>-nosplash</code> on command line.
     *
     * @param licensee
     */
    private static void showSplash() {
        if (argProcessor.showSplash()) {
            MarathonSplashScreen splashScreen = new MarathonSplashScreen(new VersionInfo(Version.id(), Version.blurbTitle(),
                    Version.blurbCompany(), Version.blurbWebsite(), Version.blurbCredits())) {
                @Override public void dispose() {
                    super.dispose();
                }
            };
            splashScreen.getStage().showAndWait();
        }
    }

    /**
     * Called when no MPF is given on command line while running Marathon in GUI
     * mode. Pops up a dialog for selecting a MPF.
     *
     * @param arg
     *            , the MPF given on command line, null if none given
     * @return MPF selected by the user. Can be null.
     */
    private static String getProjectDirectory(final String arg) {
        if (arg != null && !ProjectFile.isValidProjectDirectory(new File(arg))) {
            argProcessor.help("`" + arg + "`Please provide a Marathon project folder.");
        }
        if (arg != null) {
            return arg;
        }
        List<String> selectedProjects = new ArrayList<>();
        ObservableList<ProjectInfo> projectList = FXCollections.observableArrayList();
        List<List<String>> frameworks = Arrays.asList(Arrays.asList("Java/Swing Project", Constants.FRAMEWORK_SWING),
                Arrays.asList("Java/FX Project", Constants.FRAMEWORK_FX));
        ProjectSelection selection = new ProjectSelection(projectList, frameworks) {
            @Override protected void onSelect(ProjectInfo selected) {
                super.onSelect(selected);
                selectedProjects.add(selected.getFolder());
                dispose();
            }
        };
        Stage stage = selection.getStage();
        selection.setNewProjectHandler(new NewProjectHandler(stage));
        selection.setEditProjectHandler(new EditProjectHandler(stage));
        stage.showAndWait();
        if (selectedProjects.size() == 0) {
            return null;
        }
        Preferences.resetInstance();
        return selectedProjects.get(0);
    }

    /**
     * Process the given MPF.
     *
     * @param mpf
     *            , Marathon project file. a suffix '.mpf' is added if the given
     *            name does not end with it.
     */
    public static void processMPF(String projectDir) {
        try {
            File file = new File(projectDir);
            projectDir = file.getCanonicalPath();
            System.setProperty(Constants.PROP_PROJECT_DIR, projectDir);
            Properties mpfProps = ProjectFile.getProjectProperties();
            checkForScriptModel(projectDir, mpfProps);
            MPFUtils.convertPathChar(mpfProps);
            MPFUtils.replaceEnviron(mpfProps);
            Properties props = System.getProperties();
            props.putAll(mpfProps);
            System.setProperties(props);
        } catch (FileNotFoundException e) {
            FXUIUtils.showMessageDialog(null, "Unable to open Marathon Project File " + e.getMessage(), "Error", AlertType.ERROR);
            System.exit(1);
        } catch (IOException e) {
            FXUIUtils.showMessageDialog(null, "Unable to read Marathon Project File " + e.getMessage(), "Error", AlertType.ERROR);
            System.exit(1);
        }
        String userDir = System.getProperty(Constants.PROP_PROJECT_DIR);
        if (userDir != null && !userDir.equals("") && System.getProperty("user.dir") == null) {
            System.setProperty("user.dir", userDir);
        }
        checkForProperties();
        if (!dirExists(Constants.PROP_MODULE_DIRS) || !dirExists(Constants.PROP_TEST_DIR) || !dirExists(Constants.PROP_FIXTURE_DIR)
                || !dirExists(Constants.PROP_CHECKLIST_DIR)) {
            System.exit(1);
        }
    }

    private static void checkForScriptModel(String projectDir, Properties mpfProps) {
        String scriptModel = mpfProps.getProperty(Constants.PROP_PROJECT_SCRIPT_MODEL);
        if ("net.sourceforge.marathon.ruby.RubyScriptModel".equals(scriptModel)) {
            return;
        }
        String message = "This project is configured with MarahtonITE.\n" + "You can't use Marathon to open it.";
        FXUIUtils.showMessageDialog(null, message, "Script Model", AlertType.ERROR);
        System.exit(1);
    }

    /**
     * The user selected properties are set with 'marathon.properties' prefix in
     * the MPF files. This function removes this prefix (if exist).
     *
     * @param mpfProps
     *            , properties for which the substitution need to be performed.
     * @return new property list.
     */
    public static Properties removePrefixes(Properties mpfProps) {
        Enumeration<Object> enumeration = mpfProps.keys();
        Properties props = new Properties();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String value = mpfProps.getProperty(key);
            if (key.startsWith(Constants.PROP_PROPPREFIX)) {
                key = key.substring(Constants.PROP_PROPPREFIX.length());
                props.setProperty(key, value);
            } else if (!props.containsKey(key)) {
                props.setProperty(key, value);
            }
        }
        return props;
    }

    /**
     * Given a directory key like marathon.test.dir check whether given
     * directory exists.
     *
     * @param dirKey
     *            , a property key
     * @return true, if the directory exists
     */
    private static boolean dirExists(String dirKey) {
        String dirName = System.getProperty(dirKey);
        if (dirKey != null) {
            dirName = dirName.replace(';', File.pathSeparatorChar);
            dirName = dirName.replace('/', File.separatorChar);
            System.setProperty(dirKey, dirName);
        }
        dirName = System.getProperty(dirKey);
        String[] values = dirName.split(String.valueOf(File.pathSeparatorChar));
        for (String value : values) {
            File dir = new File(value);
            if (!dir.exists() || !dir.isDirectory()) {
                FXUIUtils.showMessageDialog(null, "Invalid directory specified for " + dirKey + " - " + dirName, "Error",
                        AlertType.ERROR);
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether the mandatory properties are given.
     */
    private static void checkForProperties() {
        List<String> missingProperties = new ArrayList<String>();
        missingProperties.add("The following properties are not given.");
        String[] reqdProperties = { Constants.PROP_FIXTURE_DIR, Constants.PROP_TEST_DIR, Constants.PROP_MODULE_DIRS,
                Constants.PROP_CHECKLIST_DIR };
        for (String reqdPropertie : reqdProperties) {
            if (System.getProperty(reqdPropertie) == null) {
                missingProperties.add(reqdPropertie);
            }
        }
        if (missingProperties.size() > 1) {
            FXUIUtils.showMessageDialog(null, missingProperties.toString(), "Missing Properties", AlertType.ERROR);
            System.exit(1);
        }
    }

}
