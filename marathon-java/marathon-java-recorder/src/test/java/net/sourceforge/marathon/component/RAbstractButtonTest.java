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
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;

import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.DynamicTreeDemo;

@Test public class RAbstractButtonTest extends RComponentTest {

    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        siw(new Runnable() {
            @Override public void run() {
                frame = new JFrame(RAbstractButtonTest.class.getSimpleName());
                frame.setName("frame-" + RAbstractButtonTest.class.getSimpleName());
                frame.getContentPane().add(new DynamicTreeDemo(), BorderLayout.CENTER);
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

    public void click() throws Throwable, InvocationTargetException {
        final JButton button = (JButton) ComponentUtils.findComponent(JButton.class, frame);
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                RAbstractButton rButton = new RAbstractButton(button, null, null, lr);
                MouseEvent me = new MouseEvent(button, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 5, 5, 1, false,
                        MouseEvent.BUTTON1);
                rButton.mouseButton1Pressed(me);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("click", call.getFunction());
        AssertJUnit.assertEquals("", call.getState());
    }
}
