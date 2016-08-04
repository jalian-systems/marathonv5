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
package net.sourceforge.marathon.component;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.JSONArray;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.TabbedPaneDemo;
import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.javaagent.Device;
import net.sourceforge.marathon.javaagent.IDevice;
import net.sourceforge.marathon.javaagent.IDevice.Buttons;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

@Test public class RTabbedPaneTest extends RComponentTest {
    protected JFrame frame;
    private JTabbedPane tabbedPane;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(RTabbedPaneTest.class.getSimpleName());
                frame.setName("frame-" + RTabbedPaneTest.class.getSimpleName());
                frame.getContentPane().add(new TabbedPaneDemo(), BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
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

    public void selectANormalTab() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                tabbedPane = (JTabbedPane) ComponentUtils.findComponent(JTabbedPane.class, frame);
                RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, lr);
                rtp.stateChanged(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("Tab 1", call.getState());
        siw(new Runnable() {
            @Override public void run() {
                tabbedPane = (JTabbedPane) ComponentUtils.findComponent(JTabbedPane.class, frame);
                RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, lr);
                tabbedPane.setSelectedIndex(2);
                rtp.stateChanged(null);
            }
        });
        call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("Tab 3", call.getState());
    }

    public void selectANoTextTab() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                tabbedPane = (JTabbedPane) ComponentUtils.findComponent(JTabbedPane.class, frame);
                tabbedPane.setTitleAt(3, null);
                RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, lr);
                tabbedPane.setSelectedIndex(3);
                rtp.stateChanged(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("middle", call.getState());
    }

    public void selectANoIconAndTextTab() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                tabbedPane = (JTabbedPane) ComponentUtils.findComponent(JTabbedPane.class, frame);
                tabbedPane.setTitleAt(3, null);
                tabbedPane.setIconAt(3, null);
                RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, lr);
                tabbedPane.setSelectedIndex(3);
                rtp.stateChanged(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("tabIndex-3", call.getState());
    }

    public void areWeGettingMultipleSelects() throws Throwable {
        IDevice d = Device.getDevice();
        final LoggingRecorder lr = new LoggingRecorder();
        final JTabbedPane tp = (JTabbedPane) ComponentUtils.findComponent(JTabbedPane.class, frame);
        Point p = getTabClickPoint(tp, 1);
        d.click(tp, Buttons.LEFT, 1, p.x, p.y);
        tp.addChangeListener(new ChangeListener() {
            @Override public void stateChanged(ChangeEvent e) {
                RTabbedPane rtp = new RTabbedPane(tp, null, null, lr);
                rtp.stateChanged(e);
            }
        });
        p = getTabClickPoint(tp, 2);
        d.click(tp, Buttons.LEFT, 1, p.x, p.y);
        AssertJUnit.assertEquals(1, lr.getCalls().size());
    }

    public void tabDuplicates() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                tabbedPane = (JTabbedPane) ComponentUtils.findComponent(JTabbedPane.class, frame);
                tabbedPane.setTitleAt(2, "Tab 2");
                RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, lr);
                tabbedPane.setSelectedIndex(1);
                rtp.stateChanged(null);
            }
        });
        Call cal = lr.getCall();
        AssertJUnit.assertEquals("select", cal.getFunction());
        AssertJUnit.assertEquals("Tab 2", cal.getState());

        siw(new Runnable() {
            @Override public void run() {
                tabbedPane = (JTabbedPane) ComponentUtils.findComponent(JTabbedPane.class, frame);
                tabbedPane.setTitleAt(2, "Tab 2");
                RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, lr);
                tabbedPane.setSelectedIndex(2);
                rtp.stateChanged(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("Tab 2(1)", call.getState());
    }

    public void tabMultipleDuplicates() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                tabbedPane = (JTabbedPane) ComponentUtils.findComponent(JTabbedPane.class, frame);
                tabbedPane.setTitleAt(2, "Tab 2");
                RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, lr);
                tabbedPane.setSelectedIndex(1);
                rtp.stateChanged(null);
            }
        });
        Call cal = lr.getCall();
        AssertJUnit.assertEquals("select", cal.getFunction());
        AssertJUnit.assertEquals("Tab 2", cal.getState());

        siw(new Runnable() {
            @Override public void run() {
                tabbedPane = (JTabbedPane) ComponentUtils.findComponent(JTabbedPane.class, frame);
                tabbedPane.setTitleAt(0, "Tab 2");
                tabbedPane.setTitleAt(2, "Tab 2");
                RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, lr);
                tabbedPane.setSelectedIndex(2);
                rtp.stateChanged(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("Tab 2(2)", call.getState());
    }

    protected Point getTabClickPoint(final JTabbedPane tp, final int tabIndex) {
        final Point[] ps = new Point[] { null };
        siw(new Runnable() {
            @Override public void run() {
                Rectangle boundsAt = tp.getBoundsAt(tabIndex);
                ps[0] = new Point(boundsAt.x + boundsAt.width / 2, boundsAt.y + boundsAt.height / 2);
            }
        });
        Point p = ps[0];
        return p;
    }

    public void assertContent() throws Throwable {
        siw(new Runnable() {
            @Override public void run() {
                tabbedPane = (JTabbedPane) ComponentUtils.findComponent(JTabbedPane.class, frame);
            }
        });
        final RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, new LoggingRecorder());
        final Object[] content = new Object[] { null };
        siw(new Runnable() {
            @Override public void run() {
                content[0] = rtp.getContent();
            }
        });
        JSONArray a = new JSONArray(content[0]);
        Assert.assertEquals("[[\"Tab 1\",\"Tab 2\",\"Tab 3\",\"Tab 4\"]]", a.toString());
    }

    public void assertContentDuplicates() throws Throwable {
        siw(new Runnable() {
            @Override public void run() {
                tabbedPane = (JTabbedPane) ComponentUtils.findComponent(JTabbedPane.class, frame);
                tabbedPane.setTitleAt(2, "Tab 2");
            }
        });
        final RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, new LoggingRecorder());
        final Object[] content = new Object[] { null };
        siw(new Runnable() {
            @Override public void run() {
                content[0] = rtp.getContent();
            }
        });
        JSONArray a = new JSONArray(content[0]);
        Assert.assertEquals("[[\"Tab 1\",\"Tab 2\",\"Tab 2(1)\",\"Tab 4\"]]", a.toString());
    }
}
