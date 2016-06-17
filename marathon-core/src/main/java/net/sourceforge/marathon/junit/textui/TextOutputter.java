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
package net.sourceforge.marathon.junit.textui;

import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.runner.BaseTestRunner;

public class TextOutputter implements IOutputter {
    private static final String NL = "\n";
    private NumberFormat formatter;

    public TextOutputter() {
        super();
    }

    public void output(Writer writer, Test test, Map<Test, MarathonTestResult> testOutputMap) throws IOException {
        if (test instanceof TestSuite)
            printHeader(writer, (TestSuite) test, testOutputMap);
        printDetails(writer, test, testOutputMap);
    }

    private void printHeader(Writer writer, TestSuite testSuite, Map<Test, MarathonTestResult> testOutputMap) throws IOException {
        Collection<MarathonTestResult> values = testOutputMap.values();
        double totalDuration = 0.0;
        int totalFailures = 0;
        int totalErrors = 0;
        for (Iterator<MarathonTestResult> iter = values.iterator(); iter.hasNext();) {
            MarathonTestResult result = (MarathonTestResult) iter.next();
            totalDuration += result.getDuration();
            if (result.getStatus() == MarathonTestResult.STATUS_FAILURE)
                totalFailures++;
            if (result.getStatus() == MarathonTestResult.STATUS_ERROR)
                totalErrors++;
        }
        formatter = NumberFormat.getInstance();
        String durationStr = formatter.format(totalDuration);
        StringBuffer buffer = new StringBuffer();
        buffer.append("TestSuite: ").append(testSuite.getName());
        buffer.append(NL);
        buffer.append("Tests run: ").append(testSuite.countTestCases());
        buffer.append(",  Failures: ").append(totalFailures);
        buffer.append(",  Errors: ").append(totalErrors);
        buffer.append(",  Time elapsed: ").append(durationStr).append(" seconds");
        buffer.append(NL).append(NL);
        writer.write(buffer.toString());
    }

    private void printDetails(Writer writer, Test test, Map<Test, MarathonTestResult> testOutputMap) throws IOException {
        if (test instanceof TestSuite) {
            Enumeration<Test> testsEnum = ((TestSuite) test).tests();
            while (testsEnum.hasMoreElements())
                printDetails(writer, (Test) testsEnum.nextElement(), testOutputMap);
        } else {
            if (test != null) {
                MarathonTestResult result = (MarathonTestResult) testOutputMap.get(test);
                writeResultText(writer, result);
            }
        }
    }

    private void writeResultText(Writer writer, MarathonTestResult result) throws IOException {
        writer.write("Testcase: " + result.getTestName());
        writer.write("   " + result.getStatusDescription() + NL);
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            writer.write(throwable.getMessage() + NL);
            writer.write(BaseTestRunner.getFilteredTrace(throwable));
        }
        writer.write(" took " + formatter.format(result.getDuration()) + " seconds" + NL + NL);
    }
}
