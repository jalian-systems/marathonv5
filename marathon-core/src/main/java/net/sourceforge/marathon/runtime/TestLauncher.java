package net.sourceforge.marathon.runtime;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

import javax.swing.JOptionPane;

import net.sourceforge.marathon.runtime.api.ITestLauncher;
import net.sourceforge.marathon.util.Blurb;

import org.openqa.selenium.WebDriver;

public class TestLauncher implements ITestLauncher {

    private WebDriver driver;
    private IWebDriverRuntimeLauncherModel launcherModel;
    private Map<String, Object> ps;
    private PrintStream writerOutputStream;
    private PrintStream messagePS;

    public TestLauncher(IWebDriverRuntimeLauncherModel launcherModel, Map<String, Object> ps) {
        this.launcherModel = launcherModel;
        this.ps = ps;
    }

    @Override public void destroy() {
        if (driver != null)
            driver.quit();
    }

    @Override public void copyOutputTo(OutputStream writerOutputStream) {
        this.writerOutputStream = new PrintStream(writerOutputStream);
    }

    @Override public void setMessageArea(OutputStream writerOutputStream) {
        messagePS = new PrintStream(writerOutputStream);
    }

    @Override public int start() {
        int selection = JOptionPane.OK_OPTION;
        if (launcherModel.isWebStart() || launcherModel.isApplet()) {
            Blurb blurb = new Blurb("/webstartlauncher", "Using WebStart", true) {
            };
            selection = blurb.getSelection();
            if (selection != JOptionPane.OK_OPTION)
                return selection;
        }
        try {
            IWebdriverProxy proxy = launcherModel.createDriver(ps, -1, writerOutputStream);
            driver = proxy.getDriver();
        } catch (Throwable t) {
            t.printStackTrace(writerOutputStream);
            writerOutputStream.flush();
            messagePS
                    .println("If you are using webstart/applet launchers this can happen if the policy files are not set properly");
            messagePS.println(
                    "Add the following lines to your policy file in <javahome>/lib/security/javaws.policy (see the above output for the java.home property)");
            messagePS.println(
                    "Add the following lines to your policy file in <javahome>/lib/security/java.policy (see the above output for the java.home property)");
            messagePS.println("grant codeBase \"${marathon.agent}\" {");
            messagePS.println("    permission java.security.AllPermission;");
            messagePS.println("};");
            messagePS.println("grant codeBase \"${marathon.recorder}\" {");
            messagePS.println("    permission java.security.AllPermission;");
            messagePS.println("};");
            messagePS.flush();
        }
        return selection;
    }

    @Override public String toString() {
        if (driver != null)
            return driver.toString();
        return super.toString();
    }
}
