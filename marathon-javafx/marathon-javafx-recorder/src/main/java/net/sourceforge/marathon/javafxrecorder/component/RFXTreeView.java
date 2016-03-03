package net.sourceforge.marathon.javafxrecorder.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXTreeView extends RFXComponent {

	public RFXTreeView(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
		super(source, omapConfig, point, recorder);
	}

	@Override
	public void focusLost(RFXComponent next) {
		TreeView<?> treeView = (TreeView<?>) getComponent();
		ObservableList<?> selectedItems = treeView.getSelectionModel().getSelectedItems();
		JSONArray pa = new JSONArray();
		for (Object object : selectedItems) {
			pa.put(getTextForNode(treeView, (TreeItem<?>) object));
		}
		recorder.recordSelect(this, pa.toString());
	}

	private String getTextForNode(TreeView<?> treeView, TreeItem<?> selectedItem) {
		StringBuilder sb = new StringBuilder();
		Object[] treePath = buildTreePath(selectedItem);
		int start = treeView.isShowRoot() ? 0 : 1;
		for (int i = start; i < treePath.length; i++) {
			String pathString = escapeSpecialCharacters(getTextForNodeObject(treeView, (TreeItem<?>) treePath[i]));
			sb.append("/").append(pathString);
		}
		return sb.toString();
	}

	private String getTextForNodeObject(TreeView<?> treeView, TreeItem<?> lastPathComponent) {
		// We should be getting the *text* from the tree cell. We need to figure out how to construct
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
		if(lastPathComponent == null || lastPathComponent.getValue() == null)
			return "" ;
		return lastPathComponent.getValue().toString();
	}

	private String escapeSpecialCharacters(String name) {
		return name.replaceAll("/", "\\\\/");
	}

	private Object[] buildTreePath(TreeItem<?> selectedItem) {
		List<Object> path = new ArrayList<>();
		path.add(selectedItem);
		while (selectedItem.getParent() != null) {
			path.add(selectedItem.getParent());
			selectedItem = selectedItem.getParent();
		}
		Collections.reverse(path);
		return path.toArray();
	}
}
