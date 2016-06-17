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
package net.sourceforge.marathon.testrunner.swingui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icons {
    private static final String pathToIcons = "net/sourceforge/marathon/testrunner/swingui/icons/";
    public static final Icon SUITES = new ImageIcon(Icons.class.getClassLoader().getResource(pathToIcons + "suites_toggle.gif"));
    public static final Icon JUNIT = new ImageIcon(Icons.class.getClassLoader().getResource(pathToIcons + "junit.gif"));
    public static final Icon ERROR = new ImageIcon(Icons.class.getClassLoader().getResource(pathToIcons + "error.gif"));
    public static final Icon FAILURE = new ImageIcon(Icons.class.getClassLoader().getResource(pathToIcons + "failure.gif"));
    public static final Icon FAILURES = new ImageIcon(Icons.class.getClassLoader().getResource(pathToIcons + "failures.gif"));
    public static final Icon HIERARCHY = new ImageIcon(Icons.class.getClassLoader().getResource(pathToIcons + "hierarchy.gif"));
    public static final Icon TRACE = new ImageIcon(Icons.class.getClassLoader().getResource(pathToIcons + "trace.gif"));

    // Tree Icons

    public static final Icon T_TEST = new ImageIcon(Icons.class.getClassLoader().getResource(pathToIcons + "tree/test.gif"));
    public static final Icon T_TESTRUN = new ImageIcon(Icons.class.getClassLoader().getResource(pathToIcons + "tree/testrun.gif"));
    public static final Icon T_TESTERROR = new ImageIcon(
            Icons.class.getClassLoader().getResource(pathToIcons + "tree/testerror.gif"));
    public static final Icon T_TESTFAIL = new ImageIcon(
            Icons.class.getClassLoader().getResource(pathToIcons + "tree/testfail.gif"));
    public static final Icon REPORT_DISABLED = new ImageIcon(
            Icons.class.getClassLoader().getResource(pathToIcons + "disabled/report.gif"));
    public static final Icon T_TESTOK = new ImageIcon(Icons.class.getClassLoader().getResource(pathToIcons + "tree/testok.gif"));
    public static final Icon T_TSUITE = new ImageIcon(Icons.class.getClassLoader().getResource(pathToIcons + "tree/tsuite.gif"));
    public static final Icon T_TSUITERUN = new ImageIcon(
            Icons.class.getClassLoader().getResource(pathToIcons + "tree/tsuiterun.gif"));
    public static final Icon T_TSUITEERROR = new ImageIcon(
            Icons.class.getClassLoader().getResource(pathToIcons + "tree/tsuiteerror.gif"));
    public static final Icon T_TSUITEFAIL = new ImageIcon(
            Icons.class.getClassLoader().getResource(pathToIcons + "tree/tsuitefail.gif"));
    public static final Icon T_TSUITEOK = new ImageIcon(
            Icons.class.getClassLoader().getResource(pathToIcons + "tree/tsuiteok.gif"));
}
