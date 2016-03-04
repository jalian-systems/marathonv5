package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.sourceforge.marathon.javafxagent.IJavaAgent;
import net.sourceforge.marathon.javafxagent.IJavaElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JWindow;

public class JavaFXTreeViewElement extends JavaFXElement {

	public JavaFXTreeViewElement(Node component, IJavaAgent driver, JWindow window) {
		super(component, driver, window);
	}

	@Override
	public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
		if (selector.equals("select-by-properties"))
			return findNodeByProperties(new JSONObject((String)params[0]));
		return super.getByPseudoElement(selector, params);
	}

	private List<IJavaElement> findNodeByProperties(JSONObject o) {
		List<IJavaElement> r = new ArrayList<>();
		if (o.has("select")) {
			if (getPath((TreeView<?>) getComponent(), o.getString("select")) != null) {
				r.add(new JavaFXTreeViewNodeElement(this, o.getString("select")));
			}
		}
		return r;
	}

	@Override
	public boolean marathon_select(String value) {
		return setSelectionPath(value);
	}

	@SuppressWarnings("unchecked")
	private boolean setSelectionPath(String value) {
		TreeView<?> treeView = (TreeView<?>) getComponent();
		JSONArray pathElements = new JSONArray(value);
		List<TreeItem<?>> paths = new ArrayList<>();
		for (int i = 0; i < pathElements.length(); i++) {
			paths.add(getPath(treeView, pathElements.getString(i)));
		}
		treeView.getSelectionModel().clearSelection();
		for (@SuppressWarnings("rawtypes") TreeItem treeItem : paths) {
			treeView.scrollTo(treeView.getRow(treeItem));
			treeView.getSelectionModel().select(treeItem);
		}
		return true;
	}

	public static TreeItem<?> getPath(TreeView<?> treeView, String path) {
		String[] tokens = path.substring(1).split("(?<!\\\\)/");
		Object rootNode = treeView.getRoot();
		int start = treeView.isShowRoot() ? 1 : 0;
		List<TreeItem<?>> treePath = new ArrayList<TreeItem<?>>();
		treePath.add((TreeItem<?>) rootNode);
		StringBuilder searchedPath = new StringBuilder();
		if (treeView.isShowRoot()) {
			String rootNodeText = unescapeSpecialCharacters(tokens[0]);
			searchedPath.append("/" + rootNodeText);
			if (rootNode == null)
				throw new RuntimeException("TreeView does not have a root node!");
			if (!searchedPath.toString().equals("/" + getPathText(treePath)))
				throw new RuntimeException("TreeView root node does not match: Expected </" + getPathText(treePath)
						+ "> Actual: <" + searchedPath.toString() + ">");
		}
		for (int i = start; i < tokens.length; i++) {
			String childText = unescapeSpecialCharacters(tokens[i]);
			searchedPath.append("/" + childText);
			boolean matched = false;
			TreeItem<?> item = (TreeItem<?>) treePath.get(treePath.size() - 1);
			item.setExpanded(true);
			for (int j = 0; j < item.getChildren().size(); j++) {
				Object child = item.getChildren().get(j);
				treePath.add((TreeItem<?>) child);
				List<TreeItem<?>> childPath = treePath;
				if (childText.equals(getPathText(childPath))) {
					treePath = childPath;
					matched = true;
					break;
				}
			}
			if (!matched)
				return null;
		}
		return treePath.get(treePath.size() - 1);
	}

	private static String getPathText(List<TreeItem<?>> treePath) {
		// We should be getting the *text* from the tree cell. We need to figure
		// out how to construct
		// a tree cell for a item that is not visible
		// Set<Node> nodes = treeView.lookupAll(".tree-cell");
		// for (Node node : nodes) {
		// TreeCell<?> cell = (TreeCell<?>) node;
		// if (lastPathComponent == cell.getTreeItem()) {
		// RFXComponent cellComponent = new
		// RFXComponentFactory(omapConfig).findRawRComponent(node, null,
		// recorder);
		// return cellComponent.getText();
		// }
		// }
		TreeItem<?> lastPathComponent = treePath.get(treePath.size() - 1);
		if (lastPathComponent == null || lastPathComponent.getValue() == null)
			return "";
		return lastPathComponent.getValue().toString();
	}

	public static String unescapeSpecialCharacters(String name) {
		return name.replaceAll("\\\\/", "/");
	}
	
}
