package net.sourceforge.marathon.javafxagent.components;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.IPseudoElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;

public class JavaFXTreeViewNodeElement extends JavaFXElement implements IJavaFXElement, IPseudoElement {

	private JavaFXTreeViewElement parent;
	private String path;

	public JavaFXTreeViewNodeElement(JavaFXTreeViewElement parent, String path) {
		super(parent);
		this.parent = parent;
		this.path = path;
	}

	public JavaFXTreeViewNodeElement(JavaFXTreeViewElement parent, int row) {
		super(parent);
		this.parent = parent ;
		this.path = rowToPath(row);
	}

	private String rowToPath(int row) {
		TreeView<?> treeView = (TreeView<?>) getComponent();
		TreeItem<?> treeItem = treeView.getTreeItem(row);
		if(treeItem == null)
			throw new RuntimeException("Trying to create a tree item for row " + row + " which is invalid");
		return getTextForNode(treeView, treeItem);
	}

	@Override
	public IJavaFXElement getParent() {
		return parent;
	}

	@Override
	public String createHandle() {
		JSONObject o = new JSONObject().put("selector", "select-by-properties").put("parameters",
				new JSONArray().put(new JSONObject().put("select", path).toString()));
		return parent.getHandle() + "#" + o.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Node getPseudoComponent() {
		TreeView<?> treeView = (TreeView<?>) getComponent();
		@SuppressWarnings("rawtypes")
		TreeItem item = getPath(treeView, path);
		if (item == null)
			return null;
		treeView.scrollTo(treeView.getRow(item));
		Set<Node> lookupAll = treeView.lookupAll(".tree-cell");
		for (Node node : lookupAll) {
			TreeCell<?> cell = (TreeCell<?>) node;
			if (cell.getTreeItem() == item)
				return cell;
		}
		return null;
	}

	@Override
	public void _moveto() {
		Point2D midpoint = _getMidpoint();
		parent._moveto(midpoint.getX(), midpoint.getY());
	}

	@Override
	public void _moveto(double xoffset, double yoffset) {
		Node cell = getPseudoComponent();
		Point2D pCoords = cell.localToParent(xoffset, yoffset);
		parent._moveto(pCoords.getX(), pCoords.getY());
	}

	@Override
	public Point2D _getMidpoint() {
		Node cell = getPseudoComponent();
		Bounds boundsInParent = cell.getBoundsInParent();
		double x = boundsInParent.getWidth() / 2;
		double y = boundsInParent.getHeight() / 2;
		return cell.localToParent(x, y);
	}

	@Override
	public Object _makeVisible() {
		getPseudoComponent();
		return null;
	}
	
	public String getPath() {
		return path;
	}
}
