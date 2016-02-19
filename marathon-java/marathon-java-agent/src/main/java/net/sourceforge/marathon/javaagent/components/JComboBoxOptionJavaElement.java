package net.sourceforge.marathon.javaagent.components;

import java.awt.Component;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.IPseudoElement;
import net.sourceforge.marathon.javaagent.JavaElementPropertyAccessor;
import net.sourceforge.marathon.javaagent.NoSuchElementException;

import org.json.JSONArray;
import org.json.JSONObject;

public class JComboBoxOptionJavaElement extends AbstractJavaElement implements IPseudoElement {

    private JComboBoxJavaElement parent;
    private int option;

    public JComboBoxOptionJavaElement(JComboBoxJavaElement parent, int option) {
        super(parent);
        this.parent = parent;
        this.option = option;
    }

    @Override public Component getPseudoComponent() {
        return EventQueueWait.exec(new Callable<Component>() {
            @Override public Component call() throws Exception {
                return getRendererComponent(((JComboBox) parent.getComponent()), option);
            }
        });
    }

    private static Component getRendererComponent(JComboBox comboBox, int option) {
        ComboBoxModel model = comboBox.getModel();
        if (option >= model.getSize())
            throw new NoSuchElementException("Index out-of-bounds error on JComboBox: " + option, null);
        Component rendererComponent = comboBox.getRenderer().getListCellRendererComponent(new JList(model),
                model.getElementAt(option), option, false, false);
        return rendererComponent;
    }

    @Override public String createHandle() {
        JSONObject o = new JSONObject().put("selector", "nth-option").put("parameters", new JSONArray().put(option + 1));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override public void click(int button, int clickCount, int xoffset, int yoffset) {
        long implicitWait = parent.getDriver().implicitWait;
        try {
            parent.getDriver().setImplicitWait(0);
            EventQueueWait.exec(new Runnable() {
                @Override public void run() {
                    List<IJavaElement> menus = parent.getDriver().findElementsByCssSelector("basic-combo-popup");
                    if (menus.size() == 0)
                        try {
                            List<IJavaElement> dropdown = parent.findElementsByCssSelector(":instance-of('javax.swing.JButton')");
                            if (dropdown.size() == 0)
                                parent.click();
                            else
                                dropdown.get(0).click();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            });
            EventQueueWait.empty();
            EventQueueWait.exec(new Runnable() {
                @Override public void run() {
                    IJavaElement menu;
                    try {
                        menu = parent.getDriver().findElementByCssSelector("basic-combo-popup");
                        IJavaElement listitem = menu.findElementByCssSelector("list::nth-item(" + (option + 1) + ")");
                        listitem.click();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } finally {
            parent.getDriver().setImplicitWait(implicitWait);
        }
    }

    @Override public String _getText() {
        return getText((JComboBox) component, option, true);
    }

    public static String getText(JComboBox combo, int index, boolean appendIndex) {
        String original = getItemText(combo, index);
        String itemText = original;
        int suffixIndex = 0;
        for (int i = 0; i < index; i++) {
            String current = getItemText(combo, i);
            if (current.equals(original)) {
                if (appendIndex)
                    itemText = String.format("%s(%d)", original, ++suffixIndex);
                else
                    itemText = original;
            }
        }
        return itemText;
    }

    protected static String getItemText(JComboBox combo, int index) {
        Component renComponent = getRendererComponent(combo, index);
        JavaElementPropertyAccessor pa = new JavaElementPropertyAccessor(renComponent);
        String asText = stripHTMLTags(pa.getText());
        if (asText == null) {
            asText = stripHTMLTags(combo.getSelectedItem().toString());
        }
        return asText;
    }

    protected static String stripHTMLTags(String text) {
        Pattern p = Pattern.compile("(<\\s*html\\s*>)(.*)(<\\s*/html\\s*>)");
        Matcher m = p.matcher(text);
        if (m.matches())
            text = stripTags(m.group(2));
        return text;
    }

    private static String stripTags(String text) {
        text = text.trim();
        int indexOfGT = text.indexOf("<");
        int indexOfLT = text.indexOf(">");
        if (indexOfGT != -1 && indexOfLT != -1 && indexOfLT > indexOfGT) {
            text = text.replace(text.substring(indexOfGT, indexOfLT + 1), "");
            text = stripTags(text);
        }
        return text;
    }

    @Override public JComboBoxJavaElement getParent() {
        return parent;
    }
}
