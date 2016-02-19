package net.sourceforge.marathon.runtime;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openqa.selenium.remote.DesiredCapabilities;

import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.runtime.api.Constants.MarathonMode;
import net.sourceforge.marathon.runtime.api.IRuntimeLauncherModel;
import net.sourceforge.marathon.runtime.api.ITestLauncher;
import net.sourceforge.marathon.runtime.api.RuntimeLogger;
import net.sourceforge.marathon.runtime.api.Wait;

public abstract class AbstractJavaDriverRuntimeLauncherModel implements IJavaDriverRuntimeLauncherModel, IRuntimeLauncherModel {
    public ITestLauncher createLauncher(Properties props) {
        Map<String, Object> ps = new HashMap<String, Object>();
        Enumeration<Object> ks = props.keys();
        while (ks.hasMoreElements()) {
            String object = (String) ks.nextElement();
            ps.put(object, props.getProperty(object));
        }
        return new TestLauncher(this, ps);
    }

    @Override public IWebdriverProxy createDriver(Map<String, Object> props, int recordingPort, OutputStream outputStream) {
        final JavaProfile profile = createProfile(props, recordingPort > 0 ? MarathonMode.RECORDING : MarathonMode.PLAYING);
        profile.setRecordingPort(recordingPort);
        profile.copyOutputTo(outputStream);
        DesiredCapabilities caps = new DesiredCapabilities();
        boolean nativeEvents = false;
        if (props.containsKey("nativeEvents"))
            nativeEvents = ((Boolean) props.get("nativeEvents")).booleanValue();
        caps.setCapability("nativeEvents", nativeEvents);
        JavaDriver driver = new JavaDriver(profile, caps, caps);
        RuntimeLogger.getRuntimeLogger().info("WebDriverRuntime", "Launching application", "Launching: " + driver);
        new Wait("The Server did not start") {
            @Override public boolean until() {
                try {
                    Socket s = new Socket("localhost", profile.getPort());
                    s.close();
                    return true;
                } catch (UnknownHostException e) {
                } catch (IOException e) {
                }
                return false;
            }
        };
        return new JavaWebDriverProxy(profile, driver);
    }

    @Override public URL getProfileAsURL(Map<String, Object> props, int recordingPort, OutputStream outputStream)
            throws URISyntaxException, IOException {
        final JavaProfile profile = createProfile(props, recordingPort > 0 ? MarathonMode.RECORDING : MarathonMode.PLAYING);
        if (recordingPort != -1)
            throw new RuntimeException("createLauncher: illegal operation(recording) for grid launcher");
        profile.setRecordingPort(recordingPort);
        profile.copyOutputTo(outputStream);
        return profile.asURL();
    }
    
    @Override public boolean needReplaceEnviron() {
        return true;
    }
    
    @Override public boolean isWebStart() {
        return false;
    }
    
    @Override public boolean isApplet() {
        return false;
    }
}
