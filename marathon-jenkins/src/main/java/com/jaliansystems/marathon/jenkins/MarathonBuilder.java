package com.jaliansystems.marathon.jenkins;

import java.io.IOException;

import javax.servlet.ServletException;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BatchFile;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tasks.Shell;
import hudson.util.FormValidation;

public class MarathonBuilder extends Builder {

    private final String marathonHome;
    private final String mProjectDir;
    private final String testCases;

    private static String OS = System.getProperty("os.name").toLowerCase();

    @DataBoundConstructor public MarathonBuilder(String marathonHome, String mProjectDir, String testCases) {
        this.marathonHome = marathonHome;
        this.mProjectDir = mProjectDir;
        this.testCases = testCases;
    }

    public String getMarathonHome() {
        return marathonHome;
    }

    public String getmProjectDir() {
        return mProjectDir;
    }

    public String getTestCases() {
        return testCases;
    }

    private String getCommand() {
        return "-batch -i " + mProjectDir + " " + testCases;
    }

    @Override public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        if (OS.indexOf("win") >= 0) {
            return new BatchFile(marathonHome + "marathon.bat " + getCommand()).perform(build, launcher, listener);
        } else if (OS.indexOf("mac") >= 0 || OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0) {
            return new Shell(marathonHome + "marathon " + getCommand()).perform(build, launcher, listener);
        } else {
            listener.getLogger().println("Your system is not supported");
            return false;
        }

    }

    @Symbol("greet") @Extension public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckMarathonHome(@QueryParameter String marathonHome, @AncestorInPath AbstractProject<?, ?> project)
                throws IOException, ServletException {
            if (marathonHome.length() == 0)
                return FormValidation.error("Please set Marathon Home.");
            return FormValidation.ok();
        }

        public FormValidation doCheckMProjectDir(@QueryParameter String mProjectDir, @AncestorInPath AbstractProject<?, ?> project)
                throws IOException, ServletException {
            if (mProjectDir.length() == 0)
                return FormValidation.error("Please set Marathon project directory.");
            return FormValidation.ok();
        }

        public FormValidation doCheckTestCases(@QueryParameter String testCases, @AncestorInPath AbstractProject<?, ?> project)
                throws IOException, ServletException {
            if (testCases.length() == 0)
                return FormValidation.warning("Please enter test case to run.");
            return FormValidation.ok();
        }

        @Override public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override public String getDisplayName() {
            return "Marathon test runner";
        }
    }

}
