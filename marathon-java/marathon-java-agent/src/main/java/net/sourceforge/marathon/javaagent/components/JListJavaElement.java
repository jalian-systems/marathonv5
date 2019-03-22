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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.swing.JList;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JListJavaElement extends AbstractJavaElement {

    public static final Logger LOGGER = Logger.getLogger(JListJavaElement.class.getName());

    private interface Predicate {
        public boolean isValid(JListItemJavaElement e);
    }

    public JListJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    public String getContent() {
        return new JSONArray(getContent((JList) component)).toString();
    }

    public String[][] getContent(JList component) {
        int nItems = component.getModel().getSize();
        String[][] content = new String[1][nItems];
        for (int i = 0; i < nItems; i++) {
            content[0][i] = new JListItemJavaElement(this, i)._getText();
        }
        return content;
    }

    @Override
    public String _getText() {
        return getSelectionText((JList) component);
    }

    @Override
    public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("nth-item")) {
            return Arrays.asList((IJavaElement) new JListItemJavaElement(this, ((Integer) params[0]).intValue() - 1));
        } else if (selector.equals("all-items") || selector.equals("all-cells")) {
            return collectItems(new ArrayList<IJavaElement>(), new Predicate() {
                @Override
                public boolean isValid(JListItemJavaElement e) {
                    return true;
                }
            });
        } else if (selector.equals("select-by-properties")) {
            return findItemByProperties(new JSONObject((String) params[0]));
        }
        return super.getByPseudoElement(selector, params);
    }

    private List<IJavaElement> findItemByProperties(JSONObject o) {
        if (!o.has("select")) {
            return Collections.<IJavaElement> emptyList();
        }
        final String item = o.getString("select");
        List<IJavaElement> r = new ArrayList<IJavaElement>();
        return collectItems(r, new Predicate() {
            @Override
            public boolean isValid(JListItemJavaElement e) {
                return item.equals(e._getText());
            }
        });
    }

    public List<IJavaElement> collectItems(List<IJavaElement> r, Predicate p) {
        int nitems = getCount();
        for (int i = 0; i < nitems; i++) {
            JListItemJavaElement l = new JListItemJavaElement(this, i);
            if (p.isValid(l)) {
                r.add(l);
            }
        }
        return r;
    }

    public int getCount() {
        try {
            return EventQueueWait.exec(new Callable<Integer>() {
                @Override
                public Integer call() {
                    return ((JList) getComponent()).getModel().getSize();
                }
            });
        } catch (Exception e) {
            throw new InternalError("Call to getSize() failed for JList#model");
        }
    }

    @Override
    public boolean marathon_select(JSONArray jsonArray) {
        Properties[] pa = new Properties[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            pa[i] = PropertyHelper.asProperties(jsonArray.getJSONObject(i));
        }
        return setItemSelection(pa);
    }

    @Override
    public boolean marathon_select(String value) {
        Properties[] pa = PropertyHelper.fromStringToArray(value, new String[][] { { "text" } });
        return setItemSelection(pa);
    }

    private boolean setItemSelection(Properties[] pa) {
        List<IJavaElement> r = new ArrayList<IJavaElement>();
        for (final Properties properties : pa) {
            collectItems(r, new Predicate() {
                @Override
                public boolean isValid(JListItemJavaElement e) {
                    Set<Object> keySet = properties.keySet();
                    for (Object object : keySet) {
                        if (!properties.getProperty(object.toString()).equals(e.getAttribute(object.toString()))) {
                            return false;
                        }
                    }
                    return true;
                }
            });
        }
        if (r.size() != pa.length) {
            return false;
        }
        int[] indices = new int[r.size()];
        int index = 0;
        for (IJavaElement je : r) {
            indices[index++] = ((JListItemJavaElement) je).getIndex();
        }
        ((JList) component).setSelectedIndices(indices);
        return true;
    }

    public String getSelectionText(JList list) {
        List<Properties> pa = new ArrayList<Properties>();
        int[] selectedIndices = list.getSelectedIndices();
        for (int index : selectedIndices) {
            Properties p = new Properties();
            p.setProperty("listText", new JListItemJavaElement(this, index)._getText());
            pa.add(p);
        }
        return PropertyHelper.toString(pa.toArray(new Properties[pa.size()]), new String[] { "listText" });
    }

    public static int getIndexAt(JList list, Point point) {
        if (point == null)
            return list.getSelectedIndex();
        return list.locationToIndex(point);
    }
}
