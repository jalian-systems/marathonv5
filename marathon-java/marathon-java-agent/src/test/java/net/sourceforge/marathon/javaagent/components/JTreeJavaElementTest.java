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

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.DynamicTreeDemo;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;

@Test public class JTreeJavaElementTest extends JavaElementTest {
    protected JFrame frame;
    private IJavaAgent driver;

    @BeforeMethod public void showDialog() throws Throwable {
        siw(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JTreeJavaElementTest.class.getSimpleName());
                frame.setName("frame-" + JTreeJavaElementTest.class.getSimpleName());
                frame.getContentPane().add(new DynamicTreeDemo(), BorderLayout.CENTER);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });
        driver = new JavaAgent();
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        siw(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    public void assertContent() {
        IJavaElement tree = driver.findElementByTagName("tree");
        String expected = "[[\"Root Node\",\"Parent 1\",\"Child 1\",\"Child 2\",\"Parent 2\",\"Child 1\",\"Child 2\"]]";
        AssertJUnit.assertEquals(expected, tree.getAttribute("content"));
    }
}
