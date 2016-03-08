package net.sourceforge.marathon.javaagent.components;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.JTabbedPane;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

import org.json.JSONArray;

public class JTabbedPaneJavaElement extends AbstractJavaElement {

    public JTabbedPaneJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public String _getText() {
        return getSelectedItemText((JTabbedPane)component);
    }

    public static String getSelectedItemText(JTabbedPane component) {
        int selectedIndex = component.getSelectedIndex();
        if(selectedIndex != -1)
            return JTabbedPaneTabJavaElement.getText(component, selectedIndex);
        return "";
    }

    @Override public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("nth-tab")) {
            int tabIndex = ((Integer) params[0]).intValue() - 1;
            return Arrays.asList((IJavaElement) new JTabbedPaneTabJavaElement(this, tabIndex));
        } else if (selector.equals("all-tabs")) {
            int nitems = getCount();
            List<IJavaElement> r = new ArrayList<IJavaElement>();
            for (int i = 0; i < nitems; i++) {
                r.add(new JTabbedPaneTabJavaElement(this, i));
            }
            return r;
        }
        return super.getByPseudoElement(selector, params);
    }

    private int getCount() {
        try {
            return EventQueueWait.exec(new Callable<Integer>() {
                @Override public Integer call() {
                    JTabbedPane pane = (JTabbedPane) getComponent();
                    return pane.getTabCount();
                }
            });
        } catch (Exception e) {
            throw new InternalError("Call to getTabCount() failed for JTabbedPane#getTabCount");
        }
    }

    @Override public boolean marathon_select(String tab) {
        JTabbedPane tp = (JTabbedPane) component;
        int tabCount = tp.getTabCount();
        for (int index = 0; index < tabCount; index++) {
            String current = JTabbedPaneTabJavaElement.getText(tp, index);
            if (tab.equals(current)) {
                tp.setSelectedIndex(index);
                return true;
            }
        }
        return false;
    }

    public String getContent() {
        return new JSONArray(getContent((JTabbedPane) component)).toString();
    }

    public static String[][] getContent(JTabbedPane component) {
        int nItems = component.getTabCount();
        String[][] content = new String[1][nItems];
        for (int i = 0; i < nItems; i++) {
            content[0][i] = JTabbedPaneTabJavaElement.getText(component, i);
        }
        return content;
    }
}
