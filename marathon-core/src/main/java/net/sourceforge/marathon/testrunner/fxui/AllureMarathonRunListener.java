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
package net.sourceforge.marathon.testrunner.fxui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import junit.framework.Test;
import net.sourceforge.marathon.api.TestAttributes;
import net.sourceforge.marathon.checklist.CheckList;
import net.sourceforge.marathon.junit.ScreenShotEntry;
import net.sourceforge.marathon.junit.MarathonAssertion;
import net.sourceforge.marathon.junit.MarathonTestCase;
import net.sourceforge.marathon.model.Group;
import net.sourceforge.marathon.model.Group.GroupType;
import net.sourceforge.marathon.resource.Project;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.SourceLine;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.config.AllureConfig;
import ru.yandex.qatools.allure.config.AllureModelUtils;
import ru.yandex.qatools.allure.events.ClearStepStorageEvent;
import ru.yandex.qatools.allure.events.MakeAttachmentEvent;
import ru.yandex.qatools.allure.events.TestCaseCanceledEvent;
import ru.yandex.qatools.allure.events.TestCaseFailureEvent;
import ru.yandex.qatools.allure.events.TestCaseFinishedEvent;
import ru.yandex.qatools.allure.events.TestCasePendingEvent;
import ru.yandex.qatools.allure.events.TestCaseStartedEvent;
import ru.yandex.qatools.allure.events.TestSuiteFinishedEvent;
import ru.yandex.qatools.allure.events.TestSuiteStartedEvent;
import ru.yandex.qatools.allure.model.DescriptionType;
import ru.yandex.qatools.allure.model.Label;
import ru.yandex.qatools.allure.model.LabelName;
import ru.yandex.qatools.allure.model.SeverityLevel;
import ru.yandex.qatools.allure.utils.AllureResultsUtils;
import ru.yandex.qatools.allure.utils.AnnotationManager;

/**
 * @author Dmitry Baev charlie@yandex-team.ru Date: 20.12.13
 */
public class AllureMarathonRunListener extends RunListener {

    public static final Logger LOGGER = Logger.getLogger(AllureMarathonRunListener.class.getName());

    private Allure lifecycle;;

    private final Map<String, String> suites = new HashMap<>();

    private List<Group> issues;
    private List<Group> features;
    private List<Group> stories;

    public AllureMarathonRunListener() {
        Field config;
        try {
            config = AllureResultsUtils.class.getDeclaredField("CONFIG");
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(config, config.getModifiers() & ~Modifier.FINAL);
            config.setAccessible(true);
            config.set(null, AllureConfig.newInstance());
            AllureResultsUtils.setResultsDirectory(null);
            Constructor<Allure> constructor = Allure.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            lifecycle = constructor.newInstance();
            issues = Group.getGroups(GroupType.ISSUE);
            features = Group.getGroups(GroupType.FEATURE);
            stories = Group.getGroups(GroupType.STORY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testSuiteStarted(Description description) {
        String uid = generateSuiteUid(getSuiteName(description));

        TestSuiteStartedEvent event = new TestSuiteStartedEvent(uid, getSuiteName(description));
        AnnotationManager am = new AnnotationManager(description.getAnnotations());

        am.update(event);

        event.withLabels(AllureModelUtils.createTestFrameworkLabel("Marathon"));

        getLifecycle().fire(event);
    }

    private String getSuiteName(Description description) {
        Test test = (Test) TestAttributes.get("test_object");
        if (test == null) {
            return "<ERROR GETTING TEST>";
        }
        if (test instanceof MarathonTestCase) {
            try {
                Path path = ((MarathonTestCase) test).getFile().toPath();
                Path testPath = Constants.getMarathonDirectory(Constants.PROP_TEST_DIR).toPath();
                if (!path.isAbsolute()) {
                    return "root";
                }
                Path relativePath = testPath.relativize(path);
                int nameCount = relativePath.getNameCount();
                StringBuilder sb = new StringBuilder("root");
                for (int i = 0; i < nameCount - 1; i++) {
                    sb.append("::").append(relativePath.getName(i));
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "My Own Test Cases";
    }

    @Override
    public void testStarted(Description description) {
        TestCaseStartedEvent event = new TestCaseStartedEvent(getSuiteUid(description), getTestName());

        SeverityLevel severityLevel = SeverityLevel.NORMAL;
        Properties testProperties = getTestProperties();
        String severity = testProperties.getProperty("severity");
        if (severity != null) {
            severityLevel = SeverityLevel.fromValue(severity);
        }
        event.getLabels().add(AllureModelUtils.createSeverityLabel(severityLevel));

        String desc = testProperties.getProperty("description");
        if (desc != null) {
            String dType = "text";
            if (desc.startsWith("html:")) {
                dType = "html";
                desc = desc.substring(5);
            } else if (desc.startsWith("markdown:")) {
                dType = "markdown";
                desc = desc.substring(9);
            }
            event.setDescription(
                    new ru.yandex.qatools.allure.model.Description().withType(DescriptionType.fromValue(dType)).withValue(desc));
        }

        String id = testProperties.getProperty("id");
        if (id != null) {
            event.getLabels().add(AllureModelUtils.createTestLabel(id));
        }

        Path testPath = Paths.get(testProperties.getProperty("path", ""));
        addGroups(issues, event.getLabels(), testPath, LabelName.ISSUE);
        addGroups(features, event.getLabels(), testPath, LabelName.FEATURE);
        addGroups(stories, event.getLabels(), testPath, LabelName.STORY);
        getLifecycle().fire(event);
    }

    private void addGroups(List<Group> groups, List<Label> labels, Path testPath, LabelName labelName) {
        List<Group> matched = groups.stream().filter((g) -> g.hasTest(testPath)).collect(Collectors.toList());
        for (Group group : matched) {
            labels.add(AllureModelUtils.createLabel(labelName, group.getName()));
        }
    }

    @Override
    public void testFailure(Failure failure) {
        if (failure.getDescription().isTest()) {
            fireTestCaseFailure(failure.getException());
        } else {
            startFakeTestCase(failure.getDescription());
            fireTestCaseFailure(failure.getException());
            finishFakeTestCase();
        }
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        testFailure(failure);
    }

    @Override
    public void testIgnored(Description description) {
        startFakeTestCase(description);
        getLifecycle().fire(new TestCasePendingEvent().withMessage(getIgnoredMessage(description)));
        finishFakeTestCase();
    }

    @Override
    public void testFinished(Description description) {
        Test test = (Test) TestAttributes.get("test_object");
        if (test != null && test instanceof MarathonTestCase) {
            StringBuilder failed = new StringBuilder();
            List<CheckList> checklists = ((MarathonTestCase) test).getChecklists();
            for (CheckList checkList : checklists) {
                String name = checkList.getName();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                checkList.saveHTML(out);
                getLifecycle().fire(new MakeAttachmentEvent(out.toByteArray(), name, "text/html"));
                if (checkList.getCaptureFile() != null) {
                    File captureFile = new File(System.getProperty(Constants.PROP_IMAGE_CAPTURE_DIR),
                            "ext-" + checkList.getCaptureFile());
                    try {
                        getLifecycle().fire(new MakeAttachmentEvent(Files.readAllBytes(captureFile.toPath()),
                                name + "(ScreenCapture)", "image/png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if ("Fail".equals(checkList.getStatus())) {
                    failed.append(checkList.getName()).append(", ");
                }
            }
            if (failed.length() > 0) {
                failed.setLength(failed.length() - 2);
                net.sourceforge.marathon.runtime.api.Failure[] failures = new net.sourceforge.marathon.runtime.api.Failure[] {
                        null };
                failures[0] = new net.sourceforge.marathon.runtime.api.Failure("Failed Checklists: " + failed.toString(),
                        new SourceLine[0], null);
                MarathonAssertion assertion = new MarathonAssertion(failures, ((MarathonTestCase) test).getName());
                testAssumptionFailure(new Failure(description, assertion));
            }
            List<ScreenShotEntry> screenshots = ((MarathonTestCase) test).getScreenshots();
            for (ScreenShotEntry entry : screenshots) {
                entry.fire(getLifecycle());
            }
        }
        getLifecycle().fire(new TestCaseFinishedEvent());
    }

    public void testSuiteFinished(String uid) {
        getLifecycle().fire(new TestSuiteFinishedEvent(uid));
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description);
    }

    @Override
    public void testRunFinished(Result result) {
        for (String uid : getSuites().values()) {
            testSuiteFinished(uid);
        }
    }

    public String generateSuiteUid(String suiteName) {
        String uid = UUID.randomUUID().toString();
        synchronized (getSuites()) {
            getSuites().put(suiteName, uid);
        }
        return uid;
    }

    public String getSuiteUid(Description description) {
        String suiteName = getSuiteName(description);
        if (!getSuites().containsKey(suiteName)) {
            Description suiteDescription = Description.createSuiteDescription(description.getTestClass());
            testSuiteStarted(suiteDescription);
        }
        return getSuites().get(suiteName);
    }

    public String getIgnoredMessage(Description description) {
        Ignore ignore = description.getAnnotation(Ignore.class);
        return ignore == null || ignore.value().isEmpty() ? "Test ignored (without reason)!" : ignore.value();
    }

    public void startFakeTestCase(Description description) {
        String uid = getSuiteUid(description);

        String name = description.isTest() ? getTestName() : getSuiteName(description);
        TestCaseStartedEvent event = new TestCaseStartedEvent(uid, name);
        AnnotationManager am = new AnnotationManager(description.getAnnotations());
        am.update(event);

        fireClearStepStorage();
        getLifecycle().fire(event);
    }

    private String getTestName() {
        Properties testProperties = getTestProperties();
        return testProperties.getProperty("name");
    }

    public Properties getTestProperties() {
        Properties testProperties = new Properties();
        Test test = (Test) TestAttributes.get("test_object");
        if (test != null && test instanceof MarathonTestCase) {
            File file = ((MarathonTestCase) test).getFile();
            testProperties = Project.getTestProperties(file);
            testProperties.put("path", file.toPath().toAbsolutePath().toString());
        } else {
            testProperties = new Properties();
            testProperties.put("name", "<UNKNOWN TEST - SHOULD NOT HAPPEN>");
        }
        return testProperties;
    }

    public void finishFakeTestCase() {
        getLifecycle().fire(new TestCaseFinishedEvent());
    }

    public void fireTestCaseFailure(Throwable throwable) {
        if (throwable instanceof AssumptionViolatedException) {
            getLifecycle().fire(new TestCaseCanceledEvent().withThrowable(throwable));
        } else {
            getLifecycle().fire(new TestCaseFailureEvent().withThrowable(throwable));
        }
    }

    public void fireClearStepStorage() {
        getLifecycle().fire(new ClearStepStorageEvent());
    }

    public Allure getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Allure lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Map<String, String> getSuites() {
        return suites;
    }
}
