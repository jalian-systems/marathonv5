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
package net.sourceforge.marathon.runtime;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchMode;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.Constants.MarathonMode;
import net.sourceforge.marathon.runtime.api.IRuntimeFactory;
import net.sourceforge.marathon.runtime.api.IRuntimeLauncherModel;
import net.sourceforge.marathon.runtime.fx.api.ISubPropertiesLayout;

public class RuntimeLauncherModel extends AbstractJavaDriverRuntimeLauncherModel
        implements IRuntimeLauncherModel, IJavaDriverRuntimeLauncherModel {

    @Override public List<String> getPropertyKeys() {
        return Arrays.asList(Constants.PROP_APPLICATION_MAINCLASS, Constants.PROP_APPLICATION_ARGUMENTS,
                Constants.PROP_APPLICATION_VM_ARGUMENTS, Constants.PROP_APPLICATION_JAVA_HOME,
                Constants.PROP_APPLICATION_WORKING_DIR, Constants.PROP_APPLICATION_PATH);
    }

    @Override public IRuntimeFactory getRuntimeFactory() {
        return new WebDriverRuntimeFactory(this);
    }

    @Override public JavaProfile createProfile(Map<String, Object> props, MarathonMode mode) {
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_COMMAND_LINE);
        String javaHome = (String) props.get(Constants.PROP_APPLICATION_JAVA_HOME);
        if (javaHome != null && !javaHome.equals("")) {
            profile.setJavaHome(javaHome);
        }
        System.setProperty("marathon.mode", mode == MarathonMode.RECORDING ? "record" : "other");
        String vmArgs = (String) props.get(Constants.PROP_APPLICATION_VM_ARGUMENTS);
        if (vmArgs != null && !vmArgs.equals("")) {
            ArgumentProcessor p = new ArgumentProcessor(vmArgs);
            List<String> args = p.parseArguments();
            profile.addVMArgument(args.toArray(new String[args.size()]));
        }
        Set<String> keySet = props.keySet();
        for (String key : keySet) {
            if (key.startsWith(Constants.PROP_PROPPREFIX)) {
                int prefixLength = Constants.PROP_PROPPREFIX.length();
                profile.addVMArgument("-D" + key.substring(prefixLength) + "=" + props.get(key).toString());
            }
        }
        String classPath = (String) props.get(Constants.PROP_APPLICATION_PATH);
        if (classPath != null && !classPath.equals("")) {
            String[] cp = classPath.split(";");
            profile.addClassPath(cp);
        }
        String mainClass = (String) props.get(Constants.PROP_APPLICATION_MAINCLASS);
        if (mainClass == null || mainClass.equals("")) {
            throw new RuntimeException("Main Class Not Given");
        }
        profile.setMainClass(mainClass);
        String args = (String) props.get(Constants.PROP_APPLICATION_ARGUMENTS);
        if (!args.equals("")) {
            ArgumentProcessor p = new ArgumentProcessor(args);
            List<String> appArgs = p.parseArguments();
            profile.addApplicationArguments(appArgs.toArray(new String[appArgs.size()]));
        }
        String workingDir = (String) props.get(Constants.PROP_APPLICATION_WORKING_DIR);
        if (workingDir != null && !"".equals(workingDir)) {
            profile.setWorkingDirectory(workingDir);
        }
        return profile;
    }

    @Override public ISubPropertiesLayout[] getSublayouts(ModalDialog<?> parent) {
        return new ISubPropertiesLayout[] { new MainLayout(parent), new ClassPathLayout(parent) };
    }

}
