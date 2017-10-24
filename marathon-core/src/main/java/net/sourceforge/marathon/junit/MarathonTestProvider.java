/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * All Rights Reserved.
 ******************************************************************************/
package net.sourceforge.marathon.junit;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.io.Files;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.marathon.api.TestAttributes;
import net.sourceforge.marathon.runtime.api.Constants;
import ru.yandex.qatools.allure.annotations.Attachment;

@RunWith(Parameterized.class) public class MarathonTestProvider {

    public static final Logger LOGGER = Logger.getLogger(MarathonTestProvider.class.getName());

    private static Test suite;

    @Parameters(name = "{1}") public static Iterable<Object[]> data() {
        ArrayList<Object[]> tests = new ArrayList<Object[]>();
        collectTests(suite, tests);
        return tests;
    }

    public static void setSuite(Test suite) {
        MarathonTestProvider.suite = suite;
    }

    private static void collectTests(Test test, ArrayList<Object[]> tests) {
        if (test instanceof TestSuite) {
            TestSuite suite = (TestSuite) test;
            Enumeration<Test> tests2 = suite.tests();
            while (tests2.hasMoreElements()) {
                Test test2 = tests2.nextElement();
                collectTests(test2, tests);
            }
        } else {
            String name;
            if (test instanceof IHasFullname) {
                name = ((IHasFullname) test).getFullName();
            } else {
                name = ((TestCase) test).getName();
            }
            tests.add(new Object[] { test, name });
        }
    }

    public MarathonTestProvider(Test test, String name) {
        fTest = test;
        fName = name;
        TestAttributes.put("test_object", test);
    }

    public void captureScreens() {
        String captureDir = System.getProperty(Constants.PROP_IMAGE_CAPTURE_DIR);
        if (captureDir == null) {
            return;
        }
        File dir = new File(captureDir);
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override public boolean accept(File dir, String name) {
                return name.matches(Pattern.quote(fName) + "-error[0-9]*.png");
            }
        });
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            try {
                captureScreen(Files.toByteArray(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Attachment(value = "screencap", type = "image/png") public byte[] captureScreen(byte[] bs) {
        return bs;
    }

    private Test fTest;
    private String fName;

    @org.junit.Test public void test() throws Throwable {
        try {
            ((MarathonTestCase) fTest).initialize();
            ((MarathonTestCase) fTest).runTest();
        } finally {
            captureScreens();
        }
    }

}
