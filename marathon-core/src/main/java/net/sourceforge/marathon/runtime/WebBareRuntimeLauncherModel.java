package net.sourceforge.marathon.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IMarathonRuntime;
import net.sourceforge.marathon.runtime.api.IRuntimeFactory;
import net.sourceforge.marathon.runtime.api.IRuntimeLauncherModel;
import net.sourceforge.marathon.runtime.api.ITestLauncher;
import net.sourceforge.marathon.runtime.fx.api.ISubPropertiesLayout;

public class WebBareRuntimeLauncherModel implements IRuntimeLauncherModel {

    public static final Logger LOGGER = Logger.getLogger(WebBareRuntimeLauncherModel.class.getName());

    @Override
    public ISubPropertiesLayout[] getSublayouts(ModalDialog<?> parent) {
        return new ISubPropertiesLayout[0];
    }

    @Override
    public List<String> getPropertyKeys() {
        return new ArrayList<String>();
    }

    @Override
    public IRuntimeFactory getRuntimeFactory() {
        return new IRuntimeFactory() {
            @Override
            public IMarathonRuntime createRuntime() {
                return new BareRuntime();
            }
        };
    }

    @Override
    public ITestLauncher createLauncher(Properties props) {
        return null;
    }

    @Override
    public String getFramework() {
        return Constants.FRAMEWORK_WEB;
    }

}
