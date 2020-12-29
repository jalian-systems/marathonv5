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
package net.sourceforge.marathon.junit;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.json.JSONObject;
import net.sourceforge.marathon.model.Group;
import net.sourceforge.marathon.model.Group.GroupType;
import net.sourceforge.marathon.model.GroupEntry;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IConsole;
import net.sourceforge.marathon.runtime.api.ScriptModel;
import net.sourceforge.marathon.testrunner.fxui.TestRunnerHistory;
import net.sourceforge.marathon.util.FilePatternMatcher;

public class TestCreator {

    public static final Logger LOGGER = Logger.getLogger(TestCreator.class.getName());

    private static String hideFilePattern = "";
    private static FilePatternMatcher hiddenFPM;

    static {
        hideFilePattern = "\\..* .*\\.class \\Q__init__.py\\E \\QExploratoryTests\\E";
        hiddenFPM = new FilePatternMatcher(TestCreator.hideFilePattern);
    }

    private String sourcePath;
    private String suffix;
    private boolean acceptChecklist;
    private final IConsole console;
    private boolean ignoreDDT = false;

    public TestCreator(boolean acceptChecklist, IConsole console) throws IOException {
        this.console = console;
        sourcePath = new File(System.getProperty(Constants.PROP_TEST_DIR)).getCanonicalPath();
        suffix = ScriptModel.getModel().getSuffix();
        this.acceptChecklist = acceptChecklist;
    }

    public File getFile(String testcase) {
        String filename = convertDotFormat(testcase, sourcePath);
        if (filename.endsWith("AllTests")) {
            filename = filename.substring(0, filename.length() - 9);
        } else {
            filename += suffix;
        }
        return new File(filename);
    }

    private String convertDotFormat(String filename, String sourcePath) {
        return sourcePath + File.separatorChar + filename.replace('.', File.separatorChar);
    }

    public Test getTest(List<String> testCases) {
        TestSuite suite;
        if (testCases.size() == 1) {
            Test test = getTest(testCases.get(0));
            if (test != null && test instanceof TestSuite) {
                return test;
            }
        }
        suite = new TestSuite("Marathon Test");
        for (int i = 0; i < testCases.size(); i++) {
            Test test = getTest(testCases.get(i));
            if (test != null) {
                suite.addTest(test);
            }
        }
        return suite;
    }

    public Test getTest(String name) {
        try {
            if (name.startsWith("+")) {
                return getGroup(GroupType.SUITE, name.substring(1));
            }
            if (name.equals("AllSuites")) {
                return getAllTestsForGroups(GroupType.SUITE);
            }
            if (name.startsWith("@")) {
                return getGroup(GroupType.FEATURE, name.substring(1));
            }
            if (name.equals("AllFeatures")) {
                return getAllTestsForGroups(GroupType.FEATURE);
            }
            if (name.startsWith("#")) {
                return getGroup(GroupType.STORY, name.substring(1));
            }
            if (name.equals("AllStories")) {
                return getAllTestsForGroups(GroupType.STORY);
            }
            if (name.startsWith("!")) {
                return getGroup(GroupType.ISSUE, name.substring(1));
            }
            if (name.equals("AllIssues")) {
                return getAllTestsForGroups(GroupType.ISSUE);
            }
            if (name.startsWith("~")) {
                return getRunHistory(name.substring(1));
            }
            return getTest(getFile(name), name);
        } catch (IOException e) {
            Logger.getLogger(TestCreator.class.getName()).warning("Unable to create a test for " + name);
            return null;
        }
    }

    private Test getRunHistory(String name) {
        JSONArray tests = TestRunnerHistory.getInstance().getHistory("favourites");
        for (int i = 0; i < tests.length(); i++) {
            JSONObject testObject = tests.getJSONObject(i);
            if (name.equals(testObject.getString("name"))) {
                return createTest(testObject);
            }
        }
        return null;
    }

    private Test createTest(JSONObject his) {
        TestSuite testSuite = new TestSuite(his.getString("name"));
        JSONArray tests = his.getJSONArray("tests");
        for (int i = 0; i < tests.length(); i++) {
            JSONObject test = tests.getJSONObject(i);
            if (test.has("tests")) {
                if (test.has("isDDT") && test.getBoolean("isDDT")) {
                    Test marathonTest = createMarathonTest(test);
                    if (marathonTest != null) {
                        testSuite.addTest(marathonTest);
                    }
                } else {
                    testSuite.addTest(createTest(test));
                }
            } else {
                Test marathonTest = createMarathonTest(test);
                if (marathonTest != null) {
                    testSuite.addTest(marathonTest);
                }
            }
        }
        return testSuite;
    }

    private Test createMarathonTest(JSONObject test) {
        try {
            TestCreator testCreator = new TestCreator(acceptChecklist, console);
            File base = new File(System.getProperty(Constants.PROP_TEST_DIR), test.getString("path"));
            if (!base.exists()) {
                return null;
            }
            return testCreator.getTest(base, test.getString("name"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setIgnoreDDTSuites(boolean ignoreDDT) {
        this.ignoreDDT = ignoreDDT;
    }

    public Test getGroup(GroupType type, String groupName) throws IOException {
        File file = getGroupFile(type, groupName);
        if (file == null) {
            return null;
        }
        TestSuite testSuite = getTest(Group.findByFile(type, file.toPath()));
        return testSuite;
    }

    public TestSuite getTest(Group suite) {
        TestSuite testSuite = new TestSuite(suite.getName());
        List<GroupEntry> tests = suite.getEntries();
        for (GroupEntry suiteEntry : tests) {
            try {
                testSuite.addTest(suiteEntry.getTest(acceptChecklist, console));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return testSuite;
    }

    public File getGroupFile(GroupType type, String suiteName) {
        String filename = convertDotFormat(suiteName, type.dir().getAbsolutePath());
        filename += type.ext();
        File file = new File(filename);
        if (file.exists()) {
            return file;
        }
        Group group = Group.findByName(type, suiteName);
        if (group == null) {
            return null;
        }
        return group.getPath().toFile();
    }

    public Test getTest(File file, String name) throws IOException {
        if (file.isFile()) {
            if (isDDT(file) && !ignoreDDT) {
                return createDDTTest(file);
            }
            return createTest(file, name);
        }
        File[] fileList = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return !hiddenFPM.isMatch(file);
                }
                return file.getName().endsWith(suffix) && ScriptModel.getModel().isTestFile(file) && !hiddenFPM.isMatch(file);
            }
        });
        if (fileList == null) {
            throw new IOException("Could not list files for " + file);
        }
        if (fileList.length == 0) {
            return null;
        }
        Arrays.sort(fileList, new Comparator<File>() {
            @Override
            public boolean equals(Object obj) {
                return false;
            }

            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public int compare(File f1, File f2) {
                if (f1.isDirectory() == f2.isDirectory()) {
                    return f1.getName().compareTo(f2.getName());
                }
                return f1.isDirectory() ? -1 : 1;
            }
        });
        TestSuite suite = new TestSuite(name == null ? getTestName(file) : name);
        for (File element : fileList) {
            try {
                Test test = getTest(element, getTestName(element));
                if (test != null) {
                    suite.addTest(test);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return suite;
    }

    protected Test createDDTTest(File file) throws IOException {
        return new MarathonDDTestSuite(file, acceptChecklist, console);
    }

    protected Test createTest(File file, String name) throws IOException {
        MarathonTestCase marathonTestCase = new MarathonTestCase(file, acceptChecklist, console);
        if (name == null) {
            marathonTestCase.setFullName(getTestName(file));
        } else {
            marathonTestCase.setFullName(name);
        }
        return marathonTestCase;
    }

    private boolean isDDT(File file) throws IOException {
        try {
            DDTestRunner runner = new DDTestRunner(console, file);
            return runner.isDDT();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getTestName(File file) throws IOException {
        return createDotFormat(file);
    }

    private String createDotFormat(File file) throws IOException {
        String filename = file.getCanonicalPath();
        if (!filename.startsWith(sourcePath)) {
            throw new IOException("Test file not in test directory");
        }
        if (filename.equals(sourcePath)) {
            return "AllTests";
        }
        filename = filename.substring(sourcePath.length() + 1);
        filename = filename.replace(File.separatorChar, '.');
        if (file.isDirectory()) {
            return filename + ".AllTests";
        } else {
            return filename.substring(0, filename.length() - 3);
        }
    }

    public static String getHideFilePattern() {
        return hideFilePattern;
    }

    public static void setHideFilePattern(String hideFilePattern) {
        if (hideFilePattern == null) {
            TestCreator.hideFilePattern = "\\..* .*\\.class \\Q__init__.py\\E \\QExploratoryTests\\E";
        } else {
            TestCreator.hideFilePattern = hideFilePattern;
        }
        hiddenFPM = new FilePatternMatcher(TestCreator.hideFilePattern);
    }

    public void setAcceptChecklist(boolean acceptChecklist) {
        this.acceptChecklist = acceptChecklist;
    }

    public Test getAllTestsForGroups(GroupType type) {
        File[] files = type.dir().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(type.ext());
            }
        });

        TestSuite suite = new TestSuite(type.dockName());

        if (files != null) {
            for (File file : files) {
                try {
                    String name = file.getName();
                    suite.addTest(getGroup(type, name.substring(0, name.length() - type.ext().length())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return suite;
    }

    public IConsole getConsole() {
        return console;
    }
}
