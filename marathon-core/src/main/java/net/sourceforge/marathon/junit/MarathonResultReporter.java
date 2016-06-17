/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.junit;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import net.sourceforge.marathon.junit.textui.MarathonTestResult;
import net.sourceforge.marathon.junit.textui.IOutputter;

public class MarathonResultReporter implements TestListener {
    private Test test;
    private Map<Test, MarathonTestResult> testOutputMap;
    private long testStartTime;

    public MarathonResultReporter(Test test) {
        this.test = test;
        testOutputMap = new HashMap<Test, MarathonTestResult>();
    }

    public synchronized void startTest(Test test) {
        testOutputMap.put(test, new MarathonTestResult(test));
        testStartTime = System.currentTimeMillis();
    }

    public synchronized void endTest(Test test) {
        long testEndTime = System.currentTimeMillis();
        double duration = (testEndTime - testStartTime) / (1000.0);
        MarathonTestResult result = (MarathonTestResult) testOutputMap.get(test);
        if (result != null) {
            result.setDuration(duration);
        }
    }

    public synchronized void addFailure(Test test, AssertionFailedError error) {
        addDefect(test, error, MarathonTestResult.STATUS_FAILURE);
    }

    public synchronized void addError(Test test, Throwable throwable) {
        addDefect(test, throwable, MarathonTestResult.STATUS_ERROR);
    }

    private void addDefect(Test test, Throwable throwable, int status) {
        MarathonTestResult result = (MarathonTestResult) testOutputMap.get(test);
        if (result != null) {
            result.setStatus(status);
            result.setThrowable(throwable);
        }
    }

    public void generateReport(IOutputter outputter, String fileName) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(fileName);
            outputter.output(writer, test, testOutputMap);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        }
    }
}
