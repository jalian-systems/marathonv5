package net.sourceforge.marathon.runtime;

import java.util.Map;

import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchType;
import net.sourceforge.marathon.runtime.api.Constants.MarathonMode;

public class FXRuntimeLauncherModel extends RuntimeLauncherModel {

    @Override public JavaProfile createProfile(Map<String, Object> props, MarathonMode mode) {
        return super.createProfile(props, mode).setLaunchType(LaunchType.FX_APPLICATION);
    }
}
