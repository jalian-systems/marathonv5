package net.sourceforge.marathon.javaagent.components.jide;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.JideSplitPane;

import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.Wait;
import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

public class JideSplitPaneElementTest {

    private JavaAgent driver;
    protected JFrame frame;

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {

                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                frame = new JFrame("JideSplitPaneDemo");
                JideSplitPaneDemo jsdemo = new JideSplitPaneDemo();
                frame.getContentPane().add(jsdemo);
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
    public void getText() throws Throwable {
        JideSplitPane splitPaneNode = (JideSplitPane) ComponentUtils.findComponent(JideSplitPane.class, frame);
        String expected = new JSONArray(splitPaneNode.getDividerLocations()).toString();
        driver = new JavaAgent();
        IJavaElement splitPane = driver.findElementByCssSelector("jide-split-pane");
        String actual = splitPane.getText();
        AssertJUnit.assertEquals(expected, actual);
    }

    @Test
    public void select() {
        final JideSplitPane splitPaneNode = (JideSplitPane) ComponentUtils.findComponent(JideSplitPane.class, frame);
        final JSONArray initialValue = new JSONArray(splitPaneNode.getDividerLocations());
        driver = new JavaAgent();
        final IJavaElement splitPane = driver.findElementByCssSelector("jide-split-pane");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                splitPane.marathon_select("[129]");
            }
        });
        new Wait("Waiting for split pane to set divider location") {
            @Override
            public boolean until() {
                return initialValue.getInt(0) != new JSONArray(splitPaneNode.getDividerLocations()).getInt(0);
            }
        };
        JSONArray pa = new JSONArray(splitPaneNode.getDividerLocations());
        AssertJUnit.assertEquals(129, pa.getInt(0));
    }

    @Test
    public void select2() {
        final JideSplitPane splitPaneNode = (JideSplitPane) ComponentUtils.findComponent(JideSplitPane.class, frame);
        final JSONArray initialValue = new JSONArray(splitPaneNode.getDividerLocations());
        driver = new JavaAgent();
        final IJavaElement splitPane = driver.findElementByCssSelector("jide-split-pane");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                splitPane.marathon_select("[238,450]");
            }
        });
        new Wait("Waiting for split pane to set divider location") {
            @Override
            public boolean until() {
                return initialValue.getInt(1) != new JSONArray(splitPaneNode.getDividerLocations()).getInt(1);
            }
        };
        JSONArray pa = new JSONArray(splitPaneNode.getDividerLocations());
        AssertJUnit.assertEquals(450, pa.getInt(1));
    }
}
