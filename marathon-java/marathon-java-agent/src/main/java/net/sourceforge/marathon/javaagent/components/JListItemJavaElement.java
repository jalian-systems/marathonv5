package net.sourceforge.marathon.javaagent.components;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.Callable;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IPseudoElement;
import net.sourceforge.marathon.javaagent.JavaElementPropertyAccessor;
import net.sourceforge.marathon.javaagent.NoSuchElementException;

import org.json.JSONArray;
import org.json.JSONObject;

public class JListItemJavaElement extends AbstractJavaElement implements IPseudoElement {

    private int item;
    private JListJavaElement parent;
    private static final int MAX_LIST_ITEMS = Integer.parseInt(System.getProperty("marathon.duplicate.check.max", "100"));

    public JListItemJavaElement(JListJavaElement parent, int item) {
        super(parent);
        this.parent = parent;
        this.item = item;
    }

    private void validateItem() {
        if (item >= ((JList) parent.getComponent()).getModel().getSize())
            throw new NoSuchElementException("Index out of bounds error on JList: " + item, null);
    }

    @Override public String createHandle() {
        JSONObject o = new JSONObject().put("selector", "nth-item").put("parameters", new JSONArray().put(item + 1));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override public JListJavaElement getParent() {
        return parent;
    }

    @Override public void _moveto() {
        validateItem();
        Rectangle bounds = getCellBounds();
        getDriver().getDevices().moveto(parent.getComponent(), bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    private Rectangle getCellBounds() {
        return ((JList) parent.getComponent()).getCellBounds(item, item);
    }

    @Override public boolean _isDisplayed() {
        JList list = (JList) parent.getComponent();
        return item <= list.getLastVisibleIndex() && item >= list.getFirstVisibleIndex();
    }

    @Override public Component getPseudoComponent() {
        return EventQueueWait.exec(new Callable<Component>() {
            @Override public Component call() throws Exception {
                validateItem();
                JList list = (JList) parent.getComponent();
                return getRendererComponent(list, item);
            }
        });
    }

    public static Component getRendererComponent(JList list, int item) {
        Object value = list.getModel().getElementAt(item);
        ListCellRenderer cellRenderer = list.getCellRenderer();
        Component rendererComponent = cellRenderer.getListCellRendererComponent(list, value, item, false, false);
        if (rendererComponent == null) {
            return null;
        }
        return rendererComponent;
    }

    public int getIndex() {
        return item;
    }

    @Override public Point _getMidpoint() {
        validateItem();
        Rectangle bounds = getCellBounds();
        return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    @Override public String _getText() {
        return getText((JList) component, item);
    }

    public static String getText(JList list, int index) {
        String original = getItemText(list, index);
        String itemText = original;
        int suffixIndex = 0;
        if (list.getModel().getSize() > MAX_LIST_ITEMS)
            return itemText;
        for (int i = 0; i < index; i++) {
            String current = getItemText(list, i);
            if (current.equals(original))
                itemText = String.format("%s(%d)", original, ++suffixIndex);
        }
        return itemText;
    }

    protected static String getItemText(JList listItem, int index) {
        Component renComponent = getRendererComponent(listItem, index);
        JavaElementPropertyAccessor pa = new JavaElementPropertyAccessor(renComponent);
        return pa.getText();
    }
}
