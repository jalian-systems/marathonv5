package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXTreeViewElement extends JavaFXElement {

    public JavaFXTreeViewElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("select-by-properties"))
            return findNodeByProperties(new JSONObject((String) params[0]));
        else if (selector.equals("root")) {
            return Arrays.asList(new JavaFXTreeViewNodeElement(this, 0));
        } else if (selector.equals("nth-node")) {
            return Arrays.asList(new JavaFXTreeViewNodeElement(this, ((Integer) params[0]).intValue() - 1));
        } else if (selector.equals("all-nodes")) {
            TreeView<?> treeView = (TreeView<?>) getComponent();
            ArrayList<IJavaFXElement> r = new ArrayList<>();
            int nrows = treeView.getExpandedItemCount();
            for (int i = 0; i < nrows; i++) {
                r.add(new JavaFXTreeViewNodeElement(this, i));
            }
            return r;
        }
        return super.getByPseudoElement(selector, params);
    }

    private List<IJavaFXElement> findNodeByProperties(JSONObject o) {
        List<IJavaFXElement> r = new ArrayList<>();
        if (o.has("select")) {
            if (getPath((TreeView<?>) getComponent(), o.getString("select")) != null) {
                r.add(new JavaFXTreeViewNodeElement(this, o.getString("select")));
            }
        }
        return r;
    }

    @Override public boolean marathon_select(String value) {
        return setSelectionPath(value);
    }

    @SuppressWarnings("unchecked") private boolean setSelectionPath(String value) {
        TreeView<?> treeView = (TreeView<?>) getComponent();
        JSONArray pathElements = new JSONArray(value);
        List<TreeItem<?>> paths = new ArrayList<>();
        for (int i = 0; i < pathElements.length(); i++) {
            paths.add(getPath(treeView, pathElements.getString(i)));
        }
        treeView.getSelectionModel().clearSelection();
        for (@SuppressWarnings("rawtypes")
        TreeItem treeItem : paths) {
            treeView.scrollTo(treeView.getRow(treeItem));
            treeView.getSelectionModel().select(treeItem);
        }
        return true;
    }

}
