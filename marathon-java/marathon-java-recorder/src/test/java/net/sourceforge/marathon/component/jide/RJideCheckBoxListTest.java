package net.sourceforge.marathon.component.jide;

import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.CheckBoxList;
import com.jidesoft.swing.CheckBoxListCellRenderer;

import net.sourceforge.marathon.component.LoggingRecorder;
import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.component.RComponentTest;
import net.sourceforge.marathon.component.RList;
import net.sourceforge.marathon.javaagent.components.PropertyHelper;
import net.sourceforge.marathon.javaagent.components.jide.CheckBoxListDemo;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

public class RJideCheckBoxListTest extends RComponentTest {
    protected JFrame frame;
    private CheckBoxList list;

    @BeforeMethod
    public void showDialog() throws Throwable {
        System.setProperty("marathon.mode", "recording");
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                frame = new JFrame("CheckBoxListDemo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                CheckBoxListDemo panel = new CheckBoxListDemo();
                frame.getContentPane().add(panel);
                frame.pack();
                frame.setAlwaysOnTop(true);
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
    public void selectNoSelection() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                list = (CheckBoxList) ComponentUtils.findComponent(CheckBoxList.class, frame);
                list.setSelectedIndices(new int[0]);
                RList rList = new RList(list, null, null, lr);
                rList.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("[]", call.getState());
    }

    @Test
    public void selectSingleItemSelection() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                list = (CheckBoxList) ComponentUtils.findComponent(CheckBoxList.class, frame);
                Rectangle cb = list.getCellBounds(1, 1);
                int hotspot = new JCheckBox().getPreferredSize().width;
                RList rList = new RList(list, null, new Point(cb.x + hotspot + 10, cb.y + 2), lr);
                rList.mouseButton1Pressed(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("click", call.getFunction());
        AssertJUnit.assertEquals("China", call.getCellinfo());
    }

    @Test
    public void selectMultipleItemSelection() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                list = (CheckBoxList) ComponentUtils.findComponent(CheckBoxList.class, frame);
                list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                list.setSelectedIndices(new int[] { 0, 2 });
                RList rList = new RList(list, null, null, lr);
                rList.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("[India, USA]", call.getState());
    }

    @Test
    public void selectSpecialItemSelection() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                list = (CheckBoxList) ComponentUtils.findComponent(CheckBoxList.class, frame);
                DefaultListModel model = (DefaultListModel) list.getModel();
                model.set(0, "\\ Special Characters ([]\\,)");
                Rectangle cb = list.getCellBounds(0, 0);
                int hotspot = new JCheckBox().getPreferredSize().width;
                RList rList = new RList(list, null, new Point(cb.x + hotspot + 10, cb.y + 2), lr);
                rList.mouseButton1Pressed(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("click", call.getFunction());
        AssertJUnit.assertEquals("\\ Special Characters ([]\\,)", call.getCellinfo());
    }

    @Test
    public void listDuplicates() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                list = (CheckBoxList) ComponentUtils.findComponent(CheckBoxList.class, frame);
                list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                list.setSelectedIndices(new int[] { 1, 2 });
                RList rList = new RList(list, null, null, lr);
                rList.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("[China, USA]", call.getState());

        siw(new Runnable() {
            @Override
            public void run() {
                list = (CheckBoxList) ComponentUtils.findComponent(CheckBoxList.class, frame);
                list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                DefaultListModel model = (DefaultListModel) list.getModel();
                model.set(2, "India");
                list.setSelectedIndices(new int[] { 0, 2 });
                RList rList = new RList(list, null, null, lr);
                rList.focusLost(null);
            }
        });
        Call calll = lr.getCall();
        AssertJUnit.assertEquals("select", calll.getFunction());
        AssertJUnit.assertEquals("[India, India(1)]", calll.getState());
    }

    @Test
    public void listMultipleDuplicates() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                list = (CheckBoxList) ComponentUtils.findComponent(CheckBoxList.class, frame);
                list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                list.setSelectedIndices(new int[] { 1, 2 });
                RList rList = new RList(list, null, null, lr);
                rList.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("[China, USA]", call.getState());

        siw(new Runnable() {
            @Override
            public void run() {
                list = (CheckBoxList) ComponentUtils.findComponent(CheckBoxList.class, frame);
                DefaultListModel model = (DefaultListModel) list.getModel();
                model.set(0, "China");
                model.set(1, "China");
                model.set(2, "China");
                list.setSelectedIndices(new int[] { 0, 1, 2 });
                RList rList = new RList(list, null, null, lr);
                rList.focusLost(null);
            }
        });
        Call calll = lr.getCall();
        AssertJUnit.assertEquals("select", calll.getFunction());
        AssertJUnit.assertEquals("[China, China(1), China(2)]", calll.getState());
    }

    @Test
    public void propertyhelperWorksOk() {
        Properties p = new Properties();
        p.setProperty("listText", " Hello");
        String r = PropertyHelper.toString(new Properties[] { p }, new String[] { "listText" });
        AssertJUnit.assertEquals("[\\ Hello]", r);
    }

    @Test
    public void assertContent() throws Throwable {
        siw(new Runnable() {
            @Override
            public void run() {
                list = (CheckBoxList) ComponentUtils.findComponent(CheckBoxList.class, frame);
            }
        });
        final RList rList = new RList(list, null, null, new LoggingRecorder());
        final Object[] content = new Object[] { null };
        siw(new Runnable() {
            @Override
            public void run() {
                content[0] = rList.getContent();
            }
        });
        JSONArray a = new JSONArray(content[0]);
        AssertJUnit.assertEquals(
                "[[\"India\",\"China\",\"USA\",\"UAE\",\"UK\",\"Australia\",\"Afghanistan\",\"Albania\",\"Antarctica\"]]",
                a.toString());
    }

    @Test
    public void assertContentDuplicates() throws Throwable {
        siw(new Runnable() {
            @Override
            public void run() {
                list = (CheckBoxList) ComponentUtils.findComponent(CheckBoxList.class, frame);
                DefaultListModel model = (DefaultListModel) list.getModel();
                model.set(2, "China");
                list.setSelectedIndices(new int[] { 2 });
            }
        });
        final RList rList = new RList(list, null, null, new LoggingRecorder());
        final Object[] content = new Object[] { null };
        siw(new Runnable() {
            @Override
            public void run() {
                content[0] = rList.getContent();
            }
        });
        JSONArray a = new JSONArray(content[0]);
        AssertJUnit.assertEquals(
                "[[\"India\",\"China\",\"China(1)\",\"UAE\",\"UK\",\"Australia\",\"Afghanistan\",\"Albania\",\"Antarctica\"]]",
                a.toString());
    }

    @Test
    public void selectListItemCheckBox() throws InterruptedException {

        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                list = (CheckBoxList) ComponentUtils.findComponent(CheckBoxList.class, frame);
                Point point = getLocationOfCheckBox(list, 2);
                RList rList = new RList(list, null, point, lr);
                list.setCheckBoxListSelectedIndex(2);
                rList.focusLost(null);
            }

        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("true", call.getState());
    }

    private Point getLocationOfCheckBox(JList list, int index) {
        Point point = list.indexToLocation(index);
        Component[] childern = ((CheckBoxListCellRenderer) list.getCellRenderer()).getComponents();
        Checkbox cb;
        for (Component component : childern) {
            if (component instanceof Checkbox) {
                cb = (Checkbox) component;
                Rectangle bounds = cb.getBounds();
                point.x = point.x + bounds.x;
                point.y = point.y + bounds.y;
                break;
            }
        }
        return point;
    }
}
