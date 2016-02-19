package net.sourceforge.marathon.junit.textui;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.textui.ResultPrinter;
import net.sourceforge.marathon.ArgumentProcessor;
import net.sourceforge.marathon.junit.MarathonResultReporter;
import net.sourceforge.marathon.junit.MarathonTestCase;
import net.sourceforge.marathon.junit.StdOutConsole;
import net.sourceforge.marathon.junit.TestCreator;

public class TestRunner extends junit.textui.TestRunner {
    ArgumentProcessor argProcessor;
    private Test currentTest;
    private TestCreator creator;
    private MarathonResultReporter resultReporter;

    public TestRunner() {
    }

    public TestResult doRun(Test suite, boolean wait) {
        TestResult result = new MarathonJunitTestResult();
        TestResultPrinter printer = new TestResultPrinter(System.out);
        resultReporter = new MarathonResultReporter(currentTest);
        result.addListener(resultReporter);
        result.addListener(printer);
        result.addListener(TestRunner.this);
        long startTime = System.currentTimeMillis();
        runSuite(suite, result);
        long endTime = System.currentTimeMillis();
        long runTime = (endTime - startTime);
        String xmlFileName = argProcessor.getXmlFileName();
        if (xmlFileName != null) {
            resultReporter.generateReport(new XMLOutputter(), xmlFileName);
        }
        String textFileName = argProcessor.getTextFileName();
        ;
        if (textFileName != null) {
            resultReporter.generateReport(new TextOutputter(), textFileName);
        }
        String htmlFileName = argProcessor.getHtmlFileName();
        if (htmlFileName != null) {
            resultReporter.generateReport(new HTMLOutputter(), htmlFileName);
        }
        String testLinkXmlFileName = argProcessor.getTestLinkXmlFileName();
        if (testLinkXmlFileName != null) {
            resultReporter.generateReport(new TestLinkXMLOutputter(), testLinkXmlFileName);
        }
        printer.printDetails(result, runTime);
        pause(wait);
        return result;
    }

    protected void runSuite(Test suite, TestResult result) {
        suite.run(result);
        MarathonTestCase.reset();
    }

    public TestResult runTests(ArgumentProcessor argProcessor) throws Exception {
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

    private static final class TestResultPrinter extends ResultPrinter {
        public TestResultPrinter(PrintStream writer) {
            super(writer);
        }

        public void printDetails(TestResult result, long runTime) {
            printHeader(runTime);
            printErrors(result);
            printFailures(result);
            printFooter(result);
        }
    }

}
