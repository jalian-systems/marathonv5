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
package net.sourceforge.marathon.javadriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.os.CommandLine;

public class JavaProfile {

    public static final Logger LOGGER = Logger.getLogger(JavaProfile.class.getName());

    private static final String PROP_HOME = "marathon.home";
    private static final String MARATHON_AGENT = "marathon.agent";
    private static final String MARATHON_RECORDER = "marathon.recorder";

    private static Map<String, File> jnlpFiles = new HashMap<String, File>();
    private static final File NULLFILE = new File("");

    public enum LaunchType {
        // @formatter:off
        SWING_APPLICATION("java"),
        FX_APPLICATION("javafx"),
        ;
        private String prefix;

        // @formatter:on

        LaunchType(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    public enum LaunchMode {
        // @formatter:off
        EMBEDDED("unittesting", "Unit Testing"),
        JAVA_COMMAND_LINE("javacommand", "Java Command Line", "vmargument", "classpath", "mainclass", "appargument"),
        JAVA_WEBSTART("webstart", "WebStart Application", "vmargument", "wsargument", "jnlppath", "jnlpnolocalcopy", "startwindowtitle"),
        COMMAND_LINE("commandline", "Command Line", "command", "appargument", "startwindowtitle", "vmargument"),
        JAVA_APPLET("applet", "Applet Application",  "vmargument", "appleturl", "startwindowtitle"),
        EXECUTABLE_JAR("executablejar", "Executable JAR", "executablejar", "appargument", "startwindowtitle", "vmargument"),
        ;
        // @formatter:on

        private String name;
        private String description;
        private List<String> validProperties = new ArrayList<String>();

        LaunchMode(String name, String description, String... validProperties) {
            this.name = name;
            this.description = description;
            for (String prop : validProperties) {
                this.validProperties.add(prop);
            }
        }

        public boolean isValidProperty(String property) {
            return validProperties.contains(property);
        }

        @Override public String toString() {
            return "Launchmode " + description;
        }

        public String getName() {
            return name;
        }
    }

    List<File> classPathEntries = new ArrayList<File>();
    private List<String> vmArguments = new ArrayList<String>();
    private List<String> wsArguments = new ArrayList<String>();
    private List<String> appArguments = new ArrayList<String>();
    private LaunchMode launchMode;
    private LaunchType launchType = LaunchType.SWING_APPLICATION;
    private String mainClass;
    private String jnlpPath;
    private int port;
    private String startWindowTitle;
    private String workingDirectory;
    private String vmCommand;
    private String command;
    private String appletURL;
    private static String dirOfMarathonJavaDriverJar;
    private boolean keepLog = false;

    static {
        dirOfMarathonJavaDriverJar = ClassPathHelper.getClassPath(JavaProfile.class.getName());
        File dir = new File(dirOfMarathonJavaDriverJar).getParentFile();
        if (dir.exists()) {
            dirOfMarathonJavaDriverJar = dir.getAbsolutePath();
        } else {
            dirOfMarathonJavaDriverJar = ".";
        }
    }

    public JavaProfile() {
        this(LaunchMode.EMBEDDED);
    }

    public JavaProfile(LaunchMode launchMode) {
        this.launchMode = launchMode;
        this.port = findPort();
    }

    public JavaProfile(URL url) throws URISyntaxException {
        parse(url);
        this.port = findPort();
    }

    private int findPort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } catch (IOException e1) {
            throw new WebDriverException("Could not allocate a port: " + e1.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public int getPort() {
        return port;
    }

    public CommandLine getCommandLine() {
        if (launchMode == LaunchMode.JAVA_COMMAND_LINE) {
            List<String> args = new ArrayList<String>();
            args.add(findJavaBinary());
            if (classPathEntries.size() > 0) {
                args.add("-cp");
                args.add(getClassPath());
            }
            args.add(mainClass);
            args.addAll(appArguments);
            CommandLine commandLine = new CommandLine(args.toArray(new String[args.size()]));
            commandLine.setEnvironmentVariable("JAVA_TOOL_OPTIONS", getToolOptions());
            if (javaHome != null) {
                commandLine.setEnvironmentVariable("JAVA_HOME", javaHome);
            }
            if (workingDirectory != null) {
                commandLine.setWorkingDirectory(workingDirectory);
            }
            if (outputStream != null) {
                commandLine.copyOutputTo(outputStream);
            }
            return commandLine;
        }
        if (launchMode == LaunchMode.JAVA_WEBSTART) {
            List<String> args = new ArrayList<String>();
            args.add(findJavaWSBinary());
            args.addAll(wsArguments);
            if (jnlpPath != null) {
                args.add(getLocalCopy(jnlpPath));
            } else {
                throw new WebDriverException("You must set either JNLP URL or File");
            }
            CommandLine commandLine = new CommandLine(args.toArray(new String[args.size()]));
            commandLine.setEnvironmentVariable("JAVA_TOOL_OPTIONS", getToolOptions());
            if (javaHome != null) {
                commandLine.setEnvironmentVariable("JAVA_HOME", javaHome);
            }
            if (workingDirectory != null) {
                commandLine.setWorkingDirectory(workingDirectory);
            }
            if (outputStream != null) {
                commandLine.copyOutputTo(outputStream);
            }
            return commandLine;
        }
        if (launchMode == LaunchMode.JAVA_APPLET) {
            List<String> args = new ArrayList<String>();
            args.add(findAppletViewerBinary());
            if (appletURL != null) {
                args.add(appletURL);
            }
            CommandLine commandLine = new CommandLine(args.toArray(new String[args.size()]));
            commandLine.setEnvironmentVariable("JAVA_TOOL_OPTIONS", getToolOptions());
            if (javaHome != null) {
                commandLine.setEnvironmentVariable("JAVA_HOME", javaHome);
            }
            if (workingDirectory != null) {
                commandLine.setWorkingDirectory(workingDirectory);
            }
            if (outputStream != null) {
                commandLine.copyOutputTo(outputStream);
            }
            return commandLine;
        }
        if (launchMode == LaunchMode.COMMAND_LINE) {
            List<String> args = new ArrayList<String>();
            args.add(command);
            args.addAll(appArguments);
            CommandLine commandLine = new CommandLine(args.toArray(new String[args.size()]));
            if (javaHome != null) {
                commandLine.setEnvironmentVariable("JAVA_HOME", javaHome);
            }
            commandLine.setEnvironmentVariable("JAVA_TOOL_OPTIONS", getToolOptions());
            if (workingDirectory != null) {
                commandLine.setWorkingDirectory(workingDirectory);
            }
            if (outputStream != null) {
                commandLine.copyOutputTo(outputStream);
            }
            return commandLine;
        }
        if (launchMode == LaunchMode.EXECUTABLE_JAR) {
            List<String> args = new ArrayList<String>();
            args.add(findJavaBinary());
            args.add("-jar");
            args.add(executableJar);
            args.addAll(appArguments);
            CommandLine commandLine = new CommandLine(args.toArray(new String[args.size()]));
            if (javaHome != null) {
                commandLine.setEnvironmentVariable("JAVA_HOME", javaHome);
            }
            commandLine.setEnvironmentVariable("JAVA_TOOL_OPTIONS", getToolOptions());
            if (workingDirectory != null) {
                commandLine.setWorkingDirectory(workingDirectory);
            }
            if (outputStream != null) {
                commandLine.copyOutputTo(outputStream);
            }
            return commandLine;
        }
        return null;
    }

    private String getToolOptions() {
        StringBuilder java_tool_options = new StringBuilder();
        java_tool_options.append("-DkeepLog=" + Boolean.toString(keepLog)).append(" ");
        java_tool_options.append("-Dmarathon.launch.mode=" + launchMode.getName()).append(" ");
        java_tool_options.append("-Dmarathon.mode=" + (recordingPort != -1 ? "recording" : "playing")).append(" ");
        if (startWindowTitle != null) {
            java_tool_options.append("-Dstart.window.title=\"" + startWindowTitle).append("\" ");
        }
        java_tool_options.append("-D" + MARATHON_AGENT + "=" + getAgentJarURL()).append(" ");
        java_tool_options.append("-javaagent:\"" + getAgentJar() + "\"=" + port).append(" ");
        if (recordingPort != -1) {
            java_tool_options.append("-D" + MARATHON_RECORDER + "=" + getRecorderJarURL()).append(" ");
            java_tool_options.append(" -javaagent:\"" + getRecorderJar() + "\"=" + recordingPort).append(" ");
        }
        addExtensions(java_tool_options);
        for (String vmArg : vmArguments) {
            java_tool_options.append("\"" + vmArg + "\"").append(" ");
        }
        if (System.getProperty("java.util.logging.config.file") != null) {
            java_tool_options
                    .append("-Djava.util.logging.config.file=\"" + System.getProperty("java.util.logging.config.file") + "\" ");
        }
        if (System.getProperty("marathon.project.dir") != null) {
            java_tool_options.append("-Dmarathon.project.dir=\"" + System.getProperty("marathon.project.dir") + "\" ");
        }
        java_tool_options.setLength(java_tool_options.length() - 1);
        if(java_tool_options.length() > 1023) {
            throw new RuntimeException("JAVA_TOOL_OPTIONS is more than 1023 bytes. Move marathon installation to a shorter path");
        }
        return java_tool_options.toString();
    }

    private String getRecorderJarURL() {
        return getURL(getRecorderJar());
    }

    private String getURL(String path) {
        try {
            return new File(path).toURI().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return path;
        }
    }

    private String getAgentJarURL() {
        return getURL(getAgentJar());
    }

    private void addExtensions(StringBuilder sb) {
        Collection<String> classes = FindResources.findClasses("MarathonExtension");
        for (String klass : classes) {
            String classPath = ClassPathHelper.getClassPath(klass);
            if (classPath != null) {
                sb.append("-javaagent:\"" + classPath + "\"").append(" ");
            }
        }
    }

    public String getAgentJar() {
        String prefix = launchType.getPrefix();
        if (System.getenv(MARATHON_AGENT + ".file") != null) {
            return System.getenv(MARATHON_AGENT + ".file");
        }
        if (System.getProperty(MARATHON_AGENT + ".file") != null) {
            return System.getProperty(MARATHON_AGENT + ".file");
        }
        String path = findFile(new String[] { ".", "marathon-" + prefix + "-agent", "../marathon-" + prefix + "-agent",
                "../../marathon/marathon-" + prefix + "/marathon-" + prefix + "-agent", System.getProperty(PROP_HOME, "."),
                dirOfMarathonJavaDriverJar, System.getenv("MARATHON_HOME") }, "marathon-" + prefix + "-agent.*.jar");
        if (path != null) {
            Logger.getLogger(JavaProfile.class.getName()).info("Using " + path + " for agent");
            return path;
        }
        throw new WebDriverException("Unable to find marathon-agent.jar. Set " + MARATHON_AGENT + ".file"
                + " environment variable to point to the jar file: " + new File(".").getAbsolutePath());
    }

    private static String findFile(String[] likelyPlaces, final String namePattern) {
        String path = null;
        for (String likelyPlace : likelyPlaces) {
            if (likelyPlace == null)
                continue;
            File[] f = new File(likelyPlace).listFiles(new FilenameFilter() {
                @Override public boolean accept(File dir, String name) {
                    return name.matches(namePattern);
                }
            });
            if (f != null && f.length == 1) {
                try {
                    path = f[0].getCanonicalPath();
                    break;
                } catch (IOException e) {
                }
            }
        }
        ;
        return path;
    }

    private String getClassPath() {
        StringBuilder sb = new StringBuilder();
        for (File f : classPathEntries) {
            sb.append(f.getAbsolutePath()).append(File.pathSeparator);
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public String findCommand(String command) {
        if (vmCommand != null) {
            return vmCommand;
        }
        if (javaHome != null) {
            File homeFolder = new File(javaHome);
            if (!homeFolder.exists() || !homeFolder.isDirectory()) {
                throw new WebDriverException(String.format("%s: No such directory", homeFolder));
            }
            File binFolder = new File(javaHome, "bin");
            if (!binFolder.exists() || !binFolder.isDirectory()) {
                throw new WebDriverException(String.format("%s: No bin directory found in home directory", binFolder));
            }
            File java = new File(binFolder, Platform.getCurrent().is(Platform.WINDOWS) ? command + ".exe" : command);
            if (!java.exists() || !java.isFile()) {
                throw new WebDriverException(String.format("%s: No such file", java));
            }
            return java.getAbsolutePath();
        }
        return command;
    }

    private String findJavaBinary() {
        return findCommand("java");
    }

    private String findJavaWSBinary() {
        if (vmCommand != null) {
            return vmCommand;
        }
        if (Platform.getCurrent().is(Platform.MAC)) {
            return "javaws";
        }
        return findCommand("javaws");
    }

    private String findAppletViewerBinary() {
        return findCommand("appletviewer");
    }

    public JavaProfile addClassPath(File... jarOrDirs) {
        checkValidProperty("classpath");
        for (File jarOrDir : jarOrDirs) {
            if (!jarOrDir.exists()) {
                throw new WebDriverException(String.format("But unable to locate the requested jar or folder: %s", jarOrDir));
            }
            classPathEntries.add(jarOrDir);
        }
        return this;
    }

    public JavaProfile addClassPath(String... jarOrDirs) {
        checkValidProperty("classpath");
        for (String s : jarOrDirs) {
            File jarOrDir = new File(s);
            if (!jarOrDir.exists() && !s.matches(".*%[^%]*%.*")) {
                throw new WebDriverException(String.format("But unable to locate the requested jar or folder: %s", jarOrDir));
            }
            classPathEntries.add(jarOrDir);
        }
        return this;
    }

    private void checkValidProperty(String property) {
        if (!launchMode.isValidProperty(property)) {
            throw new WebDriverException(property + " is not valid for " + launchMode);
        }
    }

    public JavaProfile addVMArgument(String... arg) {
        checkValidProperty("vmargument");
        for (String a : arg) {
            vmArguments.add(a);
        }
        return this;
    }

    public JavaProfile addWSArgument(String... arg) {
        checkValidProperty("wsargument");
        for (String a : arg) {
            wsArguments.add(a);
        }
        return this;
    }

    public JavaProfile addApplicationArguments(String... arg) {
        checkValidProperty("appargument");
        for (String a : arg) {
            appArguments.add(a);
        }
        return this;
    }

    public JavaProfile setMainClass(String mainClass) {
        checkValidProperty("mainclass");
        this.mainClass = mainClass;
        return this;
    }

    public JavaProfile setJNLPPath(String url) {
        checkValidProperty("jnlppath");
        this.jnlpPath = url;
        return this;
    }

    public JavaProfile setAppletURL(String url) {
        checkValidProperty("appleturl");
        this.appletURL = url;
        return this;
    }

    public JavaProfile setStartWindowTitle(String title) {
        checkValidProperty("startwindowtitle");
        this.startWindowTitle = title;
        return this;
    }

    public JavaProfile setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
        return this;
    }

    public JavaProfile setCommand(String c) {
        checkValidProperty("command");
        this.command = c;
        return this;
    }

    private int recordingPort = -1;

    private OutputStream outputStream;
    private String javaHome;
    private boolean nativeEvents;
    private String executableJar;
    private boolean jnlpNoLocalCopy;

    public String getRecorderJar() {
        if (System.getenv(MARATHON_RECORDER + ".file") != null) {
            return System.getenv(MARATHON_RECORDER + ".file");
        }
        if (System.getProperty(MARATHON_RECORDER + ".file") != null) {
            return System.getProperty(MARATHON_RECORDER + ".file");
        }
        String prefix = launchType.getPrefix();
        String path = findFile(
                new String[] { ".", "marathon-" + prefix + "-recorder", "../marathon-" + prefix + "-recorder",
                        System.getProperty(PROP_HOME, "."), dirOfMarathonJavaDriverJar, System.getenv("MARATHON_HOME") },
                "marathon-" + prefix + "-recorder.*.jar");
        if (path != null) {
            Logger.getLogger(JavaProfile.class.getName()).info("Using " + path + " for recorder");
            return path;
        }
        throw new WebDriverException("Unable to find marathon-recorder.jar. Set " + MARATHON_RECORDER
                + ".file environment variable to point to the jar file");
    }

    public void setRecordingPort(int recordingPort) {
        this.recordingPort = recordingPort;
    }

    public String getURL() {
        return "http://localhost:" + getPort();
    }

    public void copyOutputTo(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream() {
        if (outputStream == null) {
            return System.err;
        }
        return outputStream;
    }

    public JavaProfile setJavaHome(String javaHome) {
        this.javaHome = javaHome;
        return this;
    }

    public JavaProfile setJavaCommand(String vmCommand) {
        this.vmCommand = vmCommand;
        return this;
    }

    public URL asURL() throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder("http://javaprofile");
        builder.addParameter("launchmode", launchMode.name());
        for (int i = 1; i <= appArguments.size(); i++) {
            builder.addParameter("arg" + i, appArguments.get(i - 1));
        }
        if (command != null) {
            builder.addParameter("command", command);
        }
        if (startWindowTitle != null) {
            builder.addParameter("swt", startWindowTitle);
        }
        if (javaHome != null) {
            builder.addParameter("javahome", javaHome);
        }
        if (vmCommand != null) {
            builder.addParameter("vmcommand", vmCommand);
        }
        for (int i = 1; i <= vmArguments.size(); i++) {
            builder.addParameter("vmarg" + i, vmArguments.get(i - 1));
        }
        for (int i = 1; i <= classPathEntries.size(); i++) {
            builder.addParameter("cp" + i, classPathEntries.get(i - 1).getPath());
        }
        if (mainClass != null) {
            builder.addParameter("mainclass", mainClass);
        }
        for (int i = 1; i <= wsArguments.size(); i++) {
            builder.addParameter("wsarg" + i, wsArguments.get(i - 1));
        }
        if (jnlpPath != null) {
            builder.addParameter("jnlp", jnlpPath);
        }
        builder.addParameter("jnlpNoLocalCopy", Boolean.toString(jnlpNoLocalCopy));
        if (appletURL != null) {
            builder.addParameter("appleturl", appletURL);
        }
        if (nativeEvents) {
            builder.addParameter("nativeevents", nativeEvents + "");
        }
        if (executableJar != null) {
            builder.addParameter("executablejar", executableJar);
        }
        if (launchType != null) {
            builder.addParameter("launchtype", launchType.name());
        }
        builder.addParameter("keepLog", Boolean.toString(keepLog));
        return builder.build().toURL();
    }

    private void parse(URL url) throws URISyntaxException {
        List<NameValuePair> values = URLEncodedUtils.parse(url.toURI(), StandardCharsets.UTF_8);
        String launchModeStr = findValueOf(values, "launchmode");
        launchMode = LaunchMode.valueOf(launchModeStr);
        for (int i = 1;; i++) {
            if (hasValueFor(values, "arg" + i)) {
                appArguments.add(findValueOf(values, "arg" + i));
            } else {
                break;
            }
        }
        if (hasValueFor(values, "command")) {
            command = findValueOf(values, "command");
        }
        if (hasValueFor(values, "executablejar")) {
            executableJar = findValueOf(values, "executablejar");
        }
        if (hasValueFor(values, "swt")) {
            startWindowTitle = findValueOf(values, "swt");
        }
        if (hasValueFor(values, "javahome")) {
            javaHome = findValueOf(values, "javahome");
        }
        if (hasValueFor(values, "vmcommand")) {
            vmCommand = findValueOf(values, "vmcommand");
        }
        for (int i = 1;; i++) {
            if (hasValueFor(values, "vmarg" + i)) {
                vmArguments.add(findValueOf(values, "vmarg" + i));
            } else {
                break;
            }
        }
        for (int i = 1;; i++) {
            if (hasValueFor(values, "cp" + i)) {
                classPathEntries.add(new File(findValueOf(values, "cp" + i)));
            } else {
                break;
            }
        }
        if (hasValueFor(values, "mainclass")) {
            mainClass = findValueOf(values, "mainclass");
        }
        for (int i = 1;; i++) {
            if (hasValueFor(values, "wsarg" + i)) {
                wsArguments.add(findValueOf(values, "wsarg" + i));
            } else {
                break;
            }
        }
        if (hasValueFor(values, "jnlp")) {
            jnlpPath = findValueOf(values, "jnlp");
        }
        jnlpNoLocalCopy = false;
        if (hasValueFor(values, "jnlpNoLocalCopy")) {
            jnlpNoLocalCopy = Boolean.parseBoolean(findValueOf(values, "jnlpNoLocalCopy"));
        }
        if (hasValueFor(values, "appleturl")) {
            appletURL = findValueOf(values, "appleturl");
        }
        if (hasValueFor(values, "nativeevents")) {
            nativeEvents = true;
        }
        if (hasValueFor(values, "launchtype")) {
            launchType = LaunchType.valueOf(findValueOf(values, "launchtype"));
        }
        if (hasValueFor(values, "keepLog")) {
            keepLog = Boolean.parseBoolean(findValueOf(values, "keepLog"));
        }
    }

    private boolean hasValueFor(List<NameValuePair> values, String name) {
        for (NameValuePair nameValuePair : values) {
            if (nameValuePair.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private String findValueOf(List<NameValuePair> values, String name) {
        for (NameValuePair nameValuePair : values) {
            if (nameValuePair.getName().equals(name)) {
                return replaceEnviron(nameValuePair.getValue());
            }
        }
        throw new RuntimeException("Could not find value for " + name + " in " + values);
    }

    private String replaceEnviron(String value) {
        if (value == null) {
            return null;
        }
        Pattern p = Pattern.compile("[^%]*(%[^%]*%).*");
        Matcher m = p.matcher(value);
        while (m.matches()) {
            String var = m.group(1);
            String varValue = replaceEnviron(System.getProperty(var.substring(1, var.length() - 1)));
            if (varValue == null) {
                varValue = System.getProperty(var.substring(1, var.length() - 1), null);
                if (varValue == null) {
                    varValue = System.getenv(var.substring(1, var.length() - 1));
                    if (varValue == null) {
                        varValue = "";
                    }
                }
            }
            value = value.replaceAll(var, escape(varValue));
            m = p.matcher(value);
        }
        return value;
    }

    private static String escape(String value) {
        return value.replaceAll("\\\\", "\\\\\\\\");
    }

    public void setNativeEvents(boolean nativeEvents) {
        this.nativeEvents = nativeEvents;
    }

    @Override public String toString() {
        return "JavaProfile [classPathEntries=" + classPathEntries + ", vmArguments=" + vmArguments + ", wsArguments=" + wsArguments
                + ", appArguments=" + appArguments + ", launchMode=" + launchMode + ", mainClass=" + mainClass + ", jnlpPath="
                + jnlpPath + ", jnlpNoLocalCopy = " + jnlpNoLocalCopy + ", port=" + port + ", startWindowTitle=" + startWindowTitle
                + ", workingDirectory=" + workingDirectory + ", vmCommand=" + vmCommand + ", command=" + command + ", appletURL="
                + appletURL + ", recordingPort=" + recordingPort + ", javaHome=" + javaHome + ", nativeEvents=" + nativeEvents
                + "]";
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (appArguments == null ? 0 : appArguments.hashCode());
        result = prime * result + (appletURL == null ? 0 : appletURL.hashCode());
        result = prime * result + (classPathEntries == null ? 0 : classPathEntries.hashCode());
        result = prime * result + (command == null ? 0 : command.hashCode());
        result = prime * result + (javaHome == null ? 0 : javaHome.hashCode());
        result = prime * result + (jnlpPath == null ? 0 : jnlpPath.hashCode());
        result = prime * result + (jnlpNoLocalCopy ? 0 : 1);
        result = prime * result + (launchMode == null ? 0 : launchMode.hashCode());
        result = prime * result + (mainClass == null ? 0 : mainClass.hashCode());
        result = prime * result + (nativeEvents ? 1231 : 1237);
        result = prime * result + (startWindowTitle == null ? 0 : startWindowTitle.hashCode());
        result = prime * result + (vmArguments == null ? 0 : vmArguments.hashCode());
        result = prime * result + (vmCommand == null ? 0 : vmCommand.hashCode());
        result = prime * result + (workingDirectory == null ? 0 : workingDirectory.hashCode());
        result = prime * result + (wsArguments == null ? 0 : wsArguments.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        JavaProfile other = (JavaProfile) obj;
        if (appArguments == null) {
            if (other.appArguments != null) {
                return false;
            }
        } else if (!appArguments.equals(other.appArguments)) {
            return false;
        }
        if (appletURL == null) {
            if (other.appletURL != null) {
                return false;
            }
        } else if (!appletURL.equals(other.appletURL)) {
            return false;
        }
        if (classPathEntries == null) {
            if (other.classPathEntries != null) {
                return false;
            }
        } else if (!classPathEntries.equals(other.classPathEntries)) {
            return false;
        }
        if (command == null) {
            if (other.command != null) {
                return false;
            }
        } else if (!command.equals(other.command)) {
            return false;
        }
        if (javaHome == null) {
            if (other.javaHome != null) {
                return false;
            }
        } else if (!javaHome.equals(other.javaHome)) {
            return false;
        }
        if (jnlpPath == null) {
            if (other.jnlpPath != null) {
                return false;
            }
        } else if (!jnlpPath.equals(other.jnlpPath)) {
            return false;
        }
        if (jnlpNoLocalCopy != other.jnlpNoLocalCopy)
            return false;
        if (launchMode != other.launchMode) {
            return false;
        }
        if (mainClass == null) {
            if (other.mainClass != null) {
                return false;
            }
        } else if (!mainClass.equals(other.mainClass)) {
            return false;
        }
        if (nativeEvents != other.nativeEvents) {
            return false;
        }
        if (startWindowTitle == null) {
            if (other.startWindowTitle != null) {
                return false;
            }
        } else if (!startWindowTitle.equals(other.startWindowTitle)) {
            return false;
        }
        if (vmArguments == null) {
            if (other.vmArguments != null) {
                return false;
            }
        } else if (!vmArguments.equals(other.vmArguments)) {
            return false;
        }
        if (vmCommand == null) {
            if (other.vmCommand != null) {
                return false;
            }
        } else if (!vmCommand.equals(other.vmCommand)) {
            return false;
        }
        if (workingDirectory == null) {
            if (other.workingDirectory != null) {
                return false;
            }
        } else if (!workingDirectory.equals(other.workingDirectory)) {
            return false;
        }
        if (wsArguments == null) {
            if (other.wsArguments != null) {
                return false;
            }
        } else if (!wsArguments.equals(other.wsArguments)) {
            return false;
        }
        return true;
    }

    public boolean isNativeEvents() {
        return nativeEvents;
    }

    public JavaProfile setExecutableJar(String jar) {
        checkValidProperty("executablejar");
        this.executableJar = jar;
        return this;

    }

    public boolean isEmbedded() {
        return launchMode == LaunchMode.EMBEDDED;
    }

    public boolean isCommandLine() {
        return launchMode == LaunchMode.COMMAND_LINE;
    }

    public boolean isJavaCommandLine() {
        return launchMode == LaunchMode.JAVA_COMMAND_LINE;
    }

    public boolean isExecutableJar() {
        return launchMode == LaunchMode.EXECUTABLE_JAR;
    }

    public boolean isJavaApplet() {
        return launchMode == LaunchMode.JAVA_APPLET;
    }

    public boolean isJavaWebStart() {
        return launchMode == LaunchMode.JAVA_WEBSTART;
    }

    public JavaProfile setLaunchType(LaunchType launchType) {
        this.launchType = launchType;
        return this;
    }

    public LaunchType getLaunchType() {
        return launchType;
    }

    public void setKeepLog(boolean keepLog) {
        this.keepLog = keepLog;
    }

    public JavaProfile setJNLPNoLocalCopy(boolean jnlpNoLocalCopy) {
        checkValidProperty("jnlpnolocalcopy");
        this.jnlpNoLocalCopy = jnlpNoLocalCopy;
        return this;
    }

    private String getLocalCopy(String url) {
        if (jnlpNoLocalCopy || !validURL(url))
            return url;
        File file = jnlpFiles.get(url);
        if (file == null) {
            file = createJNLPCopy(url);
            if (file != NULLFILE) {
                LOGGER.info("WebStart: Copied remote URL " + url + " to " + file.getAbsolutePath());
            } else {
                LOGGER.info("WebStart: Considering " + url + " as local");
            }
            jnlpFiles.put(url, file);
        }
        if (file == NULLFILE) {
            return url;
        }
        return file.getAbsolutePath();
    }

    private File createJNLPCopy(String urlSpec) {
        File jnlpFile = NULLFILE;
        OutputStream os = null;
        InputStream is = null;
        try {
            URL url = new URL(urlSpec);
            URLConnection openConnection = url.openConnection();
            Object content = openConnection.getContent();
            File tempFile = File.createTempFile("marathon", ".jnlp");
            tempFile.deleteOnExit();
            os = new FileOutputStream(tempFile);
            if (content instanceof InputStream) {
                is = (InputStream) content;
                byte[] b = new byte[1024];
                int n;
                while ((n = is.read(b)) != -1) {
                    os.write(b, 0, n);
                }
            }
            jnlpFile = tempFile;
        } catch (Exception e) {
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }
        return jnlpFile;
    }

    private boolean validURL(String urlPath) {
        try {
            URL url = new URL(urlPath);
            String protocol = url.getProtocol();
            return protocol.equalsIgnoreCase("http") || protocol.equalsIgnoreCase("https");
        } catch (MalformedURLException e) {
        }
        return false;
    }
}
