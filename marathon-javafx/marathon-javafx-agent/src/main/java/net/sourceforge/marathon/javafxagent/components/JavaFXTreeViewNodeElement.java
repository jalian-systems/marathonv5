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
package net.sourceforge.marathon.javafxagent.components;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.PickResult;
import javafx.scene.text.Text;
import net.sourceforge.marathon.javafxagent.EventQueueWait;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.IPseudoElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElementFactory;
import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.json.JSONObject;

public class JavaFXTreeViewNodeElement extends JavaFXElement implements IPseudoElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXTreeViewNodeElement.class.getName());

    private JavaFXTreeViewElement parent;
    private String path;

    public JavaFXTreeViewNodeElement(JavaFXTreeViewElement parent, String path) {
        super(parent);
        this.parent = parent;
        this.path = path;
    }

    public JavaFXTreeViewNodeElement(JavaFXTreeViewElement parent, int row) {
        super(parent);
        this.parent = parent;
        this.path = rowToPath(row);
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
        if (item == null) {
            return null;
        }
        EventQueueWait.exec(() -> treeView.scrollTo(treeView.getRow(item)));
        return getCellAt(treeView, item);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        TreeView<?> treeView = (TreeView<?>) getComponent();
        @SuppressWarnings("rawtypes")
        TreeItem item = getPath(treeView, path);
        if (item == null)
            return Arrays.asList();
        if (getVisibleCellAt(treeView, item) == null) {
            EventQueueWait.exec(() -> treeView.scrollTo(treeView.getRow(item)));
            return Arrays.asList();
        }
        if (selector.equals("editor")) {
            return Arrays.asList(JavaFXElementFactory.createElement(getEditor(), driver, window));
        }
        return super.getByPseudoElement(selector, params);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Node getEditor() {
        TreeCell cell = (TreeCell) getPseudoComponent();
        TreeView treeView = (TreeView) getComponent();
        treeView.edit(cell.getTreeItem());
        Node cellComponent = cell.getGraphic();
        cellComponent.getProperties().put("marathon.celleditor", true);
        cellComponent.getProperties().put("marathon.cell", cell);
        return cellComponent;
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
        TreeView<?> treeView = (TreeView<?>) getComponent();
        Node cell = getCellAt(treeView, getPath(treeView, path));
        Bounds boundsInParent = cell.getBoundsInParent();
        double x = boundsInParent.getWidth() / 2;
        double y = boundsInParent.getHeight() / 2;
        return cell.localToParent(x, y);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object _makeVisible() {
        TreeView<?> treeView = (TreeView<?>) getComponent();
        @SuppressWarnings("rawtypes")
        TreeItem item = getPath(treeView, path);
        Node cell = getVisibleCellAt(treeView, item);
        if (cell == null) {
            treeView.scrollTo(treeView.getRow(item));
            return false;
        }
        return true;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String _getText() {
        TreeView<?> treeView = (TreeView<?>) getComponent();
        return getTextForNode(treeView, getPath(treeView, path));
    }

    @Override
    public void click(int button, Node target, PickResult pickResult, int clickCount, double xoffset, double yoffset) {
        Node cell = getPseudoComponent();
        target = getTextObj((TreeCell<?>) cell);
        Point2D targetXY = node.localToScene(xoffset, yoffset);
        super.click(button, target, new PickResult(target, targetXY.getX(), targetXY.getY()), clickCount, xoffset, yoffset);
    }

    private Node getTextObj(TreeCell<?> cell) {
        for (Node child : cell.getChildrenUnmodifiable()) {
            if (child instanceof Text) {
                return child;
            }
        }
        return cell;
    }
}
