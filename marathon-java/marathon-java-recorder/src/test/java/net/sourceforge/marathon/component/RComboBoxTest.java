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

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sun.swingset3.demos.combobox.ComboBoxDemo;

import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

@Test
public class RComboBoxTest extends RComponentTest {
    protected JFrame frame;
    private JComboBox comboBox;

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame = new JFrame(RComboBoxTest.class.getSimpleName());
                frame.setName("frame-" + RComboBoxTest.class.getSimpleName());
                frame.getContentPane().add(new ComboBoxDemo(), BorderLayout.CENTER);
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

    public void getDefaultSelection() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                comboBox = (JComboBox) ComponentUtils.findComponent(JComboBox.class, frame);
                RComboBox rCombo = new RComboBox(comboBox, null, null, lr);
                comboBox.setSelectedIndex(0);
                rCombo.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("Philip, Howard, Jeff", call.getState());
    }

    public void selectOption() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                comboBox = (JComboBox) ComponentUtils.findComponent(JComboBox.class, frame);
                RComboBox rCombo = new RComboBox(comboBox, null, null, lr);
                comboBox.setSelectedIndex(2);
                rCombo.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("Howard, Scott, Hans", call.getState());
    }

    public void selectOptionWithQuotes() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                comboBox = (JComboBox) ComponentUtils.findComponent(JComboBox.class, frame);
                RComboBox rCombo = new RComboBox(comboBox, null, null, lr);
                comboBox.addItem("James, \"Lisa\", Brent");
                comboBox.setSelectedItem("James, \"Lisa\", Brent");
                rCombo.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("James, \"Lisa\", Brent", call.getState());
    }

    public void htmlOptionSelect() {
        final LoggingRecorder lr = new LoggingRecorder();
        String text = "This is a test text";
        final String htmlText = "<html><font color=\"RED\"><h1><This is also content>" + text + "</h1></html>";
        siw(new Runnable() {
            @Override
            public void run() {
                comboBox = (JComboBox) ComponentUtils.findComponent(JComboBox.class, frame);
                RComboBox rCombo = new RComboBox(comboBox, null, null, lr);
                comboBox.addItem(htmlText);
                comboBox.setSelectedItem(htmlText);
                rCombo.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals(text, call.getState());
    }

    public void assertContent() {
        siw(new Runnable() {
            @Override
            public void run() {
                comboBox = (JComboBox) ComponentUtils.findComponent(JComboBox.class, frame);
            }
        });
        final RComboBox rCombo = new RComboBox(comboBox, null, null, new LoggingRecorder());
        final Object[] content = new Object[] { null };
        siw(new Runnable() {
            @Override
            public void run() {
                content[0] = rCombo.getContent();
            }
        });
        JSONArray a = new JSONArray(content[0]);
        String expected = "[[\"Philip, Howard, Jeff\",\"Jeff, Larry, Philip\",\"Howard, Scott, Hans\",\"Philip, Jeff, Hans\",\"Brent, Jon, Scott\",\"Lara, Larry, Lisa\",\"James, Philip, Michael\",\"Philip, Lisa, Brent\",\"James, Philip, Jon\",\"Lara, Jon, Scott\"]]";
        AssertJUnit.assertEquals(expected, a.toString());
    }
}
