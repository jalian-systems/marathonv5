package net.sourceforge.marathon.javafxagent.components;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

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
		TreeItem item = JavaFXTreeViewElement.getPath(treeView, path);
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
		super._moveto();
	}

	@Override
	public void _moveto(double xoffset, double yoffset) {
		super._moveto(xoffset, yoffset);
	}

	@Override
	public Point2D _getMidpoint() {
		return super._getMidpoint();
	}

}
