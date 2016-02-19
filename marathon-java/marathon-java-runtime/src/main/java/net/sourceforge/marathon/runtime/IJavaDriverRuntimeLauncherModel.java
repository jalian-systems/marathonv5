package net.sourceforge.marathon.runtime;

import java.util.Map;

import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.runtime.api.Constants.MarathonMode;

public interface IJavaDriverRuntimeLauncherModel extends IWebDriverRuntimeLauncherModel {

    JavaProfile createProfile(Map<String, Object> props, MarathonMode mode);

}
