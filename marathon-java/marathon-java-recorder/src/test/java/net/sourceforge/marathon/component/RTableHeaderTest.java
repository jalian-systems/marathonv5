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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.json.JSONArray;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sun.swingset3.demos.table.TableDemo;

import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.javaagent.Device;
import net.sourceforge.marathon.javaagent.IDevice;
import net.sourceforge.marathon.javaagent.IDevice.Buttons;
import net.sourceforge.marathon.javaagent.Wait;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

@Test
public class RTableHeaderTest extends RComponentTest {

    protected JFrame frame;
    private JTableHeader tableHeader;
    private Point p;

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame = new JFrame(RTableHeaderTest.class.getSimpleName());
                frame.setName("frame-" + RTableHeaderTest.class.getSimpleName());
                TableDemo demo = new TableDemo();
                demo.start();
                frame.getContentPane().add(demo, BorderLayout.CENTER);
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

    public void tableHeader() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                tableHeader = (JTableHeader) ComponentUtils.findComponent(JTableHeader.class, frame);
                int columnIndex = getIndexFromHeader("Award Category");
                Rectangle rect = tableHeader.getHeaderRect(columnIndex);
                p = new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
            }
        });

        tableHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                RTableHeader rtableHeader = new RTableHeader(tableHeader, null, e.getPoint(), lr);
                rtableHeader.mouseButton1Pressed(e);
            }
        });

        IDevice device = Device.getDevice();
        device.click(tableHeader, Buttons.LEFT, 1, p.x, p.y);

        new Wait("Waiting for logging recorder callback") {
            @Override
            public boolean until() {
                return lr.getCalls().size() > 0;
            }
        };

        Call call = lr.getCall();
        AssertJUnit.assertEquals("click", call.getFunction());
        AssertJUnit.assertEquals("Award Category", call.getCellinfo());
    }

    public void tableHeaderDuplicates() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                tableHeader = (JTableHeader) ComponentUtils.findComponent(JTableHeader.class, frame);
                JTable table = (JTable) ComponentUtils.findComponent(JTable.class, frame);
                table.getColumnModel().getColumn(1).setHeaderValue("Movie Title");
                int columnIndex = 2; // Award Category
                Rectangle rect = tableHeader.getHeaderRect(columnIndex);
                p = new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
            }
        });

        tableHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                RTableHeader rtableHeader = new RTableHeader(tableHeader, null, e.getPoint(), lr);
                rtableHeader.mouseButton1Pressed(e);
            }
        });

        IDevice device = Device.getDevice();
        device.click(tableHeader, Buttons.LEFT, 1, p.x, p.y);

        new Wait("Waiting for logging recorder callback") {
            @Override
            public boolean until() {
                return lr.getCalls().size() > 0;
            }
        };

        Call call = lr.getCall();
        AssertJUnit.assertEquals("click", call.getFunction());
        AssertJUnit.assertEquals("Movie Title(1)", call.getCellinfo());
    }

    private int getIndexFromHeader(String selectedHeader) {
        TableColumnModel tcm = tableHeader.getColumnModel();
        if (tcm == null) {
            System.out.println("RTableHeaderTest.getIndexFromHeader() : Unable to get columnModel for table header:");
        }
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            TableColumn tableColumn = tcm.getColumn(i);
            if (tableColumn.getIdentifier().equals(selectedHeader)) {
                return i;
            }
        }
        return -1;
    }

    public void assertContent() throws Throwable {
        siw(new Runnable() {
            @Override
            public void run() {
                tableHeader = (JTableHeader) ComponentUtils.findComponent(JTableHeader.class, frame);
            }
        });
        final RTableHeader rtableHeader = new RTableHeader(tableHeader, null, null, new LoggingRecorder());
        final Object[] content = new Object[] { null };
        siw(new Runnable() {
            @Override
            public void run() {
                content[0] = rtableHeader.getContent();
            }
        });
        JSONArray a = new JSONArray(content[0]);
        AssertJUnit.assertEquals("[[\"Year\",\"Award Category\",\"Movie Title\",\"Nominee(s)\"]]", a.toString());
    }

    public void assertContentDuplicates() {
        siw(new Runnable() {
            @Override
            public void run() {
                tableHeader = (JTableHeader) ComponentUtils.findComponent(JTableHeader.class, frame);
                JTable table = (JTable) ComponentUtils.findComponent(JTable.class, frame);
                table.getColumnModel().getColumn(1).setHeaderValue("Movie Title");
            }
        });
        final RTableHeader rtableHeader = new RTableHeader(tableHeader, null, null, new LoggingRecorder());
        final Object[] content = new Object[] { null };
        siw(new Runnable() {
            @Override
            public void run() {
                content[0] = rtableHeader.getContent();
            }
        });
        JSONArray a = new JSONArray(content[0]);
        AssertJUnit.assertEquals("[[\"Year\",\"Movie Title\",\"Movie Title(1)\",\"Nominee(s)\"]]", a.toString());
    }

}
