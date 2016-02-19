package net.sourceforge.marathon.runtime;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import net.sourceforge.marathon.runtime.api.IRuntimeLauncherModel;

public interface IWebDriverRuntimeLauncherModel extends IRuntimeLauncherModel {

    public abstract IWebdriverProxy createDriver(Map<String, Object> props, int recordingPort, OutputStream outputStream);

    public abstract URL getProfileAsURL(Map<String, Object> props, int recordingPort, OutputStream outputStream) throws URISyntaxException, IOException;

    public abstract boolean needReplaceEnviron();

    public abstract boolean isWebStart();

    public abstract boolean isApplet();

}
