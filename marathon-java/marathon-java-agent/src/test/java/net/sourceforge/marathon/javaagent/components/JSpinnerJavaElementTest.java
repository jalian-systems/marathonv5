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
package net.sourceforge.marathon.javaagent.components;

import java.awt.FlowLayout;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.javaagent.Wait;

@Test public class JSpinnerJavaElementTest extends JavaElementTest {

    private IJavaAgent driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame("My Dialog");
                frame.setName("dialog-1");
                JSpinner listSpinner = createListSpinner();
                Calendar calendar = Calendar.getInstance();
                JSpinner numberSpinner = createNumberSpinner(calendar);
                JSpinner dateSpinner = createDateSpinner(calendar);

                frame.setLayout(new FlowLayout());
                frame.getContentPane().add(listSpinner);
                frame.getContentPane().add(numberSpinner);
                frame.getContentPane().add(dateSpinner);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });
        driver = new JavaAgent();
    }

    private JSpinner createListSpinner() {
        String[] monthStrings = { "January", "February", "March", "April" };
        SpinnerListModel spinnerListModel = new SpinnerListModel(monthStrings);
        JSpinner listSpinner = new JSpinner(spinnerListModel);
        listSpinner.setName("list-spinner");
        return listSpinner;
    }

    private JSpinner createNumberSpinner(Calendar calendar) {
        int currentYear = calendar.get(Calendar.YEAR);
        SpinnerModel yearModel = new SpinnerNumberModel(currentYear, currentYear - 100, currentYear + 100, 1);
        JSpinner numberSpinner = new JSpinner(yearModel);
        numberSpinner.setEditor(new JSpinner.NumberEditor(numberSpinner, "#"));
        numberSpinner.setName("number-spinner");
        return numberSpinner;
    }

    private JSpinner createDateSpinner(Calendar calendar) {
        Date initDate = calendar.getTime();
        calendar.add(Calendar.YEAR, -100);
        Date earliestDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 200);
        Date latestDate = calendar.getTime();
        SpinnerDateModel spinnerDateModel = new SpinnerDateModel(initDate, earliestDate, latestDate, Calendar.YEAR);
        JSpinner dateSpinner = new JSpinner(spinnerDateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "MM/yyyy"));
        dateSpinner.setName("date-spinner");
        return dateSpinner;
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        JavaElementFactory.reset();
    }

    public void cssSelectorEditor() throws Throwable {
        IJavaElement listEditor = driver.findElementByCssSelector("#list-spinner::editor");
        AssertJUnit.assertEquals("January", listEditor.getText());
    }

    public void listSpinner() throws UnsupportedEncodingException {
        IJavaElement listSpinner = driver.findElementByName("list-spinner");
        marathon_select(listSpinner, "March");
        String attribute = listSpinner.getAttribute("text");
        AssertJUnit.assertEquals("March", attribute);
    }

    public void listSpinnerWithInvalidValue() throws UnsupportedEncodingException {
        final IJavaElement listSpinner = driver.findElementByName("list-spinner");
        marathon_select(listSpinner, "Mar/");
        driver.findElementByName("date-spinner").click();
        new Wait("ListSpinner value was not reset") {
            @Override public boolean until() {
                String attribute = listSpinner.getAttribute("text");
                return attribute.equals("January");
            }
        };
        String attribute = listSpinner.getAttribute("text");
        AssertJUnit.assertEquals("January", attribute);
    }

    public void numberSpinner() {
        IJavaElement numberSpinner = driver.findElementByName("number-spinner");
        marathon_select(numberSpinner, "3000");
        String attribute = numberSpinner.getAttribute("text");
        AssertJUnit.assertEquals("3000", attribute);
    }

    public void dateSpinner() {
        IJavaElement dateSpinner = driver.findElementByName("date-spinner");
        marathon_select(dateSpinner, "08/2000");
        String attribute = dateSpinner.getAttribute("text");
        AssertJUnit.assertEquals("08/2000", attribute);
    }
}
