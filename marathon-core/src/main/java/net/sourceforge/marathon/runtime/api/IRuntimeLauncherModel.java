package net.sourceforge.marathon.runtime.api;

import java.util.List;
import java.util.Properties;

public interface IRuntimeLauncherModel extends ISubpanelProvider {

    public List<String> getPropertyKeys();

    public IRuntimeFactory getRuntimeFactory();

    public ITestLauncher createLauncher(Properties props);

    public String getFramework();

}
