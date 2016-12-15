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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;

import org.apache.commons.exec.OS;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.os.CommandLine;
import org.testng.AssertJUnit;
import org.testng.SkipException;
import org.testng.annotations.Test;

import net.sourceforge.marathon.javaagent.Wait;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchMode;
import net.sourceforge.marathon.testhelpers.MissingException;

@Test public class JavaProfileTest {

    public void getCommandLine() {
        JavaProfile profile = new JavaProfile();
        AssertJUnit.assertNull(profile.getCommandLine());
    }

    public void getJavaCommandLine() {
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_COMMAND_LINE).addVMArgument("-version");
        CommandLine commandLine = profile.getCommandLine();
        AssertJUnit.assertNotNull(commandLine);
    }

    public void executeCommand() throws Throwable {
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_COMMAND_LINE).setMainClass("-version");
        final CommandLine commandLine = profile.getCommandLine();
        AssertJUnit.assertNotNull(commandLine);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        commandLine.copyOutputTo(baos);
        commandLine.executeAsync();
        new Wait("Waiting till the command is complete") {
            @Override public boolean until() {
                return !commandLine.isRunning();
            }
        };
        BufferedReader reader = new BufferedReader(new StringReader(new String(baos.toByteArray())));
        String line = reader.readLine();
        while (line != null && !line.contains("java version")) {
            line = reader.readLine();
        }
        AssertJUnit.assertTrue(line.contains("java version"));
    }

    public void getJavaCommandLineWithClasspath() throws Throwable {
        File f = new File(".").getCanonicalFile();
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_COMMAND_LINE).addClassPath(f);
        CommandLine commandLine = profile.getCommandLine();
        AssertJUnit.assertTrue(commandLine.toString().contains("-cp"));
        AssertJUnit.assertTrue(commandLine.toString().contains(f.getAbsolutePath()));
    }

    public void getJavaCommandLineWithClasspathErr() throws Throwable {
        File f = new File("./bin/something.jar");
        try {
            new JavaProfile(LaunchMode.JAVA_COMMAND_LINE).addClassPath(f);
            throw new MissingException(WebDriverException.class);
        } catch (WebDriverException e) {
        }
    }

    public void addingSettingWrongParmsToLaunchMode() throws Throwable {
        try {
            new JavaProfile().setMainClass("net.sourceforge.marathon.Main");
            throw new MissingException(WebDriverException.class);
        } catch (WebDriverException e) {
        }
    }

    public void executeWSCommand() throws Throwable {
        if (OS.isFamilyWindows()) {
            throw new SkipException("Test not valid for Windows");
        }
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_WEBSTART).addWSArgument("-verbose").addVMArgument("-Dx.y.z=hello");
        final CommandLine commandLine = profile.getCommandLine();
        AssertJUnit.assertNotNull(commandLine);
        AssertJUnit.assertTrue(commandLine.toString().contains("-javaagent:"));
        AssertJUnit.assertTrue(commandLine.toString().contains("-verbose"));
        AssertJUnit.assertTrue(commandLine.toString().contains("-Dx.y.z=hello"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        commandLine.copyOutputTo(baos);
        commandLine.executeAsync();
        new Wait("Waiting till the command is complete") {
            @Override public boolean until() {
                return !commandLine.isRunning();
            }
        };
        BufferedReader reader = new BufferedReader(new StringReader(new String(baos.toByteArray())));
        String line = reader.readLine();
        while (line != null && !line.contains("Web Start")) {
            line = reader.readLine();
        }
        AssertJUnit.assertTrue(line.contains("Web Start"));
    }

    public void getWsCommandWithJNLP() throws Throwable {
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_WEBSTART).addWSArgument("-verbose").addVMArgument("-Dx.y.z=hello");
        profile.setJNLPFile(new File("SwingSet3.jnlp"));
        final CommandLine commandLine = profile.getCommandLine();
        AssertJUnit.assertNotNull(commandLine);
        AssertJUnit.assertTrue(commandLine.toString().contains("-javaagent:"));
        AssertJUnit.assertTrue(commandLine.toString().contains("-verbose"));
        AssertJUnit.assertTrue(commandLine.toString().contains("-Dx.y.z=hello"));
        AssertJUnit.assertTrue(commandLine.toString().contains("SwingSet3.jnlp"));
    }

    public void convertURICommandLine() throws Throwable {
        JavaProfile expected = new JavaProfile(LaunchMode.COMMAND_LINE).addApplicationArguments("Hello", "World")
                .setCommand("/usr/bin/ls").setStartWindowTitle("The World is Huge").setJavaCommand("/usr/bin/java")
                .setJavaHome("/usr/bin/javahome");
        JavaProfile actual = new JavaProfile(expected.asURL());
        AssertJUnit.assertEquals(expected, actual);
    }

    public void convertURIJavaCommandLine() throws Throwable {
        JavaProfile expected = new JavaProfile(LaunchMode.JAVA_COMMAND_LINE).addApplicationArguments("Hello", "World")
                .setJavaCommand("/usr/bin/java").setJavaHome("/usr/bin/javahome").addVMArgument("-Xmx512m", "-Xms512m")
                .addClassPath(new File(".").getCanonicalFile()).setMainClass("net.sourceforge.marathon.whatever.Main");
        JavaProfile actual = new JavaProfile(expected.asURL());
        AssertJUnit.assertEquals(expected, actual);
    }

    public void convertURIJavaWSCommand() throws Throwable {
        JavaProfile expected = new JavaProfile(LaunchMode.JAVA_WEBSTART).setJavaCommand("/usr/bin/java")
                .setJavaHome("/usr/bin/javahome").addVMArgument("-Xmx512m", "-Xms512m").setStartWindowTitle("The World Is Huge")
                .addWSArgument("-silent", "-nologo").setJNLPFile(new File("/etc/hosts"));
        JavaProfile actual = new JavaProfile(expected.asURL());
        AssertJUnit.assertEquals(expected, actual);
    }

    public void convertURIJavaAppletCommand() throws Throwable {
        JavaProfile expected = new JavaProfile(LaunchMode.JAVA_APPLET).setJavaCommand("/usr/bin/java")
                .setJavaHome("/usr/bin/javahome").addVMArgument("-Xmx512m", "-Xms512m").setStartWindowTitle("The World Is Huge")
                .setAppletURL("http://google.com");
        JavaProfile actual = new JavaProfile(expected.asURL());
        AssertJUnit.assertEquals(expected, actual);
    }
}
