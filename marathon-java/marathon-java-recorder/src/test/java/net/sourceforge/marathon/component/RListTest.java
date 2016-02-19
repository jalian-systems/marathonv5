package net.sourceforge.marathon.component;

import java.awt.BorderLayout;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.javaagent.components.PropertyHelper;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

import org.json.JSONArray;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.ListDemo;

@Test public class RListTest extends RComponentTest {
    protected JFrame frame;
    private JList list;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(RListTest.class.getSimpleName());
                frame.setName("frame-" + RListTest.class.getSimpleName());
                frame.getContentPane().add(new ListDemo(), BorderLayout.CENTER);
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

    public void selectNoSelection() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                list = (JList) ComponentUtils.findComponent(JList.class, frame);
                list.setSelectedIndices(new int[0]);
                RList rList = new RList(list, null, null, lr);
                rList.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("[]", call.getState());
    }

    public void selectSingleItemSelection() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                list = (JList) ComponentUtils.findComponent(JList.class, frame);
                list.setSelectedIndices(new int[] { 1 });
                RList rList = new RList(list, null, null, lr);
                rList.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("[John Smith]", call.getState());
    }

    public void selectMultipleItemSelection() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                list = (JList) ComponentUtils.findComponent(JList.class, frame);
                list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                list.setSelectedIndices(new int[] { 0, 2 });
                RList rList = new RList(list, null, null, lr);
                rList.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("[Jane Doe, Kathy Green]", call.getState());
    }

    public void selectSpecialItemSelection() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                list = (JList) ComponentUtils.findComponent(JList.class, frame);
                DefaultListModel model = (DefaultListModel) list.getModel();
                model.set(0, " Special Characters ([],)");
                list.setSelectedIndices(new int[] { 0 });
                RList rList = new RList(list, null, null, lr);
                rList.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("[\\ Special Characters ([]\\,)]", call.getState());
    }

    public void listDuplicates() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                list = (JList) ComponentUtils.findComponent(JList.class, frame);
                list.setSelectedIndices(new int[] { 1 });
                RList rList = new RList(list, null, null, lr);
                rList.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("[John Smith]", call.getState());

        siw(new Runnable() {
            @Override public void run() {
                list = (JList) ComponentUtils.findComponent(JList.class, frame);
                DefaultListModel model = (DefaultListModel) list.getModel();
                model.set(2, "John Smith");
                list.setSelectedIndices(new int[] { 2 });
                RList rList = new RList(list, null, null, lr);
                rList.focusLost(null);
            }
        });
        Call calll = lr.getCall();
        AssertJUnit.assertEquals("select", calll.getFunction());
        AssertJUnit.assertEquals("[John Smith(1)]", calll.getState());
    }

    public void listMultipleDuplicates() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                list = (JList) ComponentUtils.findComponent(JList.class, frame);
                list.setSelectedIndices(new int[] { 1 });
                RList rList = new RList(list, null, null, lr);
                rList.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("[John Smith]", call.getState());

        siw(new Runnable() {
            @Override public void run() {
                list = (JList) ComponentUtils.findComponent(JList.class, frame);
                DefaultListModel model = (DefaultListModel) list.getModel();
                model.set(0, "John Smith");
                model.set(1, "John Smith");
                model.set(2, "John Smith");
                list.setSelectedIndices(new int[] { 2 });
                RList rList = new RList(list, null, null, lr);
                rList.focusLost(null);
            }
        });
        Call calll = lr.getCall();
        AssertJUnit.assertEquals("select", calll.getFunction());
        AssertJUnit.assertEquals("[John Smith(2)]", calll.getState());
    }

    public void propertyhelperWorksOk() {
        Properties p = new Properties();
        p.setProperty("listText", " Hello");
        String r = PropertyHelper.toString(new Properties[] { p }, new String[] { "listText" });
        AssertJUnit.assertEquals("[\\ Hello]", r);
    }

    public void assertContent() throws Throwable {
        siw(new Runnable() {
            @Override public void run() {
                list = (JList) ComponentUtils.findComponent(JList.class, frame);
            }
        });
        final RList rList = new RList(list, null, null, new LoggingRecorder());
        final Object[] content = new Object[] { null };
        siw(new Runnable() {
            @Override public void run() {
                content[0] = rList.getContent();
            }
        });
        JSONArray a = new JSONArray(content[0]);
        AssertJUnit.assertEquals("[[\"Jane Doe\",\"John Smith\",\"Kathy Green\"]]", a.toString());
    }

    public void assertContentDuplicates() throws Throwable {
        siw(new Runnable() {
            @Override public void run() {
                list = (JList) ComponentUtils.findComponent(JList.class, frame);
                DefaultListModel model = (DefaultListModel) list.getModel();
                model.set(2, "John Smith");
                list.setSelectedIndices(new int[] { 2 });
            }
        });
        final RList rList = new RList(list, null, null, new LoggingRecorder());
        final Object[] content = new Object[] { null };
        siw(new Runnable() {
            @Override public void run() {
                content[0] = rList.getContent();
            }
        });
        JSONArray a = new JSONArray(content[0]);
        AssertJUnit.assertEquals("[[\"Jane Doe\",\"John Smith\",\"John Smith(1)\"]]", a.toString());
    }
}
