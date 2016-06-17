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

import java.lang.reflect.InvocationTargetException;

import javax.swing.JTextField;

import net.sourceforge.marathon.testhelpers.ComponentUtils;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.TextFieldDemo;

@Test public class RTextComponentTest extends RComponentTest {
    protected TextFieldDemo frame;
    protected RComponent rTextComponent;

    @BeforeMethod public void showDialog() throws Throwable {
        siw(new Runnable() {
            @Override public void run() {
                frame = new TextFieldDemo();
                frame.setName("frame-" + RTextComponentTest.class.getSimpleName());
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        siw(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    public void select() throws InterruptedException, InvocationTargetException {
        final JTextField tf;
        tf = (JTextField) ComponentUtils.findComponent(JTextField.class, frame);
        siw(new Runnable() {
            @Override public void run() {
                tf.setText("Hello World");
            }
        });
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                rTextComponent = new RTextComponent(tf, null, null, lr);
                rTextComponent.focusLost(null);
            }
        });
        net.sourceforge.marathon.component.LoggingRecorder.Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("Hello World", call.getState());
    }

    public void selectWithSpecialChars() throws InterruptedException, InvocationTargetException {
        final JTextField tf;
        tf = (JTextField) ComponentUtils.findComponent(JTextField.class, frame);
        siw(new Runnable() {
            @Override public void run() {
                tf.setText("Hello World'\"");
            }
        });
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                rTextComponent = new RTextComponent(tf, null, null, lr);
                rTextComponent.focusLost(null);
            }
        });

        net.sourceforge.marathon.component.LoggingRecorder.Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("Hello World'\"", call.getState());
    }

    public void selectWithUtf8Chars() throws InterruptedException, InvocationTargetException {
        final JTextField tf;
        tf = (JTextField) ComponentUtils.findComponent(JTextField.class, frame);
        siw(new Runnable() {
            @Override public void run() {
                tf.setText("å∫ç∂´ƒ©˙ˆ∆");
            }
        });
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                rTextComponent = new RTextComponent(tf, null, null, lr);
                rTextComponent.focusLost(null);
            }
        });
        net.sourceforge.marathon.component.LoggingRecorder.Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("å∫ç∂´ƒ©˙ˆ∆", call.getState());
    }
}
