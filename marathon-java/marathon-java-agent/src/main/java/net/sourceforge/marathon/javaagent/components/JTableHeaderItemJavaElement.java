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
import java.util.concurrent.Callable;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.IPseudoElement;
import net.sourceforge.marathon.javaagent.JavaElementPropertyAccessor;
import net.sourceforge.marathon.javaagent.NoSuchElementException;

public class JTableHeaderItemJavaElement extends AbstractJavaElement implements IPseudoElement {

    private JTableHeaderJavaElement parent;
    private int item;

    public JTableHeaderItemJavaElement(JTableHeaderJavaElement parent, int item) {
        super(parent);
        this.parent = parent;
        this.item = item;
    }

    @Override public String createHandle() {
        JSONObject o = new JSONObject().put("selector", "nth-item").put("parameters", new JSONArray().put(item + 1));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override public IJavaElement getParent() {
        return parent;
    }

    @Override public void _moveto() {
        int columnCount = getColumnCount();
        if (item < 0 || item >= columnCount) {
            throw new NoSuchElementException("Index out of bounds error on JTableHeader: " + item, null);
        }
        Rectangle bounds = getCellBounds();
        getDriver().getDevices().moveto(parent.getComponent(), bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    @Override public Component getPseudoComponent() {
        return EventQueueWait.exec(new Callable<Component>() {
            @Override public Component call() {
                return getRendererComponent((JTableHeader) parent.getComponent(), item);
            }
        });
    }

    private Rectangle getCellBounds() {
        return ((JTableHeader) parent.getComponent()).getHeaderRect(item);
    }

    @Override public Point _getMidpoint() {
        int columnCount = getColumnCount();
        if (item < 0 || item >= columnCount) {
            throw new NoSuchElementException("Index out of bounds error on JTableHeader: " + item, null);
        }
        Rectangle bounds = getCellBounds();
        return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    public int getItem() {
        return item + 1;
    }

    public static Component getRendererComponent(JTableHeader jTableHeader, int item) {
        int columnCount = jTableHeader.getColumnModel().getColumnCount();
        if (item < 0 || item >= columnCount) {
            throw new NoSuchElementException("Index out of bounds error on JTableHeader: " + item, null);
        }
        Object headerValue = jTableHeader.getColumnModel().getColumn(item).getHeaderValue();
        TableCellRenderer headerRenderer = jTableHeader.getColumnModel().getColumn(item).getHeaderRenderer();
        if (headerRenderer == null) {
            headerRenderer = jTableHeader.getDefaultRenderer();
        }
        Component rendererComponent = headerRenderer.getTableCellRendererComponent(jTableHeader.getTable(), headerValue, false,
                false, 0, item);
        return rendererComponent;
    }

    @Override public String _getText() {
        return getText((JTableHeader) component, item);
    }

    public static String getText(JTableHeader tableHeader, int index) {
        String original = getItemText(tableHeader, index);
        String itemText = original;
        int suffixIndex = 0;
        for (int i = 0; i < index; i++) {
            String current = getItemText(tableHeader, i);
            if (current.equals(original)) {
                itemText = String.format("%s(%d)", original, ++suffixIndex);
            }
        }
        return itemText;
    }

    protected static String getItemText(JTableHeader tableHeader, int index) {
        Component r = JTableHeaderItemJavaElement.getRendererComponent(tableHeader, index);
        JavaElementPropertyAccessor pa = new JavaElementPropertyAccessor(r);
        return pa.getText();
    }
}
