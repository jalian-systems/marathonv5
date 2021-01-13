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

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.IPseudoElement;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.javaagent.JavaElementPropertyAccessor;
import net.sourceforge.marathon.javaagent.NoSuchElementException;
import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.json.JSONObject;

public class JListItemJavaElement extends AbstractJavaElement implements IPseudoElement {

    public static final Logger LOGGER = Logger.getLogger(JListItemJavaElement.class.getName());

    private int item;
    private JListJavaElement parent;
    public static final int MAX_LIST_ITEMS = Integer.parseInt(System.getProperty("marathon.duplicate.check.max", "100"));

    public JListItemJavaElement(JListJavaElement parent, int item) {
        super(parent);
        this.parent = parent;
        this.item = item;
    }

    private void validateItem() {
        if (item >= ((JList) parent.getComponent()).getModel().getSize()) {
            throw new NoSuchElementException("Index out of bounds error on JList: " + item, null);
        }
    }

    @Override
    public String createHandle() {
        JSONObject o = new JSONObject().put("selector", "nth-item").put("parameters", new JSONArray().put(item + 1));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override
    public JListJavaElement getParent() {
        return parent;
    }

    @Override
    public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("editor")) {
            return Arrays.asList(
                    JavaElementFactory.createElement(getRendererComponent((JList) component, item), getDriver(), getWindow()));
        }
        return super.getByPseudoElement(selector, params);
    }

    @Override
    public void _moveto() {
        validateItem();
        Rectangle bounds = getCellBounds();
        getDriver().getDevices().moveto(parent.getComponent(), bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    private Rectangle getCellBounds() {
        return ((JList) parent.getComponent()).getCellBounds(item, item);
    }

    @Override
    public boolean _isDisplayed() {
        JList list = (JList) parent.getComponent();
        return item <= list.getLastVisibleIndex() && item >= list.getFirstVisibleIndex();
    }

    @Override
    public Component getPseudoComponent() {
        return EventQueueWait.exec(new Callable<Component>() {
            @Override
            public Component call() throws Exception {
                validateItem();
                JList list = (JList) parent.getComponent();
                return getRendererComponent(list, item);
            }
        });
    }

    public int getIndex() {
        return item;
    }

    @Override
    public Point _getMidpoint() {
        validateItem();
        Rectangle bounds = getCellBounds();
        return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    @Override
    public String _getText() {
        return resolveDuplicate((JList) component, item, ((JavaElementPropertyAccessor) JavaElementFactory
                .createElement(getRendererComponent((JList) component, item), driver, window))._getText());
    }

    private String resolveDuplicate(JList list, int index, String original) {
        String itemText = original;
        int suffixIndex = 0;
        if (list.getModel().getSize() > MAX_LIST_ITEMS) {
            return itemText;
        }
        for (int i = 0; i < index; i++) {
            String current = getItemText(list, i);
            if (current.equals(original)) {
                itemText = String.format("%s(%d)", original, ++suffixIndex);
            }
        }
        return itemText;
    }

    protected String getItemText(JList listItem, int index) {
        return ((JavaElementPropertyAccessor) JavaElementFactory.createElement(getRendererComponent((JList) listItem, index),
                driver, window))._getText();
    }

    public static Component getRendererComponent(JList list, int item) {
        Object value = list.getModel().getElementAt(item);
        ListCellRenderer cellRenderer = list.getCellRenderer();
        Component rendererComponent = cellRenderer.getListCellRendererComponent(list, value, item, false, false);
        if (rendererComponent == null) {
            return null;
        }
        if (rendererComponent instanceof JComponent) {
            ((JComponent) rendererComponent).putClientProperty("jlist", (JList) list);
            ((JComponent) rendererComponent).putClientProperty("item", item);
        }
        return rendererComponent;
    }

}
