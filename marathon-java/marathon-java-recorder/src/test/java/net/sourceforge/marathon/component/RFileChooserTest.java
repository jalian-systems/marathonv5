/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.component;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.component.RFileChooser;
import net.sourceforge.marathon.javaagent.Wait;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.FileChooserDemo;

@Test public class RFileChooserTest extends RComponentTest {
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(RFileChooserTest.class.getSimpleName());
                frame.setName("frame-" + RFileChooserTest.class.getSimpleName());
                frame.getContentPane().add(new FileChooserDemo(), BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
                File file = new File(System.getProperty("java.home"));
                System.setProperty("marathon.project.dir", file.getPath());
            }
        });
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    public void selectSingleFileSelection() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                JButton button = (JButton) ComponentUtils.findComponent(JButton.class, frame);
                button.doClick();
            }
        });
        new Wait("Waiting for window to open") {
            @Override public boolean until() {
                Window[] windows = Window.getWindows();
                int windowCount = 0;
                for (Window w : windows) {
                    if (w.isVisible())
                        windowCount++;
                }
                return windowCount > 1;
            }
        };
        Window[] windows = Window.getWindows();
        JFileChooser fc1 = null;
        for (Window window : windows) {
            fc1 = (JFileChooser) ComponentUtils.findComponent(JFileChooser.class, window);
            if (fc1 != null)
                break;
        }
        final JFileChooser fc = fc1;
        String property = System.getProperty("user.dir");
        final File file = new File(property);
        final File[] listFiles = file.listFiles();
        fc.addActionListener(new ActionListener() {

            @Override public void actionPerformed(ActionEvent e) {
                RFileChooser rFileChooser = new RFileChooser((JFileChooser) e.getSource(), null, null, lr);
                rFileChooser.actionPerformed(e);

            }
        });
        siw(new Runnable() {

            @Override public void run() {
                fc.setSelectedFile(listFiles[0].getAbsoluteFile());
                fc.approveSelection();

            }
        });

        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        String path = null;
        String absolutePath = listFiles[0].getAbsolutePath();
        if (absolutePath.startsWith(property)) {
            String prefix = "#C";
            path = (prefix + absolutePath.substring(property.length())).replace(File.separatorChar, '/');
        }
        AssertJUnit.assertEquals(path, call.getState());
    }

    public void selectNoFileSelection() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                JButton button = (JButton) ComponentUtils.findComponent(JButton.class, frame);
                button.doClick();
            }
        });
        new Wait("Waiting for window to open") {
            @Override public boolean until() {
                Window[] windows = Window.getWindows();
                int windowCount = 0;
                for (Window w : windows) {
                    if (w.isVisible())
                        windowCount++;
                }
                return windowCount > 1;
            }
        };
        Window[] windows = Window.getWindows();
        JFileChooser fc1 = null;
        for (Window window : windows) {
            fc1 = (JFileChooser) ComponentUtils.findComponent(JFileChooser.class, window);
            if (fc1 != null)
                break;
        }
        final JFileChooser fc = fc1;
        final File file = new File("");
        fc.addActionListener(new ActionListener() {

            @Override public void actionPerformed(ActionEvent e) {
                RFileChooser rFileChooser = new RFileChooser((JFileChooser) e.getSource(), null, null, lr);
                rFileChooser.actionPerformed(e);

            }
        });
        siw(new Runnable() {

            @Override public void run() {
                fc.setSelectedFile(file);
                fc.cancelSelection();

            }
        });

        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("", call.getState());
    }

    public void selectMultipleFileSelection() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                JButton button = (JButton) ComponentUtils.findComponent(JButton.class, frame);
                button.doClick();
            }
        });
        new Wait("Waiting for window to open") {
            @Override public boolean until() {
                Window[] windows = Window.getWindows();
                int windowCount = 0;
                for (Window w : windows) {
                    if (w.isVisible())
                        windowCount++;
                }
                return windowCount > 1;
            }
        };
        Window[] windows = Window.getWindows();
        JFileChooser fc1 = null;
        for (Window window : windows) {
            fc1 = (JFileChooser) ComponentUtils.findComponent(JFileChooser.class, window);
            if (fc1 != null)
                break;
        }
        final JFileChooser fc = fc1;
        fc.addActionListener(new ActionListener() {

            @Override public void actionPerformed(ActionEvent e) {
                RFileChooser rFileChooser = new RFileChooser(fc, null, null, lr);
                rFileChooser.actionPerformed(e);
            }
        });
        fc.setMultiSelectionEnabled(true);
        String property = System.getProperty("user.dir");
        File file = new File(property);
        File[] listFiles = file.listFiles();
        final File[] files = new File[] { listFiles[0].getAbsoluteFile(), listFiles[1].getAbsoluteFile() };
        siw(new Runnable() {

            @Override public void run() {
                fc.setSelectedFiles(files);
                fc.approveSelection();

            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        String path1 = null;
        String path2 = null;
        String absolutePath1 = listFiles[0].getAbsolutePath();
        String absolutePath2 = listFiles[1].getAbsolutePath();
        if (absolutePath1.startsWith(property)) {
            String prefix = "#C";
            path1 = (prefix + absolutePath1.substring(property.length())).replace(File.separatorChar, '/');
        }
        if (absolutePath2.startsWith(property)) {
            String prefix = "#C";
            path2 = (prefix + absolutePath2.substring(property.length())).replace(File.separatorChar, '/');
        }
        AssertJUnit.assertEquals(path1 + File.pathSeparator + path2, call.getState());
    }

    public void selectHomeDirFileSelection() {
        final LoggingRecorder lr = new LoggingRecorder();
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                JButton button = (JButton) ComponentUtils.findComponent(JButton.class, frame);
                button.doClick();
            }
        });
        new Wait("Waiting for window to open") {
            @Override public boolean until() {
                Window[] windows = Window.getWindows();
                int windowCount = 0;
                for (Window w : windows) {
                    if (w.isVisible())
                        windowCount++;
                }
                return windowCount > 1;
            }
        };
        Window[] windows = Window.getWindows();
        JFileChooser fc1 = null;
        for (Window window : windows) {
            fc1 = (JFileChooser) ComponentUtils.findComponent(JFileChooser.class, window);
            if (fc1 != null)
                break;
        }
        final JFileChooser fc = fc1;
        String property = System.getProperty("user.home");
        final File file = new File(property);
        final File[] listFiles = file.listFiles();
        fc.addActionListener(new ActionListener() {

            @Override public void actionPerformed(ActionEvent e) {
                RFileChooser rFileChooser = new RFileChooser((JFileChooser) e.getSource(), null, null, lr);
                rFileChooser.actionPerformed(e);

            }
        });
        siw(new Runnable() {

            @Override public void run() {
                fc.setSelectedFile(listFiles[0].getAbsoluteFile());
                fc.approveSelection();

            }
        });

        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        String path = null;
        String absolutePath = listFiles[0].getAbsolutePath();
        if (absolutePath.startsWith(property)) {
            String prefix = "#H";
            path = (prefix + absolutePath.substring(property.length())).replace(File.separatorChar, '/');
        }
        AssertJUnit.assertEquals(path, call.getState());
    }

    public void selectMarathonDirFileSelection() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                JButton button = (JButton) ComponentUtils.findComponent(JButton.class, frame);
                button.doClick();
            }
        });
        new Wait("Waiting for window to open") {
            @Override public boolean until() {
                Window[] windows = Window.getWindows();
                int windowCount = 0;
                for (Window w : windows) {
                    if (w.isVisible())
                        windowCount++;
                }
                return windowCount > 1;
            }
        };
        Window[] windows = Window.getWindows();
        JFileChooser fc1 = null;
        for (Window window : windows) {
            fc1 = (JFileChooser) ComponentUtils.findComponent(JFileChooser.class, window);
            if (fc1 != null)
                break;
        }
        final JFileChooser fc = fc1;
        String property = System.getProperty("marathon.project.dir");
        final File file = new File(property);
        final File[] listFiles = file.listFiles();
        fc.addActionListener(new ActionListener() {

            @Override public void actionPerformed(ActionEvent e) {
                RFileChooser rFileChooser = new RFileChooser((JFileChooser) e.getSource(), null, null, lr);
                rFileChooser.actionPerformed(e);

            }
        });
        siw(new Runnable() {

            @Override public void run() {
                fc.setSelectedFile(listFiles[0].getAbsoluteFile());
                fc.approveSelection();

            }
        });

        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        String path = null;
        String absolutePath = listFiles[0].getAbsolutePath();
        if (absolutePath.startsWith(property)) {
            String prefix = "#M";
            path = (prefix + absolutePath.substring(property.length())).replace(File.separatorChar, '/');
        }
        AssertJUnit.assertEquals(path, call.getState());
    }

}
