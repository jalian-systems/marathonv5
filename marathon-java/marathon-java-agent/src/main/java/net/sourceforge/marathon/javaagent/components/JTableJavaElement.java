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

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;
import net.sourceforge.marathon.javaagent.NoSuchElementException;

public class JTableJavaElement extends AbstractJavaElement {

    public static final Logger LOGGER = Logger.getLogger(JTableJavaElement.class.getName());

    private static final class PropertyPredicate implements Predicate {
        private final Properties p;

        private PropertyPredicate(Properties p) {
            this.p = p;
        }

        @Override public boolean isValid(JTableCellJavaElement e) {
            Enumeration<Object> keys = p.keys();
            while (keys.hasMoreElements()) {
                String object = (String) keys.nextElement();
                if (!p.getProperty(object).equals(e.getAttribute(object))) {
                    return false;
                }
            }
            return true;
        }
    }

    private static class RowColPropertyPredicate implements Predicate {

        private String row;
        private String column;

        public RowColPropertyPredicate(String row, String column) {
            this.row = row;
            this.column = column;
        }

        @Override public boolean isValid(JTableCellJavaElement e) {
            String eRow = e.getAttribute("row");
            String eColumn = e.getAttribute("column");
            if (row.equals(eRow)) {
                if (column.equals(eColumn))
                    return true;
                if (column.length() == 1 && column.charAt(0) >= 'A' && column.charAt(0) <= 'Z') {
                    int colId = column.charAt(0) - 'A';
                    if ((e.getViewColumn() - 1) == colId)
                        return true;
                }
            }
            return false;
        }

    }

    private static interface Predicate {
        public boolean isValid(JTableCellJavaElement e);
    }

    public JTableJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("header")) {
            return Arrays.asList((IJavaElement) new JTableHeaderJavaElement(((JTable) getComponent()).getTableHeader(), getDriver(),
                    getWindow()));
        } else if (selector.equals("mnth-cell")) {
            return Arrays.asList((IJavaElement) new JTableCellJavaElement(this, ((Integer) params[0]).intValue() - 1,
                    ((Integer) params[1]).intValue() - 1));
        } else if (selector.equals("all-cells")) {
            return collectCells(new ArrayList<IJavaElement>(), new Predicate() {
                @Override public boolean isValid(JTableCellJavaElement e) {
                    return true;
                }
            });
        } else if (selector.equals("select-by-properties")) {
            JSONObject o = new JSONObject((String) params[0]);
            return selectByProperties(new ArrayList<IJavaElement>(), o);
        }
        return super.getByPseudoElement(selector, params);
    }

    public List<IJavaElement> collectCells(List<IJavaElement> r, Predicate p) {
        try {
            int rows = (Integer) EventQueueWait.call(getComponent(), "getRowCount");
            int cols = (Integer) EventQueueWait.call(getComponent(), "getColumnCount");
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    JTableCellJavaElement e = new JTableCellJavaElement(this, i, j);
                    if (p.isValid(e)) {
                        r.add(e);
                    }
                }
            }
            return r;
        } catch (NoSuchMethodException e) {
            return Collections.<IJavaElement> emptyList();
        }
    }

    private List<IJavaElement> selectByProperties(List<IJavaElement> r, JSONObject o) {
        final Properties p;
        if (o.has("select")) {
            p = PropertyHelper.fromString(o.getString("select"), new String[][] { { "row", "column" } });
            return collectCells(r, new RowColPropertyPredicate(p.getProperty("row"), p.getProperty("column")));
        } else {
            p = PropertyHelper.asProperties(o);
            return collectCells(r, new PropertyPredicate(p));
        }
    }

    public Component getEditor(final int viewRow, final int viewCol) {
        return EventQueueWait.exec(new Callable<Component>() {
            @Override public Component call() throws Exception {
                validate(viewRow, viewCol);
                JTable table = (JTable) getComponent();
                table.editCellAt(viewRow, viewCol);
                Component c = table.getEditorComponent();
                if (c instanceof JComponent) {
                    ((JComponent) c).putClientProperty("marathon.celleditor", true);
                    ((JComponent) c).putClientProperty("marathon.celleditor.parent", table);
                }
                return c;
            }
        });
    }

    @Override public String _getText() {
        JTable table = (JTable) getComponent();
        int rows = table.getRowCount();
        int cols = table.getColumnCount();
        JSONArray r = new JSONArray();
        for (int i = 0; i < rows; i++) {
            JSONArray c = new JSONArray();
            for (int j = 0; j < cols; j++) {
                c.put(new JTableCellJavaElement(JTableJavaElement.this, i, j)._getText());
            }
            r.put(c);
        }
        return r.toString();
    }

    private void validate(int viewRow, int viewCol) {
        JTable table = (JTable) getComponent();
        try {
            int row = table.convertRowIndexToModel(viewRow);
            int col = table.convertColumnIndexToModel(viewCol);
            TableModel model = table.getModel();
            if (row >= 0 && row < model.getRowCount() && col >= 0 && col < model.getColumnCount()) {
                if (table.isCellEditable(viewRow, viewCol)) {
                    return;
                } else {
                    throw new NoSuchElementException("The cell is not editable on JTable: (" + viewRow + ", " + viewCol + ")",
                            null);
                }
            }
        } catch (IndexOutOfBoundsException e) {
        }
        throw new NoSuchElementException("Invalid row/col for JTable: (" + viewRow + ", " + viewCol + ")", null);
    }

    public String getContent() {
        return new JSONArray(getContent((JTable) component)).toString();
    }

    public static String[][] getContent(JTable component) {
        int rows = component.getRowCount();
        int cols = component.getColumnCount();
        String[][] content = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Object valueAt = component.getValueAt(i, j);
                if (valueAt == null) {
                    valueAt = "";
                }
                content[i][j] = valueAt.toString();
            }
        }
        return content;
    }

    @Override public boolean marathon_select(JSONArray jsonArray) {
        List<IJavaElement> l = new ArrayList<IJavaElement>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject o = jsonArray.getJSONObject(i);
            selectByProperties(l, o);
        }
        int[] rows = new int[l.size()];
        int[] cols = new int[l.size()];
        int index = 0;
        for (IJavaElement e : l) {
            rows[index] = ((JTableCellJavaElement) e).getRow();
            cols[index] = ((JTableCellJavaElement) e).getCol();
        }
        selectRowsColumns((JTable) component, rows, cols);
        return true;
    }

    @Override public boolean marathon_select(String text) {
        JTable table = (JTable) component;
        boolean cellEditing = table.isEditing();
        if (cellEditing) {
            return true;
        }
        if ("".equals(text)) {
            table.clearSelection();
            return true;
        }
        int[] rows;
        int[] cols;
        if ("all".equals(text)) {
            int rowCount = table.getRowCount();
            int columnCount = table.getColumnCount();
            rows = new int[rowCount];
            cols = new int[columnCount];
            for (int i = 0; i < rowCount; i++) {
                rows[i] = i;
            }
            for (int i = 0; i < columnCount; i++) {
                cols[i] = i;
            }
        } else {
            rows = parseRows(text);
            String[] colNames = parseCols(text);
            cols = new int[colNames.length];
            for (int i = 0; i < colNames.length; i++) {
                cols[i] = getColumnIndex(colNames[i]);
            }
        }

        return selectRowsColumns(table, rows, cols);
    }

    private boolean selectRowsColumns(JTable table, int[] rows, int[] cols) {
        int rowCount = table.getRowCount();
        for (int r : rows) {
            if (r >= rowCount) {
                return false;
            }
        }
        table.clearSelection();
        for (int c : cols) {
            table.addColumnSelectionInterval(c, c);
        }
        for (int r : rows) {
            table.addRowSelectionInterval(r, r);
        }
        return true;
    }

    private int getColumnIndex(String columnName) {
        JTable table = (JTable) component;
        int ncolumns = table.getColumnCount();
        for (int i = 0; i < ncolumns; i++) {
            String column = getColumnName(i);
            if (columnName.equals(escape(column))) {
                return i;
            }
        }
        if (columnName.length() == 1 && columnName.charAt(0) >= 'A' && columnName.charAt(0) <= 'Z')
            return columnName.charAt(0) - 'A';
        throw new RuntimeException("Could not find column " + columnName + " in table");
    }

    private String escape(String columnName) {
        return columnName.replaceAll("#", "##").replaceAll(",", "#;");
    }

    private int[] parseRows(String s) {
        String rowText = "";
        int i = s.indexOf("rows:");
        if (i != -1) {
            int j = s.indexOf("columns:");
            if (j == -1) {
                rowText = s.substring(i + 5);
            } else {
                rowText = s.substring(i + 5, j);
            }
            int k = rowText.indexOf('[');
            int l = rowText.indexOf(']');
            rowText = rowText.substring(k + 1, l);
        }
        StringTokenizer tokenizer = new StringTokenizer(rowText, ", ");
        List<String> rows = new ArrayList<String>();
        while (tokenizer.hasMoreElements()) {
            rows.add(tokenizer.nextToken());
        }
        int irows[] = new int[rows.size()];
        for (int j = 0; j < irows.length; j++) {
            try {
                irows[j] = Integer.parseInt(rows.get(j));
            } catch (Throwable t) {
                return new int[0];
            }
        }
        return irows;
    }

    private String[] parseCols(String s) {
        String colText = "";
        int i = s.indexOf("columns:");
        if (i != -1) {
            colText = s.substring(i + 8);
            int k = colText.indexOf('[');
            int l = colText.indexOf(']');
            colText = colText.substring(k + 1, l);
        }
        List<String> cols = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(colText, ",");
        while (tokenizer.hasMoreElements()) {
            cols.add(tokenizer.nextToken());
        }
        return cols.toArray(new String[cols.size()]);
    }

}
