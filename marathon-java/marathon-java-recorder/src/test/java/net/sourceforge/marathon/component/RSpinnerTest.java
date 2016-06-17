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

import java.awt.BorderLayout;
import java.awt.Component;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.testhelpers.MissingException;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.SpinnerDemo2;

@Test public class RSpinnerTest extends RComponentTest {
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(RSpinnerTest.class.getSimpleName());
                frame.setName("frame-" + RSpinnerTest.class.getSimpleName());
                frame.getContentPane().add(new SpinnerDemo2(), BorderLayout.CENTER);
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

    public void listSpinner() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                List<Component> spinnerComponents = ComponentUtils.findComponents(JSpinner.class, frame);
                JSpinner listSpinner = (JSpinner) (spinnerComponents.get(0));
                RSpinner rSpinner = new RSpinner(listSpinner, null, null, lr);
                rSpinner.focusGained(null);
                listSpinner.setValue("March");
                rSpinner.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("March", call.getState());
    }

    public void listSpinnerWithInvalidValue() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        final Exception[] exc = new Exception[] { null };
        siw(new Runnable() {
            @Override public void run() {
                List<Component> spinnerComponents = ComponentUtils.findComponents(JSpinner.class, frame);
                JSpinner listSpinner = (JSpinner) (spinnerComponents.get(0));
                RSpinner rSpinner = new RSpinner(listSpinner, null, null, lr);
                rSpinner.focusGained(null);
                try {
                    listSpinner.setValue("Ostrich\"");
                    Call call = lr.getCall();
                    AssertJUnit.assertEquals("select", call.getFunction());
                    AssertJUnit.assertEquals("Ostrich\"", call.getState());
                    exc[0] = new MissingException(IllegalArgumentException.class);
                } catch (IllegalArgumentException e) {
                }
                rSpinner.focusLost(null);
            }
        });
        if (exc[0] != null)
            throw exc[0];
    }

    public void numberSpinner() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                List<Component> spinnerComponents = ComponentUtils.findComponents(JSpinner.class, frame);
                JSpinner numberSpinner = (JSpinner) (spinnerComponents.get(1));
                RSpinner rSpinner = new RSpinner(numberSpinner, null, null, lr);
                rSpinner.focusGained(null);
                numberSpinner.setValue(new Integer(2000));
                rSpinner.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("2000", call.getState());
    }

    public void dateSpinner() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                List<Component> spinnerComponents = ComponentUtils.findComponents(JSpinner.class, frame);
                JSpinner dateSpinner = (JSpinner) (spinnerComponents.get(2));
                RSpinner rSpinner = new RSpinner(dateSpinner, null, null, lr);
                rSpinner.focusGained(null);
                String dateString = "04/2014";
                JComponent spinnerEditor = dateSpinner.getEditor();
                try {
                    Date date = ((DateEditor) spinnerEditor).getFormat().parse(dateString);
                    dateSpinner.setValue(date);
                    rSpinner.focusLost(null);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("04/2014", call.getState());
    }
}
