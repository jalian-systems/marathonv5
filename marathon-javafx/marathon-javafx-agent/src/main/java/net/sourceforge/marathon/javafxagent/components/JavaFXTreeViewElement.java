package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.sourceforge.marathon.javafxagent.IJavaAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

public class JavaFXTreeViewElement extends JavaFXElement {

	public JavaFXTreeViewElement(Node component, IJavaAgent driver, JWindow window) {
		super(component, driver, window);
	}

	@Override
	public boolean marathon_select(String value) {
		return setSelectionPath(value);
	}

	@SuppressWarnings("unchecked")
	private boolean setSelectionPath(String value) {
		TreeView<?> treeView = (TreeView<?>) getComponent();
		JSONArray pathElements = new JSONArray(value);
		List<List<TreeItem<?>>> paths = new ArrayList<>();
		for (int i = 0; i < pathElements.length(); i++) {
			paths.add(getPath(pathElements.getString(i)));
		}
		treeView.getSelectionModel().clearSelection();
		for (List<TreeItem<?>> path : paths) {
			@SuppressWarnings("rawtypes")
			TreeItem treeItem = (TreeItem<?>) path.get(path.size() - 1);
			treeView.scrollTo(treeView.getRow(treeItem));
			treeView.getSelectionModel().select(treeItem);
		}
		return true;
	}

	private List<TreeItem<?>> getPath(String path) {
		TreeView<?> treeView = (TreeView<?>) getComponent();
		String[] tokens = path.substring(1).split("(?<!\\\\)/");
		Object rootNode = treeView.getRoot();
		int start = treeView.isShowRoot() ? 1 : 0;
		List<TreeItem<?>> treePath = new ArrayList<TreeItem<?>>();
		treePath.add((TreeItem<?>) rootNode);
		StringBuilder searchedPath = new StringBuilder();
		if (treeView.isShowRoot()) {
			String rootNodeText = unescapeSpecialCharacters(tokens[0]);
			searchedPath.append("/" + rootNodeText);
			assertTrue("TreeView does not have a root node!", rootNode != null);
			assertTrue(
					"TreeView root node does not match: Expected </" + getPathText(treePath) + "> Actual: <"
							+ searchedPath.toString() + ">",
					searchedPath.toString().equals("/" + getPathText(treePath)));
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
		return treePath;
	}

	private String getPathText(List<TreeItem<?>> treePath) {
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

	private void assertTrue(String message, boolean b) {
		if (!b)
			throw new RuntimeException(message);
	}

	public String unescapeSpecialCharacters(String name) {
		return name.replaceAll("\\\\/", "/");
	}
}
