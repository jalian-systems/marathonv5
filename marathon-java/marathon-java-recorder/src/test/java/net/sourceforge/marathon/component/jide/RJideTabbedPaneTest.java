package net.sourceforge.marathon.component.jide;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.JideTabbedPane;

import net.sourceforge.marathon.component.LoggingRecorder;
import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.component.RComponentTest;
import net.sourceforge.marathon.component.RTabbedPane;
import net.sourceforge.marathon.javaagent.Device;
import net.sourceforge.marathon.javaagent.IDevice;
import net.sourceforge.marathon.javaagent.IDevice.Buttons;
import net.sourceforge.marathon.javaagent.components.jide.JideTabbedPanePanel;
import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

public class RJideTabbedPaneTest extends RComponentTest {

    protected JFrame frame;
    private JideTabbedPane tabbedPane;

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                JideTabbedPanePanel jideTabbedPaneDemo = new JideTabbedPanePanel();
                frame = new JFrame("JideTabbedPane Demo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(jideTabbedPaneDemo);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    @AfterMethod
    public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    @Test
    public void selectANormalTab() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                tabbedPane = (JideTabbedPane) ComponentUtils.findComponent(JideTabbedPane.class, frame);
                RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, lr);
                rtp.stateChanged(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("Mail", call.getState());
        siw(new Runnable() {
            @Override
            public void run() {
                tabbedPane = (JideTabbedPane) ComponentUtils.findComponent(JideTabbedPane.class, frame);
                RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, lr);
                tabbedPane.setSelectedIndex(2);
                rtp.stateChanged(null);
            }
        });
        call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("Contacts", call.getState());
    }

    @Test
    public void selectANoTextTab() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                tabbedPane = (JideTabbedPane) ComponentUtils.findComponent(JideTabbedPane.class, frame);
                tabbedPane.setTitleAt(3, null);
                RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, lr);
                tabbedPane.setSelectedIndex(3);
                rtp.stateChanged(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("tabIndex-3", call.getState());
    }

    @Test
    public void selectANoIconAndTextTab() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                tabbedPane = (JideTabbedPane) ComponentUtils.findComponent(JideTabbedPane.class, frame);
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

    @Test
    public void areWeGettingMultipleSelects() throws Throwable {
        IDevice d = Device.getDevice();
        final LoggingRecorder lr = new LoggingRecorder();
        final JideTabbedPane tp = (JideTabbedPane) ComponentUtils.findComponent(JideTabbedPane.class, frame);
        tp.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                System.out.println("RJideTabbedPaneTest.areWeGettingMultipleSelects().new ChangeListener() {...}.stateChanged()");
                RTabbedPane rtp = new RTabbedPane(tp, null, null, lr);
                rtp.stateChanged(e);
            }
        });
        Point p = getTabClickPoint(tp, 1);
        d.click(tp, Buttons.LEFT, 1, p.x, p.y);
        p = getTabClickPoint(tp, 2);
        d.click(tp, Buttons.LEFT, 1, p.x, p.y);
        AssertJUnit.assertEquals(1, lr.getCalls().size());
    }

    @Test
    public void tabDuplicates() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                tabbedPane = (JideTabbedPane) ComponentUtils.findComponent(JideTabbedPane.class, frame);
                tabbedPane.setTitleAt(2, "Tab 2");
                RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, lr);
                tabbedPane.setSelectedIndex(1);
                rtp.stateChanged(null);
            }
        });
        Call cal = lr.getCall();
        AssertJUnit.assertEquals("select", cal.getFunction());
        AssertJUnit.assertEquals("Calendar", cal.getState());

        siw(new Runnable() {
            @Override
            public void run() {
                tabbedPane = (JideTabbedPane) ComponentUtils.findComponent(JideTabbedPane.class, frame);
                tabbedPane.setTitleAt(2, "Calendar");
                RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, lr);
                tabbedPane.setSelectedIndex(2);
                rtp.stateChanged(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("Calendar(1)", call.getState());
    }

    @Test
    public void tabMultipleDuplicates() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                tabbedPane = (JideTabbedPane) ComponentUtils.findComponent(JideTabbedPane.class, frame);
                tabbedPane.setTitleAt(2, "Calendar");
                RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, lr);
                tabbedPane.setSelectedIndex(1);
                rtp.stateChanged(null);
            }
        });
        Call cal = lr.getCall();
        AssertJUnit.assertEquals("select", cal.getFunction());
        AssertJUnit.assertEquals("Calendar", cal.getState());

        siw(new Runnable() {
            @Override
            public void run() {
                tabbedPane = (JideTabbedPane) ComponentUtils.findComponent(JideTabbedPane.class, frame);
                tabbedPane.setTitleAt(0, "Tab 2");
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

    protected Point getTabClickPoint(final JideTabbedPane tp, final int tabIndex) {
        final Point[] ps = new Point[] { null };
        siw(new Runnable() {
            @Override
            public void run() {
                Rectangle boundsAt = tp.getBoundsAt(tabIndex);
                ps[0] = new Point(boundsAt.x + boundsAt.width / 2, boundsAt.y + boundsAt.height / 2);
            }
        });
        Point p = ps[0];
        return p;
    }

    @Test
    public void assertContent() throws Throwable {
        siw(new Runnable() {
            @Override
            public void run() {
                tabbedPane = (JideTabbedPane) ComponentUtils.findComponent(JideTabbedPane.class, frame);
            }
        });
        final RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, new LoggingRecorder());
        final Object[] content = new Object[] { null };
        siw(new Runnable() {
            @Override
            public void run() {
                content[0] = rtp.getContent();
            }
        });
        JSONArray a = new JSONArray(content[0]);
        Assert.assertEquals("[[\"Mail\",\"Calendar\",\"Contacts\",\"Tasks\",\"Notes\",\"Folder List\",\"Shortcuts\",\"Journal\"]]",
                a.toString());
    }

    @Test
    public void assertContentDuplicates() throws Throwable {
        siw(new Runnable() {
            @Override
            public void run() {
                tabbedPane = (JideTabbedPane) ComponentUtils.findComponent(JideTabbedPane.class, frame);
                tabbedPane.setTitleAt(2, "Tab 2");
            }
        });
        final RTabbedPane rtp = new RTabbedPane(tabbedPane, null, null, new LoggingRecorder());
        final Object[] content = new Object[] { null };
        siw(new Runnable() {
            @Override
            public void run() {
                content[0] = rtp.getContent();
            }
        });
        JSONArray a = new JSONArray(content[0]);
        Assert.assertEquals("[[\"Mail\",\"Calendar\",\"Tab 2\",\"Tasks\",\"Notes\",\"Folder List\",\"Shortcuts\",\"Journal\"]]",
                a.toString());
    }
}
