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
package net.sourceforge.marathon.junit.textui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.internal.TextListener;
import org.junit.runner.Result;

import junit.framework.Test;
import net.sourceforge.marathon.ArgumentProcessor;
import net.sourceforge.marathon.junit.MarathonTestCase;
import net.sourceforge.marathon.junit.MarathonTestRunner;
import net.sourceforge.marathon.junit.StdOutConsole;
import net.sourceforge.marathon.junit.TestCreator;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.testrunner.fxui.AllureMarathonRunListener;
import net.sourceforge.marathon.util.AllureUtils;

public class TestRunner {
    ArgumentProcessor argProcessor;
    private Test currentTest;
    private TestCreator creator;
    private String runReportDir;

    public TestRunner() {
    }

    public Result doRun(Test suite, boolean wait) {
        MarathonTestRunner runner = new MarathonTestRunner();
        runReportDir = argProcessor.getReportDir();
        String resultsDir = new File(runReportDir, "results").getAbsolutePath();
        if (runReportDir != null) {
            System.setProperty(Constants.PROP_REPORT_DIR, runReportDir);
            System.setProperty(Constants.PROP_IMAGE_CAPTURE_DIR, runReportDir);
            System.setProperty("allure.results.directory", resultsDir);
            runner.addListener(new AllureMarathonRunListener());
        }
        runner.addListener(new TextListener(System.out));
        Result result = runner.run(suite);
        MarathonTestCase.reset();
        if (runReportDir != null) {
            AllureUtils.launchAllure(resultsDir, new File(runReportDir, "reports").getAbsolutePath());
        }
        return result;
    }

    public Result runTests(ArgumentProcessor argProcessor) throws Exception {
        this.argProcessor = argProcessor;
        List<String> tests = this.argProcessor.getTests();
        try {
            creator = createTestCreator();
            currentTest = creator.getTest(tests);
            return doRun(currentTest, false);
        } catch (Exception e) {
            throw new Exception("Could not create test suite for argument: " + tests, e);
        }
    }

    protected TestCreator createTestCreator() throws IOException {
        return new TestCreator(this.argProcessor.getAcceptChecklists(), new StdOutConsole());
    }

}
