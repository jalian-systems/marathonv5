package net.sourceforge.marathon.javaagent.components;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

import org.json.JSONArray;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test public class JListJavaElementTest extends JavaElementTest {

    private JavaAgent driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JListJavaElementTest.class.getName());
                frame.setName("dialog-1");
                DefaultListModel model = new DefaultListModel();
                for (int i = 1; i <= 30; i++) {
                    model.addElement("List Item - " + i);
                }
                JList list = new JList(model);
                list.setName("list-1");
                frame.getContentPane().add(list);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });
        driver = new JavaAgent();
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    public void cssSelector() throws Throwable {
        IJavaElement list = driver.findElementByName("list-1");
        JSONArray a = new JSONArray();
        for (int i = 1; i <= 30; i++)
            a.put("List Item - " + i);
        JSONArray b = new JSONArray();
        b.put(a);
        AssertJUnit.assertEquals(b.toString(), list.getAttribute("content"));
        IJavaElement listItem;
        List<IJavaElement> listItems;
        listItem = driver.findElementByCssSelector("#list-1::nth-item(1)");
        AssertJUnit.assertEquals("List Item - 1", listItem.getText());
        listItems = driver.findElementsByCssSelector("#list-1::all-items");
        AssertJUnit.assertEquals(30, listItems.size());
        for (int i = 0; i < 30; i++)
            AssertJUnit.assertEquals("List Item - " + (i + 1), listItems.get(i).getText());
        List<IJavaElement> firstItem = driver.findElementsByCssSelector("#list-1::all-items[text='List Item - 1']");
        AssertJUnit.assertEquals(1, firstItem.size());
        AssertJUnit.assertEquals("List Item - 1", firstItem.get(0).getText());
    }

    public void selectForNoCells() {
        IJavaElement list = driver.findElementByName("list-1");
        marathon_select(list, "[]");
        String attribute = list.getAttribute("selectedIndices");
        AssertJUnit.assertEquals("[]", attribute);
    }

    public void selectForSingleItem() {
        IJavaElement list = driver.findElementByName("list-1");
        marathon_select(list, "[List Item - 1]");
        String attribute = list.getAttribute("selectedIndices");
        AssertJUnit.assertEquals("[0]", attribute);
    }

    public void selectForMultipleItems() {
        IJavaElement list = driver.findElementByName("list-1");
        marathon_select(list, "[List Item - 1, List Item - 2]");
        String attribute = list.getAttribute("selectedIndices");
        AssertJUnit.assertEquals("[0, 1]", attribute);
    }

    public void selectForDuplicateItems() {
        IJavaElement listItem;
        String attribute;
        IJavaElement list = driver.findElementByName("list-1");
        marathon_select(list, "[List Item - 1]");
        attribute = list.getAttribute("selectedIndices");
        AssertJUnit.assertEquals("[0]", attribute);
        listItem = driver.findElementByCssSelector("#list-1::nth-item(1)");
        AssertJUnit.assertEquals("List Item - 1", listItem.getText());

        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                JList jlist = (JList) ComponentUtils.findComponent(JList.class, frame);
                DefaultListModel model = (DefaultListModel) jlist.getModel();
                model.set(2, "List Item - 1");
            }
        });

        marathon_select(list, "[List Item - 1(1)]");
        attribute = list.getAttribute("selectedIndices");
        AssertJUnit.assertEquals("[2]", attribute);
        listItem = driver.findElementByCssSelector("#list-1::nth-item(3)");
        AssertJUnit.assertEquals("List Item - 1(1)", listItem.getText());
    }

    public void selectForMultipleDuplicates() {
        IJavaElement listItem;
        String attribute;
        IJavaElement list = driver.findElementByName("list-1");
        marathon_select(list, "[List Item - 1]");
        attribute = list.getAttribute("selectedIndices");
        AssertJUnit.assertEquals("[0]", attribute);
        listItem = driver.findElementByCssSelector("#list-1::nth-item(1)");
        AssertJUnit.assertEquals("List Item - 1", listItem.getText());

        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                JList jlist = (JList) ComponentUtils.findComponent(JList.class, frame);
                DefaultListModel model = (DefaultListModel) jlist.getModel();
                model.set(2, "List Item - 1");
                model.set(3, "List Item - 1");
            }
        });

        marathon_select(list, "[List Item - 1(1)]");
        attribute = list.getAttribute("selectedIndices");
        AssertJUnit.assertEquals("[2]", attribute);
        listItem = driver.findElementByCssSelector("#list-1::nth-item(3)");
        AssertJUnit.assertEquals("List Item - 1(1)", listItem.getText());
        marathon_select(list, "[List Item - 1(2)]");
        attribute = list.getAttribute("selectedIndices");
        AssertJUnit.assertEquals("[3]", attribute);
        listItem = driver.findElementByCssSelector("#list-1::nth-item(4)");
        AssertJUnit.assertEquals("List Item - 1(2)", listItem.getText());
    }

    public void assertContent() {
        IJavaElement list = driver.findElementByName("list-1");
        String expected = "[[\"List Item - 1\",\"List Item - 2\",\"List Item - 3\",\"List Item - 4\",\"List Item - 5\",\"List Item - 6\",\"List Item - 7\",\"List Item - 8\",\"List Item - 9\",\"List Item - 10\",\"List Item - 11\",\"List Item - 12\",\"List Item - 13\",\"List Item - 14\",\"List Item - 15\",\"List Item - 16\",\"List Item - 17\",\"List Item - 18\",\"List Item - 19\",\"List Item - 20\",\"List Item - 21\",\"List Item - 22\",\"List Item - 23\",\"List Item - 24\",\"List Item - 25\",\"List Item - 26\",\"List Item - 27\",\"List Item - 28\",\"List Item - 29\",\"List Item - 30\"]]";
        AssertJUnit.assertEquals(expected, list.getAttribute("content"));
    }

    public void assertContentWithDuplicates() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                JList jlist = (JList) ComponentUtils.findComponent(JList.class, frame);
                DefaultListModel model = (DefaultListModel) jlist.getModel();
                model.set(2, "List Item - 1");
            }
        });
        IJavaElement list = driver.findElementByName("list-1");
        String expected = "[[\"List Item - 1\",\"List Item - 2\",\"List Item - 1(1)\",\"List Item - 4\",\"List Item - 5\",\"List Item - 6\",\"List Item - 7\",\"List Item - 8\",\"List Item - 9\",\"List Item - 10\",\"List Item - 11\",\"List Item - 12\",\"List Item - 13\",\"List Item - 14\",\"List Item - 15\",\"List Item - 16\",\"List Item - 17\",\"List Item - 18\",\"List Item - 19\",\"List Item - 20\",\"List Item - 21\",\"List Item - 22\",\"List Item - 23\",\"List Item - 24\",\"List Item - 25\",\"List Item - 26\",\"List Item - 27\",\"List Item - 28\",\"List Item - 29\",\"List Item - 30\"]]";
        AssertJUnit.assertEquals(expected, list.getAttribute("content"));
    }

    public void assertContentWithMultipleDuplicates() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                JList jlist = (JList) ComponentUtils.findComponent(JList.class, frame);
                DefaultListModel model = (DefaultListModel) jlist.getModel();
                model.set(2, "List Item - 1");
                model.set(3, "List Item - 1");
            }
        });
        IJavaElement list = driver.findElementByName("list-1");
        String expected = "[[\"List Item - 1\",\"List Item - 2\",\"List Item - 1(1)\",\"List Item - 1(2)\",\"List Item - 5\",\"List Item - 6\",\"List Item - 7\",\"List Item - 8\",\"List Item - 9\",\"List Item - 10\",\"List Item - 11\",\"List Item - 12\",\"List Item - 13\",\"List Item - 14\",\"List Item - 15\",\"List Item - 16\",\"List Item - 17\",\"List Item - 18\",\"List Item - 19\",\"List Item - 20\",\"List Item - 21\",\"List Item - 22\",\"List Item - 23\",\"List Item - 24\",\"List Item - 25\",\"List Item - 26\",\"List Item - 27\",\"List Item - 28\",\"List Item - 29\",\"List Item - 30\"]]";
        AssertJUnit.assertEquals(expected, list.getAttribute("content"));
    }
}
