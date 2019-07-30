package net.sourceforge.marathon.component.jide;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.JideSplitPane;

import net.sourceforge.marathon.component.LoggingRecorder;
import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.component.RComponentTest;
import net.sourceforge.marathon.javaagent.Wait;
import net.sourceforge.marathon.javaagent.components.jide.JideSplitPaneDemo;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

public class RJideSplitPaneTest extends RComponentTest {
    protected JFrame frame;

    @BeforeMethod
    public void showDialog() throws Throwable {
        System.setProperty("marathon.mode", "recording");
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                frame = new JFrame("JideSplitPaneDemo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JideSplitPaneDemo panel = new JideSplitPaneDemo();
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
    public void getText() {
        final int location = 250;
        final JideSplitPane splitpane = (JideSplitPane) ComponentUtils.findComponent(JideSplitPane.class, frame);
        LoggingRecorder lr = new LoggingRecorder();
        final RJideSplitPaneElement rSplitPane = new RJideSplitPaneElement(splitpane, null, null, lr);
        final List<String> text = new ArrayList<String>();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                splitpane.setDividerLocation(0, location);
                rSplitPane.mouseReleased(null);
                text.add(rSplitPane.getAttribute("text"));
            }
        });
        new Wait("Waiting for Jide Splitpane text.") {
            @Override
            public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertTrue(text.get(0).matches("\\[" + location + "\\,.*"));
    }

    @Test
    public void selectSplitPane() {
        JideSplitPane splitpane = (JideSplitPane) ComponentUtils.findComponent(JideSplitPane.class, frame);
        LoggingRecorder lr = new LoggingRecorder();
        RJideSplitPaneElement rfxSplitPane = new RJideSplitPaneElement(splitpane, null, null, lr);
        splitpane.setDividerLocation(0, 250);
        rfxSplitPane.mouseReleased(null);

        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals(250, new JSONArray((String) call.getState()).getInt(0));
    }

}
