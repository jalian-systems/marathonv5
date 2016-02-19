package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.marathon.javaagent.components.JTableJavaElement;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RTable extends RComponent {

    private int column;
    private int row;
    private String text;

    public RTable(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        JTable table = (JTable) source;
        if (table.isEditing()) {
            column = table.getEditingColumn();
            row = table.getEditingRow();
        } else {
            if (point != null) {
                row = table.rowAtPoint(point);
                column = table.columnAtPoint(point);
            } else {
                row = table.getSelectedRow();
                column = table.getSelectedColumn();
            }
        }
        if (row == -1 || column == -1) {
            row = column = -1;
        }
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + column;
        result = prime * result + row;
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        RTable other = (RTable) obj;
        if (column != other.column)
            return false;
        if (row != other.row)
            return false;
        return true;
    }

    @Override public void focusLost(RComponent next) {
        if (row != -1 && column != -1) {
            Component renderer = getRenderer();
            if (renderer == null) {
            } else {
                RComponentFactory finder = new RComponentFactory(omapConfig);
                RComponent pa = finder.findRComponent(renderer, null, recorder);
                Object t = pa.getText();
                if (t != null && !t.equals(text))
                    recorder.recordSelect2(this, t.toString(), true);
            }
        }
        if (next == null || next.getComponent() != getComponent()) {
            // Focus lost on the table
            recorder.recordSelect(this, getSelection());
        }
    }

    private String getSelection() {
        JTable table = (JTable) component;
        boolean rowSelectionAllowed = table.getRowSelectionAllowed();
        boolean columnSelectionAllowed = table.getColumnSelectionAllowed();

        if (!rowSelectionAllowed && !columnSelectionAllowed)
            return null;

        int[] rows = table.getSelectedRows();
        int[] columns = table.getSelectedColumns();
        int rowCount = table.getRowCount();
        int columnCount = table.getColumnCount();

        if (rows.length == rowCount && columns.length == columnCount)
            return "all";

        if (rows.length == 0 && columns.length == 0)
            return "";

        StringBuffer text = new StringBuffer();

        text.append("rows:[");
        for (int i = 0; i < rows.length; i++) {
            text.append(rows[i]);
            if (i != rows.length - 1)
                text.append(",");
        }
        text.append("],");
        text.append("columns:[");
        for (int i = 0; i < columns.length; i++) {
            String columnName = getColumnName(columns[i]);
            text.append(escape(columnName));
            if (i != columns.length - 1)
                text.append(",");
        }
        text.append("]");
        return text.toString();
    }

    private String escape(String columnName) {
        return columnName.replaceAll("#", "##").replaceAll(",", "#;");
    }

    @Override public void focusGained(RComponent prev) {
        if (row != -1 && column != -1) {
            Component renderer = getRenderer();
            if (renderer == null) {
            } else {
                RComponentFactory finder = new RComponentFactory(omapConfig);
                RComponent pa = finder.findRComponent(renderer, null, recorder);
                text = pa.getText();
            }
        }
    }

    private Component getRenderer() {
        Object value = getObjectAtCell(row, column);
        TableCellRenderer cellRenderer = ((JTable) component).getCellRenderer(row, column);
        return cellRenderer.getTableCellRendererComponent((JTable) component, value, false, false, row, column);
    }

    private Object getObjectAtCell(int row, int column) {
        int modelRow = ((JTable) component).convertRowIndexToModel(row);
        int modelColumn = ((JTable) component).convertColumnIndexToModel(column);
        Object value = ((JTable) component).getModel().getValueAt(modelRow, modelColumn);
        return value;
    }

    @Override public String getCellInfo() {
        if (column == -1 || row == -1)
            return null;
        String scolumn = getColumnName(column);
        if (scolumn == null || "".equals(scolumn))
            scolumn = "" + column;
        return "{" + row + ", " + scolumn + "}";
    }

    @Override public String[][] getContent() {
        return JTableJavaElement.getContent((JTable) component);
    }

    @Override public String getText() {
        if (row != -1 && column != -1) {
            Component renderer = getRenderer();
            if (renderer != null) {
                RComponentFactory finder = new RComponentFactory(omapConfig);
                RComponent pa = finder.findRComponent(renderer, null, recorder);
                return pa.getText();
            }
        }
        return null;
    }
    
    @Override protected void mousePressed(MouseEvent me) {
        // Ignore Ctrl+Clicks used to select the nodes
        if (me.getButton() == MouseEvent.BUTTON1 && isMenuShortcutKeyDown(me))
            return;
        if (me.getButton() != MouseEvent.BUTTON1)
            focusLost(null);
        super.mousePressed(me);
    }
}
