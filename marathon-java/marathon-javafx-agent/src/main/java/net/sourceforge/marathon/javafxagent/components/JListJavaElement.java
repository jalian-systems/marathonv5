package net.sourceforge.marathon.javafxagent.components;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.swing.JList;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.marathon.javafxagent.AbstractJavaElement;
import net.sourceforge.marathon.javafxagent.EventQueueWait;
import net.sourceforge.marathon.javafxagent.IJavaAgent;
import net.sourceforge.marathon.javafxagent.IJavaElement;
import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

public class JListJavaElement extends AbstractJavaElement {

    private interface Predicate {
        public boolean isValid(JListItemJavaElement e);
    }

    public JListJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    public String getContent() {
        return new JSONArray(getContent((JList) component)).toString();
    }

    public static String[][] getContent(JList component) {
        int nItems = ((JList) component).getModel().getSize();
        String[][] content = new String[1][nItems];
        for (int i = 0; i < nItems; i++) {
            content[0][i] = JListItemJavaElement.getText(component, i);
        }
        return content;
    }

    @Override public String _getText() {
        return getSelectionText((JList) component);
    }

    @Override public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("nth-item")) {
            return Arrays.asList((IJavaElement) new JListItemJavaElement(this, ((Integer) params[0]).intValue() - 1));
        } else if (selector.equals("all-items") || selector.equals("all-cells")) {
            return collectItems(new ArrayList<IJavaElement>(), new Predicate() {
                @Override public boolean isValid(JListItemJavaElement e) {
                    return true;
                }
            });
        } else if (selector.equals("select-by-properties"))
            return findItemByProperties(new JSONObject((String) params[0]));
        return super.getByPseudoElement(selector, params);
    }

    private List<IJavaElement> findItemByProperties(JSONObject o) {
        if (!o.has("select"))
            return Collections.<IJavaElement> emptyList();
        final String item = o.getString("select");
        List<IJavaElement> r = new ArrayList<IJavaElement>();
        return collectItems(r, new Predicate() {
            @Override public boolean isValid(JListItemJavaElement e) {
                return item.equals(e._getText());
            }
        });
    }

    public List<IJavaElement> collectItems(List<IJavaElement> r, Predicate p) {
        int nitems = getCount();
        for (int i = 0; i < nitems; i++) {
            JListItemJavaElement l = new JListItemJavaElement(this, i);
            if (p.isValid(l))
                r.add(l);
        }
        return r;
    }

    public int getCount() {
        try {
            return EventQueueWait.exec(new Callable<Integer>() {
                @Override public Integer call() {
                    return ((JList) getComponent()).getModel().getSize();
                }
            });
        } catch (Exception e) {
            throw new InternalError("Call to getSize() failed for JList#model");
        }
    }

    @Override public boolean marathon_select(JSONArray jsonArray) {
        Properties[] pa = new Properties[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            pa[i] = PropertyHelper.asProperties(jsonArray.getJSONObject(i));
        }
        return setItemSelection(pa);
    }

    @Override public boolean marathon_select(String value) {
        Properties[] pa = PropertyHelper.fromStringToArray(value, new String[][] { { "text" } });
        return setItemSelection(pa);
    }

    private boolean setItemSelection(Properties[] pa) {
        List<IJavaElement> r = new ArrayList<IJavaElement>();
        for (final Properties properties : pa) {
            collectItems(r, new Predicate() {
                @Override public boolean isValid(JListItemJavaElement e) {
                    Set<Object> keySet = properties.keySet();
                    for (Object object : keySet) {
                        if (!properties.getProperty(object.toString()).equals(e.getAttribute(object.toString())))
                            return false;
                    }
                    return true;
                }
            });
        }
        if (r.size() != pa.length)
            return false;
        int[] indices = new int[r.size()];
        int index = 0;
        for (IJavaElement je : r) {
            indices[index++] = ((JListItemJavaElement) je).getIndex();
        }
        ((JList) component).setSelectedIndices(indices);
        return true;
    }

    public static String getSelectionText(JList list) {
        List<Properties> pa = new ArrayList<Properties>();
        int[] selectedIndices = list.getSelectedIndices();
        for (int index : selectedIndices) {
            Properties p = new Properties();
            p.setProperty("listText", JListItemJavaElement.getText(list, index));
            pa.add(p);
        }
        return PropertyHelper.toString(pa.toArray(new Properties[pa.size()]), new String[] { "listText" });
    }
}
