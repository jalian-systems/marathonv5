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
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.JTabbedPane;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JTabbedPaneJavaElement extends AbstractJavaElement {

    public JTabbedPaneJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public String _getText() {
        return getSelectedItemText((JTabbedPane) component);
    }

    public static String getSelectedItemText(JTabbedPane component) {
        int selectedIndex = component.getSelectedIndex();
        if (selectedIndex != -1) {
            return JTabbedPaneTabJavaElement.getText(component, selectedIndex);
        }
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
        } else if (selector.equals("selected-tab")) {
            int tabIndex = ((JTabbedPane)component).getSelectedIndex();
            if(tabIndex != -1)
                return Arrays.asList((IJavaElement) new JTabbedPaneTabJavaElement(this, tabIndex));
            else
                return Arrays.asList();
        } else if (selector.equals("select-by-properties")) {
            return findNodeByProperties(new JSONObject((String) params[0]));
        }
        return super.getByPseudoElement(selector, params);
    }

    private List<IJavaElement> findNodeByProperties(JSONObject o) {
        if (!o.has("select")) {
            return Collections.<IJavaElement> emptyList();
        }
        String tab = o.getString("select");
        int tabIndex = findTabIndex((JTabbedPane) component, tab);
        if (tabIndex != -1) {
            return Arrays.asList((IJavaElement) new JTabbedPaneTabJavaElement(this, tabIndex));
        }
        return Collections.<IJavaElement> emptyList();
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
        int tabIndex = findTabIndex((JTabbedPane) component, tab);
        if (tabIndex != -1) {
            ((JTabbedPane) component).setSelectedIndex(tabIndex);
        }
        return tabIndex != -1;
    }

    private int findTabIndex(JTabbedPane tp, String tab) {
        int tabCount = tp.getTabCount();
        int tabIndex = -1;
        for (int index = 0; index < tabCount; index++) {
            String current = JTabbedPaneTabJavaElement.getText(tp, index);
            if (tab.equals(current)) {
                tabIndex = index;
                break;
            }
        }
        return tabIndex;
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
