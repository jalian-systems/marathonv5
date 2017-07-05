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
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.IPseudoElement;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.javaagent.NoSuchElementException;
import net.sourceforge.marathon.javaagent.UnsupportedCommandException;

public class JTableCellJavaElement extends AbstractJavaElement implements IPseudoElement {

    public static final Logger LOGGER = Logger.getLogger(JTableCellJavaElement.class.getName());

    private JTableJavaElement parent;
    private int viewRow;
    private int viewCol;

    public JTableCellJavaElement(JTableJavaElement parent, int row, int col) {
        super(parent);
        this.parent = parent;
        this.viewRow = row;
        this.viewCol = col;
    }

    @Override public String createHandle() {
        JSONObject o = new JSONObject().put("selector", "mnth-cell").put("parameters",
                new JSONArray().put(viewRow + 1).put(viewCol + 1));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override public IJavaElement getParent() {
        return parent;
    }

    @Override public Component getPseudoComponent() {
        return EventQueueWait.exec(new Callable<Component>() {
            @Override public Component call() throws Exception {
                validateRowCol();
                JTable table = (JTable) parent.getComponent();
                int row = table.convertRowIndexToModel(viewRow);
                int col = table.convertColumnIndexToModel(viewCol);
                TableCellRenderer cellRenderer = table.getCellRenderer(viewRow, viewCol);
                Object value = table.getModel().getValueAt(row, col);
                return cellRenderer.getTableCellRendererComponent(table, value, false, false, viewRow, viewCol);
            }
        });
    }

    @Override public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("editor")) {
            return Arrays.asList(JavaElementFactory.createElement(parent.getEditor(viewRow, viewCol), getDriver(), getWindow()));
        }
        throw new UnsupportedCommandException("JTableCell Java Element does not support pseudoelement " + selector, null);
    }

    private void validateRowCol() {
        JTable table = (JTable) parent.getComponent();
        try {
            int row = table.convertRowIndexToModel(viewRow);
            int col = table.convertColumnIndexToModel(viewCol);
            TableModel model = table.getModel();
            if (row >= 0 && row < model.getRowCount() && col >= 0 && col < model.getColumnCount()) {
                return;
            }
        } catch (IndexOutOfBoundsException e) {
        }
        throw new NoSuchElementException("Invalid row/col for JTable: (" + viewRow + ", " + viewCol + ")", null);
    }

    @Override public void _moveto() {
        validateRowCol();
        Rectangle bounds = getCellBounds();
        getDriver().getDevices().moveto(parent.getComponent(), bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    private Rectangle getCellBounds() {
        return ((JTable) parent.getComponent()).getCellRect(viewRow, viewCol, false);
    }

    @Override public boolean _isDisplayed() {
        return EventQueueWait.exec(new Callable<Boolean>() {
            @Override public Boolean call() throws Exception {
                return isVisible((JTable) parent.getComponent(), viewRow, viewCol);
            }
        });
    }

    private boolean isVisible(JTable table, int row, int col) {
        Rectangle visibleRect = table.getVisibleRect();
        Rectangle cellRect = table.getCellRect(row, col, false);
        return SwingUtilities.isRectangleContainingRectangle(visibleRect, cellRect);
    }

    @Override public Point _getMidpoint() {
        validateRowCol();
        Rectangle bounds = getCellBounds();
        return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    public int getViewRow() {
        return viewRow + 1;
    }

    public int getViewColumn() {
        return viewCol + 1;
    }

    public String getViewColumnName() {
        String columnName = getColumnName(viewCol);
        if (columnName == null) {
            return "" + (viewCol + 1);
        }
        return columnName;
    }

    public int getRow() {
        return viewRow;
    }

    public String getColumn() {
        String columnName = getColumnName(viewCol);
        if (columnName == null) {
            return "" + viewCol;
        }
        return columnName;
    }

    public int getCol() {
        return viewCol;
    }
}
