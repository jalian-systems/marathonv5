package net.sourceforge.marathon.runtime;

import net.sourceforge.marathon.runtime.api.IMarathonRuntime;
import net.sourceforge.marathon.runtime.api.IRuntimeFactory;

public class WebDriverRuntimeFactory implements IRuntimeFactory {

    private IWebDriverRuntimeLauncherModel launcherModel;

    public WebDriverRuntimeFactory(IWebDriverRuntimeLauncherModel launcherModel) {
        this.launcherModel = launcherModel;
    }

    @Override public IMarathonRuntime createRuntime() {
        return new WebDriverRuntime(launcherModel);
    }

}
