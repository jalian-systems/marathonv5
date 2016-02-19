package net.sourceforge.marathon.component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

import org.json.JSONArray;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test public class RComboBox2Test extends RComponentTest {
    protected JFrame frame;
    private JComboBox combo;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(RComboBox2Test.class.getName());
                frame.setName("dialog-1");
                Employee[] items = { new Employee("Phillip"), new Employee("Larry"), new Employee("Lisa"), new Employee("James"),
                        new Employee("Larry") };
                MyComboBoxModel model = new MyComboBoxModel(items);
                JComboBox comboBox = new JComboBox(model);
                comboBox.setName("Employee");
                frame.getContentPane().add(comboBox);
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

    public void selectDuplicateOption() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                combo = (JComboBox) ComponentUtils.findComponent(JComboBox.class, frame);
                RComboBox rCombo = new RComboBox(combo, null, null, lr);
                combo.setSelectedIndex(1);
                rCombo.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("Larry", call.getState());

        siw(new Runnable() {
            @Override public void run() {
                combo = (JComboBox) ComponentUtils.findComponent(JComboBox.class, frame);
                RComboBox rCombo = new RComboBox(combo, null, null, lr);
                combo.setSelectedIndex(4);
                rCombo.focusLost(null);
            }
        });
        Call calll = lr.getCall();
        AssertJUnit.assertEquals("select", calll.getFunction());
        AssertJUnit.assertEquals("Larry(1)", calll.getState());
    }

    public void assertContentDuplicates() throws InterruptedException {
        siw(new Runnable() {
            @Override public void run() {
                combo = (JComboBox) ComponentUtils.findComponent(JComboBox.class, frame);
            }
        });
        final RComboBox rCombo = new RComboBox(combo, null, null, new LoggingRecorder());
        final Object[] content = new Object[] { null };
        siw(new Runnable() {
            @Override public void run() {
                content[0] = rCombo.getContent();
            }
        });
        JSONArray a = new JSONArray(content[0]);
        AssertJUnit.assertEquals("[[\"Phillip\",\"Larry\",\"Lisa\",\"James\",\"Larry(1)\"]]", a.toString());
    }

    public void assertEditorSelection() throws InterruptedException {
        siw(new Runnable() {
            @Override public void run() {
                combo = (JComboBox) ComponentUtils.findComponent(JComboBox.class, frame);
                combo.setEditable(true);
            }
        });
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                combo = (JComboBox) ComponentUtils.findComponent(JComboBox.class, frame);
                RComboBox rCombo = new RComboBox(combo, null, null, lr);
                ((JTextField) ((JComboBox) combo).getEditor().getEditorComponent()).setText("Kate");
                rCombo.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("Kate", call.getState());
    }

    public void assertEditorSelectionWithEmptyText() throws InterruptedException {
        siw(new Runnable() {
            @Override public void run() {
                combo = (JComboBox) ComponentUtils.findComponent(JComboBox.class, frame);
                combo.setEditable(true);
            }
        });
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                combo = (JComboBox) ComponentUtils.findComponent(JComboBox.class, frame);
                RComboBox rCombo = new RComboBox(combo, null, null, lr);
                ((JTextField) ((JComboBox) combo).getEditor().getEditorComponent()).setText("");
                rCombo.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("", call.getState());
    }
}

class MyComboBoxModel extends DefaultComboBoxModel {
    private static final long serialVersionUID = 1L;

    public MyComboBoxModel(Employee[] items) {
        super(items);
    }

    @Override public Employee getSelectedItem() {
        Object selectedItem = super.getSelectedItem();
        if (selectedItem instanceof Employee)
            return (Employee) selectedItem;
        return null;
    }
}

class Employee {
    private String name;

    public Employee(String name) {
        this.name = name;
    }

    @Override public String toString() {
        return name;
    }
}
