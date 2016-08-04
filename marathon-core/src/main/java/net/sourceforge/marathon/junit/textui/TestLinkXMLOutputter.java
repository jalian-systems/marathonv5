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
import java.io.Writer;
import java.util.Enumeration;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.runner.BaseTestRunner;
import net.sourceforge.marathon.runtime.api.Constants;

public class TestLinkXMLOutputter implements IOutputter {
    public TestLinkXMLOutputter() {
        super();
    }

    @Override public void output(Writer writer, Test testSuite, Map<Test, MarathonTestResult> testOutputMap) {
        try {
            writer.write("<?xml version=\"1.0\"  encoding=\"UTF-8\"?>\n");
            writer.write("<results>\n");
            String reportDir = new File(System.getProperty(Constants.PROP_REPORT_DIR)).getName();
            writer.write("<!-- Project name: '" + System.getProperty(Constants.PROP_PROJECT_NAME, "") + "' - " + "Report dir: '"
                    + reportDir + "' - START -->\n");
            writeTestsuite("", writer, testSuite, testOutputMap);
            writer.write("<!-- Project name: '" + System.getProperty(Constants.PROP_PROJECT_NAME, "") + "' - " + "Report dir: '"
                    + reportDir + "' - END -->\n");
            writer.write("</results>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeTestsuite(String indent, Writer writer, Test test, Map<Test, MarathonTestResult> testOutputMap)
            throws IOException {
        if (test instanceof TestSuite) {
            TestSuite suite = (TestSuite) test;
            writer.write(indent + "<!-- Testsuite name: '" + suite.getName() + "' - START -->\n");
            Enumeration<Test> testsEnum = suite.tests();
            while (testsEnum.hasMoreElements()) {
                writeTestsuite(indent + "  ", writer, testsEnum.nextElement(), testOutputMap);
            }
            writer.write(indent + "<!-- Testsuite name: '" + suite.getName() + "' - END -->\n");
        } else {
            MarathonTestResult result = testOutputMap.get(test);
            writeTestCase(indent, writer, result, test);
        }
    }

    private void writeTestCase(String indent, Writer writer, MarathonTestResult result, Test test) throws IOException {
        writer.write(indent + "<testcase external_id=\"" + result.getTestName() + "\" >\n");
        writeTestCaseResult(indent, writer, result, test);
        writer.write(indent + "</testcase>\n");
    }

    private void writeTestCaseResult(String indent, Writer writer, MarathonTestResult result, Test test) throws IOException {
        String testLinkResult = "n";
        String testLinkNotes = "";
        int status = MarathonTestResult.STATUS_NONE;
        if (result != null) {
            status = result.getStatus();
            if (status == MarathonTestResult.STATUS_PASS) {
                testLinkResult = "p";
            } else {
                if (status == MarathonTestResult.STATUS_FAILURE) {
                    testLinkResult = "f";
                } else if (status == MarathonTestResult.STATUS_ERROR) {
                    testLinkResult = "b";
                }

                if (status != MarathonTestResult.STATUS_PASS) {
                    String stackTrace = " ";
                    Throwable throwable = result.getThrowable();
                    if (throwable != null) {
                        stackTrace = BaseTestRunner.getFilteredTrace(throwable);
                    }
                    testLinkNotes = "<![CDATA[" + stackTrace + "]]>";
                }
            }
        }
        writer.write(indent + "<result>" + testLinkResult + "</result>\n");
        writer.write(indent + "<notes>" + testLinkNotes + "</notes>\n");
    }
}
