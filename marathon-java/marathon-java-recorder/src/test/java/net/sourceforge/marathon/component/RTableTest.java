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
import java.lang.reflect.Field;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import org.json.JSONArray;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.TableFilterDemo;
import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

@Test public class RTableTest extends RComponentTest {

    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(RTableTest.class.getSimpleName());
                frame.setName("frame-" + RTableTest.class.getSimpleName());
                frame.getContentPane().add(new TableFilterDemo(), BorderLayout.CENTER);
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

    public void selectNoCells() throws Throwable {
        final JTable table = (JTable) ComponentUtils.findComponent(JTable.class, frame);
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                RTable rTable = new RTable(table, null, null, lr);
                rTable.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("", call.getState());
    }

    public void selectAllCells() throws Throwable {
        final JTable table = (JTable) ComponentUtils.findComponent(JTable.class, frame);
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                table.setColumnSelectionAllowed(true);
                table.setRowSelectionAllowed(true);
                table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                table.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                int rowCount = table.getRowCount();
                int colCount = table.getColumnCount();
                table.addRowSelectionInterval(0, rowCount - 1);
                table.addColumnSelectionInterval(0, colCount - 1);
                RTable rTable = new RTable(table, null, null, lr);
                rTable.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("all", call.getState());
    }

    public void selectARow() throws Throwable {
        final JTable table = (JTable) ComponentUtils.findComponent(JTable.class, frame);
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                table.setRowSelectionAllowed(true);
                table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                table.addRowSelectionInterval(1, 1);
                table.addColumnSelectionInterval(0, 0);
                RTable rTable = new RTable(table, null, null, lr);
                rTable.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("rows:[1],columns:[First Name]", call.getState());
    }

    public void selectRows() throws Throwable {
        final JTable table = (JTable) ComponentUtils.findComponent(JTable.class, frame);
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                table.setRowSelectionAllowed(true);
                table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                table.addRowSelectionInterval(1, 1);
                table.addRowSelectionInterval(3, 3);
                table.addColumnSelectionInterval(0, 0);
                RTable rTable = new RTable(table, null, null, lr);
                rTable.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("rows:[1,3],columns:[First Name]", call.getState());
    }

    public void selectWithSpecialCharsIHeader() throws Throwable {
        final JTable table = (JTable) ComponentUtils.findComponent(JTable.class, frame);
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                JTableHeader tableHeader = table.getTableHeader();
                String[] columnNames = new String[table.getColumnCount()];
                for (int i = 0; i < table.getColumnCount(); i++) {
                    columnNames[i] = table.getColumnName(i);
                }
                columnNames[3] = " # of \\Years[0,20]";
                TableModel model = table.getModel();
                try {
                    Field field = model.getClass().getDeclaredField("columnNames");
                    field.setAccessible(true);
                    field.set(model, columnNames);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                table.setModel(model);
                tableHeader.getColumnModel().getColumn(3).setHeaderValue(" # of \\Years[0,20]");
                table.invalidate();
                table.setRowSelectionAllowed(true);
                table.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                table.addRowSelectionInterval(1, 1);
                table.addRowSelectionInterval(3, 3);
                table.addColumnSelectionInterval(3, 3);
                table.addColumnSelectionInterval(2, 2);
                RTable rTable = new RTable(table, null, null, lr);
                rTable.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("rows:[1,3],columns:[Sport, ## of \\Years[0#;20]]", call.getState());
    }

    public void editCell() throws Throwable {
        final JTable table = (JTable) ComponentUtils.findComponent(JTable.class, frame);
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                table.addRowSelectionInterval(2, 2);
                table.addColumnSelectionInterval(2, 2);
                RTable rTable = new RTable(table, null, null, lr);
                rTable.focusGained(null);
                AbstractTableModel model = (AbstractTableModel) table.getModel();
                model.setValueAt("Rowing", 2, 2);
                rTable.focusLost(null);
            }
        });
        List<Call> calls = lr.getCalls();
        Call call = calls.get(1);
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("rows:[2],columns:[Sport]", call.getState());
        call = calls.get(0);
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("Rowing", call.getState());
        AssertJUnit.assertEquals("{2, Sport}", call.getCellinfo());
    }

    public void editCellSpecialChars() throws Throwable {
        final JTable table = (JTable) ComponentUtils.findComponent(JTable.class, frame);
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                String[] columnNames = new String[table.getColumnCount()];
                for (int i = 0; i < table.getColumnCount(); i++) {
                    columnNames[i] = table.getColumnName(i);
                }
                columnNames[3] = " # of \\Years[0,20]";
                TableModel model = table.getModel();
                try {
                    Field field = model.getClass().getDeclaredField("columnNames");
                    field.setAccessible(true);
                    field.set(model, columnNames);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                table.setModel(model);
                table.getTableHeader().getColumnModel().getColumn(3).setHeaderValue(" # of \\Years[0,20]");
                table.invalidate();
                table.addRowSelectionInterval(2, 2);
                table.addColumnSelectionInterval(3, 3);
                RTable rTable = new RTable(table, null, null, lr);
                rTable.focusGained(null);
                model = table.getModel();
                model.setValueAt("100", 2, 3);
                rTable.focusLost(null);
            }
        });
        List<Call> calls = lr.getCalls();
        Call call = calls.get(1);
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("rows:[2],columns:[ ## of \\Years[0#;20]]", call.getState());
        call = calls.get(0);
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("100", call.getState());
        AssertJUnit.assertEquals("{2,  # of \\Years[0,20]}", call.getCellinfo());
    }

    public void assertContent() {
        JTable table = (JTable) ComponentUtils.findComponent(JTable.class, frame);
        final RTable rTable = new RTable(table, null, null, new LoggingRecorder());
        final Object[] content = new Object[] { null };
        siw(new Runnable() {
            @Override public void run() {
                content[0] = rTable.getContent();
            }
        });
        JSONArray a = new JSONArray(content[0]);
        String expected = "[[\"Kathy\",\"Smith\",\"Snowboarding\",\"5\",\"false\"],[\"John\",\"Doe\",\"Rowing\",\"3\",\"true\"],[\"Sue\",\"Black\",\"Knitting\",\"2\",\"false\"],[\"Jane\",\"White\",\"Speed reading\",\"20\",\"true\"],[\"Joe\",\"Brown\",\"Pool\",\"10\",\"false\"]]";
        AssertJUnit.assertEquals(expected, a.toString());
    }
}
