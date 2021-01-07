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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.PickResult;
import javafx.scene.text.Text;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.IPseudoElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElementFactory;
import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.json.JSONObject;

public class JavaFXTreeTableViewCellElement extends JavaFXElement implements IPseudoElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXTreeTableViewCellElement.class.getName());

    private String path;
    private int viewColumn;
    private JavaFXElement parent;

    public JavaFXTreeTableViewCellElement(JavaFXElement parent, int viewRow, int viewColumn) {
        super(parent);
        this.parent = parent;
        this.path = rowToTreeTablePath(viewRow);
        this.viewColumn = viewColumn;
    }

    public JavaFXTreeTableViewCellElement(JavaFXElement parent, String path, int viewColumn) {
        super(parent);
        this.parent = parent;
        this.path = path;
        this.viewColumn = viewColumn;
    }

    private String rowToTreeTablePath(int rowView) {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getComponent();
        TreeItem<?> treeItem = treeTableView.getTreeItem(rowView);
        if (treeItem == null) {
            throw new RuntimeException("Trying to create a tree item for row " + rowView + " which is invalid");
        }
        return getTextForTreeTableNode(treeTableView, treeItem);
    }

    @Override
    public IJavaFXElement getParent() {
        return parent;
    }

    @Override
    public String createHandle() {
        JSONObject pa = new JSONObject().put("cell", new JSONArray().put(path).put(getViewColumnName()));
        JSONObject o = new JSONObject().put("selector", "select-by-properties").put("parameters",
                new JSONArray().put(new JSONObject().put("select", pa.toString()).toString()));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override
    public Node getPseudoComponent() {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getComponent();
        int rowIndex = getTreeTableNodeIndex(treeTableView, path);
        treeTableView.scrollToColumnIndex(viewColumn);
        treeTableView.scrollTo(rowIndex);
        return getCellAt(treeTableView, rowIndex, viewColumn);
    }

    @Override
    public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getComponent();
        int rowIndex = getTreeTableNodeIndex(treeTableView, path);
        if (getVisibleCellAt(treeTableView, rowIndex, viewColumn) == null) {
            treeTableView.scrollToColumnIndex(viewColumn);
            treeTableView.scrollTo(rowIndex);
        }
        if (selector.equals("editor")) {
            return Arrays.asList(JavaFXElementFactory.createElement(getEditor(), driver, window));
        }
        return super.getByPseudoElement(selector, params);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Node getEditor() {
        TreeTableCell cell = (TreeTableCell) getPseudoComponent();
        cell.getTreeTableView().edit(cell.getTreeTableRow().getIndex(), cell.getTableColumn());
        Node cellComponent = cell.getGraphic();
        cellComponent.getProperties().put("marathon.celleditor", true);
        cellComponent.getProperties().put("marathon.cell", cell);
        return cellComponent;
    }

    public int getViewColumn() {
        return viewColumn + 1;
    }

    public String getViewColumnName() {
        String columnName = getTreeTableColumnName((TreeTableView<?>) parent.getComponent(), viewColumn);
        if (columnName == null) {
            return "" + (viewColumn + 1);
        }
        return columnName;
    }

    public String getColumn() {
        String columnName = getTreeTableColumnName((TreeTableView<?>) parent.getComponent(), viewColumn);
        if (columnName == null) {
            return "" + viewColumn;
        }
        return columnName;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String _getText() {
        TreeTableCell<?, ?> cell = (TreeTableCell<?, ?>) getPseudoComponent();
        JavaFXElement cellElement = (JavaFXElement) JavaFXElementFactory.createElement(cell, driver, window);
        return cellElement._getValue();
    }

    @Override
    public Point2D _getMidpoint() {
        Node cell = getPseudoComponent();
        Bounds boundsInParent = cell.getBoundsInParent();
        double x = boundsInParent.getWidth() / 2;
        double y = boundsInParent.getHeight() / 2;
        return cell.getParent().localToParent(cell.localToParent(x, y));
    }

    @Override
    public void click(int button, Node target, PickResult pickResult, int clickCount, double xoffset, double yoffset) {
        Node cell = getPseudoComponent();
        target = getTextObj((TreeTableCell<?, ?>) cell);
        Point2D targetXY = target.localToParent(xoffset, yoffset);
        targetXY = node.localToScene(targetXY);
        super.click(button, target, new PickResult(target, targetXY.getX(), targetXY.getY()), clickCount, xoffset, yoffset);
    }

    private Node getTextObj(TreeTableCell<?, ?> cell) {
        for (Node child : cell.getChildrenUnmodifiable()) {
            if (child instanceof Text) {
                return child;
            }
        }
        return cell;
    }

    @Override
    public Object _makeVisible() {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getComponent();
        TreeTableCell<?, ?> cell = (TreeTableCell<?, ?>) getPseudoComponent();
        if (cell == null) {
            treeTableView.scrollToColumnIndex(viewColumn);
            treeTableView.scrollTo(getTreeTableNodeIndex(treeTableView, path));
            return false;
        }
        return true;
    }
}
