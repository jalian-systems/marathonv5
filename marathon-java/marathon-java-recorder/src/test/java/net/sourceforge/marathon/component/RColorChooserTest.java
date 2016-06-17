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

import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.component.RColorChooser;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test public class RColorChooserTest extends RComponentTest {
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame("My Dialog");
                frame.setName("dialog-1");
                JColorChooser colorChooser = new JColorChooser();
                frame.getContentPane().add(colorChooser);
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

    public void colorChooserWithValidRgbValues() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                JColorChooser chooser = (JColorChooser) ComponentUtils.findComponent(JColorChooser.class, frame);
                chooser.setColor(0xba, 0x55, 0xd3);
                RColorChooser rColorChooser = new RColorChooser(chooser, null, null, lr);
                rColorChooser.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("#ba55d3", call.getState());
    }

    public void colorChooserWithColorName() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                JColorChooser chooser = (JColorChooser) ComponentUtils.findComponent(JColorChooser.class, frame);
                chooser.setColor(Color.red);
                RColorChooser rColorChooser = new RColorChooser(chooser, null, null, lr);
                rColorChooser.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("#ff0000", call.getState());
    }

    public void colorChooserWithIntValue() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                JColorChooser chooser = (JColorChooser) ComponentUtils.findComponent(JColorChooser.class, frame);
                chooser.setColor(0xFFFF00);
                RColorChooser rColorChooser = new RColorChooser(chooser, null, null, lr);
                rColorChooser.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("#ffff00", call.getState());
    }
}
