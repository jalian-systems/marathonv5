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
package net.sourceforge.marathon.runtime.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import net.sourceforge.marathon.util.LauncherModelHelper;

public class Constants {

    public static final Logger LOGGER = Logger.getLogger(Constants.class.getName());

    public static enum MarathonMode {
        RECORDING, PLAYING;

    }

    public static File[] getMarathonDirectories(String propKey) throws IOException {
        String testDir = System.getProperty(propKey);
        String[] values = testDir.split(String.valueOf(File.pathSeparatorChar));
        File[] MarathonDirs = new File[values.length];

        for (int i = 0; i < values.length; i++) {
            MarathonDirs[i] = new File(values[i]).getCanonicalFile();
        }

        return MarathonDirs;
    }

    public static File getMarathonDirectory(String propKey) throws IOException {
        return getMarathonDirectories(propKey)[0];
    }

    public static String[] getMarathonDirectoriesAsStringArray(String propKey) {
        String testDir = System.getProperty(propKey);
        if (testDir == null) {
            return new String[0];
        }
        String[] values = testDir.split(String.valueOf(File.pathSeparatorChar));

        return values;
    }

    public static String[] getAllMarathonDirectoriesAsStringArray() {
        ArrayList<String> dirs = new ArrayList<String>();
        String[] propKeys = { PROP_TEST_DIR, PROP_FIXTURE_DIR, PROP_MODULE_DIRS, PROP_CHECKLIST_DIR, PROP_DATA_DIR, PROP_SUITE_DIR,
                PROP_ISSUE_DIR, PROP_FEATURE_DIR, PROP_ISSUE_DIR, PROP_STORY_DIR };
        for (String propKey : propKeys) {
            dirs.addAll(Arrays.asList(getMarathonDirectoriesAsStringArray(propKey)));
        }
        return dirs.toArray(new String[dirs.size()]);
    }

    public static File getMarathonProjectDirectory() {
        String p = System.getProperty(PROP_PROJECT_DIR);
        if (p == null) {
            return null;
        }
        return new File(p);
    }

    public static final String PROP_PROJECT_DIR = "marathon.project.dir";
    public static final String PROP_PROJECT_SCRIPT_MODEL = "marathon.project.script.model";
    public static final String PROP_IMAGE_CAPTURE_DIR = "marathon.image.capture.dir";
    public static final String PROP_REPORT_DIR = "marathon.report.dir";
    public static final String PROP_CHECKLIST_DIR = "marathon.checklist.dir";
    public static final String PROP_RUNTIME_DEFAULT_DELAY = "marathon.runtime.default.delay";
    public static final String PROP_RUNTIME_DELAY = "marathon.runtime.delay";
    public static final String PROP_HOME = "marathon.home";
    public static final String PROP_PROJECT_NAME = "marathon.project.name";
    public static final String PROP_TEST_DIR = "marathon.test.dir";
    public static final String PROP_SUITE_DIR = "marathon.suite.dir";
    public static final String PROP_FEATURE_DIR = "marathon.feature.dir";
    public static final String PROP_ISSUE_DIR = "marathon.issue.dir";
    public static final String PROP_STORY_DIR = "marathon.story.dir";
    public static final String PROP_FIXTURE_DIR = "marathon.fixture.dir";
    public static final String PROP_MODULE_DIRS = "marathon.capture.dir";
    public static final String PROP_DATA_DIR = "marathon.data.dir";
    public static final String PROP_RECORDER_KEYTRIGGER = "marathon.recorder.keytrigger";
    public static final String PROP_RECORDER_MOUSETRIGGER = "marathon.recorder.mousetrigger";
    public static final String PROP_APPLICATION_PATH = "marathon.application.classpath";
    public static final String ENV_APPLICATION_PATH = "MARATHON_APPLICATION_CLASSPATH";
    public static final String PROP_APPLICATION_ARGUMENTS = "marathon.application.arguments";
    public static final String PROP_APPLICATION_MAINCLASS = "marathon.application.mainclass";
    public static final String PROP_APPLICATION_VM_ARGUMENTS = "marathon.application.vm.arguments";
    public static final String PROP_APPLICATION_JAVA_HOME = "marathon.application.java.home";
    public static final String PROP_APPLICATION_WORKING_DIR = "marathon.application.working.dir";
    public static final String PROP_PLAY_MODE_MARK = "marathon.play.mode.mark";
    public static final String PROP_APPLICATION_START_WINDOW = "marathon.application.start.window";
    public static final String PROP_APPLICATION_START_WINDOW_REGEX = "marathon.application.start.window.regex";
    public static final String PROP_PROJECT_DESCRIPTION = "marathon.project.description";
    public static final String PROP_PROPPREFIX = "marathon.properties.";
    public static final String PROP_WINDOW_TIMEOUT = "marathon.WINDOW_TIMEOUT";
    public static final String PROP_USE_FIELD_NAMES = "marathon.USE_FIELD_NAMES";
    public static final String PROP_COMPONENT_WAIT_MS = "marathon.COMPONENT_WAIT_MS";
    public static final String PROP_ISSUE_PATTERN = "allure.issues.tracker.pattern";
    public static final String PROP_TMS_PATTERN = "allure.tests.management.pattern";

    public static final String PREF_RECORDER_MOUSE_TRIGGER = "recorder.mouse.trigger";
    public static final String PREF_RECORDER_KEYBOARD_TRIGGER = "recorder.keyboard.trigger";
    public static final String PREF_NAVIGATOR_HIDEFILES = "navigator.hidefiles";
    public static final String PREF_JUNIT_HIDEFILES = "junit.hidefiles";
    public static final String PREF_ITE_BLURBS = "ite.blurbs";

    public static final String DIR_TESTCASES = "TestCases";
    public static final String DIR_FIXTURES = "Fixtures";
    public static final String DIR_MODULE = "Modules";
    public static final String DIR_CHECKLIST = "Checklists";
    public static final String DIR_DATA = "TestData";
    public static final String DIR_TESTSUITES = "TestSuites";
    public static final String DIR_FEATURES = "Features";
    public static final String DIR_STORIES = "Stories";
    public static final String DIR_ISSUES = "Issues";
    public static final String DIR_TESTREPORTS = "TestReports";
    public static final String PROP_APPLICATION_LAUNCHTIME = "marathon.application.launchtime";
    public static final String PROP_CUSTOM_CONTEXT_MENUS = "marathon.custom.context.menus";
    public static final String PROP_PROFILE_MAIN_CLASS = "marathon.runtime.profile.mainclass";
    public static final String LAUNCHER_MAIN_CLASS = "net.sourceforge.marathon.runtime.JavaRuntimeLauncher";
    public static final String DEFAULT_NAMING_STRATEGY = "net.sourceforge.marathon.objectmap.ObjectMapNamingStrategy";
    public static final String FILE_OMAP_CONFIGURATION = "omap-configuration.yaml";
    public static final String PROP_OMAP_CONFIGURATION_FILE = "net.sourceforge.marathon.objectmap.configuration.file";
    public static final String FILE_OMAP = "omap.yaml";
    public static final String PROP_OMAP_FILE = "net.sourceforge.marathon.objectmap.file";
    public static final String DIR_OMAP = "omap";
    public static final String PROP_OMAP_DIR = "net.sourceforge.marathon.objectmap.directory";
    public static final String FILE_TESTPROPERTIES = "testproperties.yaml";
    public static final String PROP_TEXT_AREA_OUTPUT_SIZE = "net.sourceforge.marathon.textareaoutput.size";
    public static final String PROP_OMAP_RESOLVE_MODE = "net.sourceforge.marathon.objectmap.resolve.mode";
    public static final String PROP_PROJECT_LAUNCHER_MODEL = "marathon.project.launcher.model";
    public static final String FIXTURE_DESCRIPTION = "marathon.fixture.description";
    public static final String FIXTURE_REUSE = "marathon.fixture.reuse";
    public static final String APPLICATION_DONT_MONITOR = "marathon.application.dont.monitor";
    public static final String PROP_PROJECT_FRAMEWORK = "marathon.project.framework";
    public static final String FRAMEWORK_SWING = "swing";
    public static final String FRAMEWORK_FX = "fx";
    public static final String FRAMEWORK_WEB = "web";
    public static final String PROP_BROWSER = "marathon.browser";

    public static InputStream getOMapConfigurationStream() {
        URL resource = Constants.class
                .getResource("/net/sourceforge/marathon/objectmap/default-omap-configuration-" + getFramework() + ".yaml");
        try {
            return resource.openStream();
        } catch (IOException e) {
            return null;
        }
    }

    public static File omapDirectory() {
        File omapDirectory = new File(getMarathonProjectDirectory(), System.getProperty(PROP_OMAP_DIR, DIR_OMAP));
        if (!omapDirectory.exists()) {
            if (!omapDirectory.mkdirs()) {
                throw new RuntimeException("Unable to craete object map directory...");
            }
        }
        return omapDirectory;
    }

    public static String getFramework() {
        String launcher = System.getProperty(PROP_PROJECT_LAUNCHER_MODEL);
        if (launcher == null)
            return System.getProperty(PROP_PROJECT_FRAMEWORK);
        IRuntimeLauncherModel launcherModel = LauncherModelHelper.getLauncherModel(launcher);
        return launcherModel.getFramework();
    }

    public static String getFramework(String launcher) {
        IRuntimeLauncherModel launcherModel = LauncherModelHelper.getLauncherModel(launcher);
        return launcherModel.getFramework();
    }

    public static File getSuiteDirectory() {
        return new File(System.getProperty(PROP_PROJECT_DIR), DIR_TESTSUITES).getAbsoluteFile();
    }

    public static File getFeatureDirectory() {
        return new File(System.getProperty(PROP_PROJECT_DIR), DIR_FEATURES).getAbsoluteFile();
    }

    public static File getStoryDirectory() {
        return new File(System.getProperty(PROP_PROJECT_DIR), DIR_STORIES).getAbsoluteFile();
    }

    public static File getIssueDirectory() {
        return new File(System.getProperty(PROP_PROJECT_DIR), DIR_ISSUES).getAbsoluteFile();
    }

    public static Path getProjectPath() {
        return getMarathonProjectDirectory().toPath().toAbsolutePath();
    }

    public static String getRelativePath(String from, File runReportDir) {
        try {
            Path fromDir = getMarathonDirectory(from).toPath();
            return fromDir.relativize(runReportDir.toPath()).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getTestDirectory() {
        return new File(System.getProperty(PROP_PROJECT_DIR), DIR_TESTCASES).getAbsoluteFile();
    }

    public static boolean isTestFile(File file) {
        try {
            Path testDirectory = Constants.getMarathonDirectory(Constants.PROP_TEST_DIR).toPath().toAbsolutePath();
            return file.toPath().startsWith(testDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isSuiteFile(File file) {
        Path suiteDirectory = Constants.getSuiteDirectory().toPath().toAbsolutePath();
        return file.isFile() && file.toPath().startsWith(suiteDirectory) && file.getName().endsWith(".suite");
    }

    public static boolean isFeatureFile(File file) {
        Path featureDirectory = Constants.getFeatureDirectory().toPath().toAbsolutePath();
        return file.isFile() && file.toPath().startsWith(featureDirectory) && file.getName().endsWith(".feature");
    }

    public static boolean isStoryFile(File file) {
        Path storyDirectory = Constants.getStoryDirectory().toPath().toAbsolutePath();
        return file.isFile() && file.toPath().startsWith(storyDirectory) && file.getName().endsWith(".story");
    }

    public static boolean isIssueFile(File file) {
        Path issueDirectory = Constants.getIssueDirectory().toPath().toAbsolutePath();
        return file.isFile() && file.toPath().startsWith(issueDirectory) && file.getName().endsWith(".issue");
    }

    public static final String AUT_WEBAPP_DEFAULT_BROWSER = "marathon.webapp.default.browser";
    public static final String AUT_WEBAPP_BROWSER_OVERRIDE = "marathon.webapp.browser.override";
    public static final String OUTPUT_MAX_CHARS = "marathon.output.max_chars";
    public static final String OUTPUT_STYLE = "marathon.output.style";
    public static final String NTHREADS = "marathon.grid.nthreads";


}
