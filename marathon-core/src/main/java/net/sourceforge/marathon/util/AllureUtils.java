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
package net.sourceforge.marathon.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.openqa.selenium.os.CommandLine;

import net.sourceforge.marathon.display.WaitMessageDialog;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.testrunner.fxui.TestRunner;
import ru.yandex.qatools.allure.AllureMain;

public class AllureUtils {

    public static final Logger LOGGER = Logger.getLogger(AllureUtils.class.getName());

    public static void launchAllure(String... args) {
        launchAllure(true, args);
    }

    public static void launchAllure(boolean showDialog, String... args) {
        if (showDialog)
            WaitMessageDialog.setVisible(true, "Generating reports");
        List<String> vmArgs = getVMArgs();
        Iterator<String> iterator = vmArgs.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.contains("-javaagent") || next.contains("-D")) {
                if (!next.contains("-Dallure")) {
                    iterator.remove();
                }
            }
        }
        vmArgs.add("-classpath");
        vmArgs.add(System.getProperty("java.class.path"));
        String property = System.getProperty(Constants.PROP_TMS_PATTERN);
        if (property != null && !"".equals(property)) {
            vmArgs.add("-D" + Constants.PROP_TMS_PATTERN + "=" + property);
        }
        property = System.getProperty(Constants.PROP_ISSUE_PATTERN);
        if (property != null && !"".equals(property)) {
            vmArgs.add("-D" + Constants.PROP_ISSUE_PATTERN + "=" + property);
        }
        ArrayList<String> newArgs = new ArrayList<String>();
        newArgs.add(getJavaCommand());
        newArgs.addAll(vmArgs);
        newArgs.add(AllureMain.class.getName());
        newArgs.addAll(new ArrayList<String>(Arrays.asList(args)));
        CommandLine command = new CommandLine(newArgs.toArray(new String[newArgs.size()]));
        command.copyOutputTo(System.out);
        Logger.getLogger(TestRunner.class.getName()).info("Launching: " + command);
        command.execute();
        if (showDialog)
            WaitMessageDialog.setVisible(false);
    }

    private static String getJavaCommand() {
        if (net.sourceforge.marathon.javaagent.Platform.getCurrent().is(net.sourceforge.marathon.javaagent.Platform.WINDOWS)) {
            return System.getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe";
        }
        return System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
    }

    private static List<String> getVMArgs() {
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        return new ArrayList<String>(runtimeMxBean.getInputArguments());
    }

}
