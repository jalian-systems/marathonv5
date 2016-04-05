package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXListViewElement extends JavaFXElement {

    public JavaFXListViewElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("nth-item")) {
            return Arrays.asList(new JavaFXListViewItemElement(this, ((Integer) params[0]).intValue() - 1));
        } else if (selector.equals("all-items") || selector.equals("all-cells")) {
            ListView<?> listView = (ListView<?>) getComponent();
            ArrayList<IJavaFXElement> r = new ArrayList<>();
            int nItems = listView.getItems().size();
            for (int i = 0; i < nItems; i++) {
                r.add(new JavaFXListViewItemElement(this, i));
            }
            return r;
        } else if (selector.equals("select-by-properties")) {
            return findItemByProperties(new JSONObject((String) params[0]));
        }
        return super.getByPseudoElement(selector, params);
    }

    private List<IJavaFXElement> findItemByProperties(JSONObject o) {
        List<IJavaFXElement> r = new ArrayList<>();
        if (o.has("select")) {
            if (o.getString("select") != null) {
                r.add(new JavaFXListViewItemElement(this, o.getString("select")));
            }
        }
        return r;
    }

    @Override public boolean marathon_select(String value) {
        return setItemSelection(value);
    }

    private boolean setItemSelection(String value) {
        ListView<?> listView = (ListView<?>) getComponent();
        JSONArray items = new JSONArray(value);
        List<Integer> listItemIndices = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            listItemIndices.add(getListItemIndex(listView, items.getString(i)));
        }
        listView.getSelectionModel().clearSelection();
        for (int i = 0; i < listItemIndices.size(); i++) {
            Integer index = listItemIndices.get(i);
            listView.scrollTo(index);
            listView.getSelectionModel().select(index);

        }
        return true;
    }

    @Override public String _getText() {
        return getListSelectionText((ListView<?>) getComponent());
    }

    public String getContent() {
        return new JSONArray(getContent((ListView<?>) getComponent())).toString();
    }
    
    /*
     * NOTE: Same code exits in RXFXListView class. So in case if you
     * want to modify. Modify both.
     */
    private String[][] getContent(ListView<?> listView) {
        int nItems = listView.getItems().size();
        String[][] content = new String[1][nItems];
        for (int i = 0; i < nItems; i++) {
            content[0][i] = new JavaFXListViewItemElement(this, i)._getText();
        }
        return content;
    }
}
