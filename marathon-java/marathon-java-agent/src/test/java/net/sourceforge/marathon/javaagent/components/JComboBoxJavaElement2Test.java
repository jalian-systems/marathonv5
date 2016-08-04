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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;

@Test public class JComboBoxJavaElement2Test extends JavaElementTest {

    protected JFrame frame;
    private IJavaAgent driver;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JComboBoxJavaElement2Test.class.getName());
                frame.setName("dialog-1");
                Employee[] items = { new Employee("Phillip"), new Employee("Larry"), new Employee("Lisa"), new Employee("James"),
                        new Employee("Larry") };
                MyComboBoxModel model = new MyComboBoxModel(items);
                JComboBox comboBox = new JComboBox(model);
                comboBox.setEditable(true);
                comboBox.setName("Employee");
                frame.getContentPane().add(comboBox);
                frame.pack();
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

    public void editorSelection() {
        IJavaElement comboBox = driver.findElementByTagName("combo-box");
        marathon_select(comboBox, "Kate");
        IJavaElement editor = comboBox.findElementByTagName("text-field");
        String attribute = editor.getAttribute("text");
        AssertJUnit.assertEquals("Kate", attribute);
    }

    public void duplicateSelection() {
        IJavaElement comboBox = driver.findElementByTagName("combo-box");
        marathon_select(comboBox, "Larry(1)");
        IJavaElement editor = comboBox.findElementByTagName("text-field");
        String attribute = editor.getAttribute("text");
        String selectedIndex = comboBox.getAttribute("selectedIndex");
        AssertJUnit.assertEquals("Larry", attribute);
        AssertJUnit.assertEquals(4, Integer.parseInt(selectedIndex));
    }

    public void defaultSelection() {
        IJavaElement comboBox = driver.findElementByTagName("combo-box");
        marathon_select(comboBox, "Phillip");
        String attribute = comboBox.getAttribute("selectedItem");
        AssertJUnit.assertEquals("Phillip", attribute);
    }

    public void selectionOption() {
        IJavaElement comboBox = driver.findElementByTagName("combo-box");
        marathon_select(comboBox, "Lisa");
        String attribute = comboBox.getAttribute("selectedItem");
        AssertJUnit.assertEquals("Lisa", attribute);
    }
}

class MyComboBoxModel extends DefaultComboBoxModel {
    private static final long serialVersionUID = 1L;

    public MyComboBoxModel(Employee[] items) {
        super(items);
    }

    @Override public Employee getSelectedItem() {
        Object selectedItem = super.getSelectedItem();
        if (selectedItem instanceof Employee) {
            return (Employee) selectedItem;
        }
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
