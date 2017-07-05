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

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.input.PickResult;
import javafx.scene.text.Text;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.IPseudoElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElementFactory;

public class JavaFXTableCellElement extends JavaFXElement implements IPseudoElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXTableCellElement.class.getName());

    private JavaFXElement parent;
    private int viewRow;
    private int viewColumn;

    public JavaFXTableCellElement(JavaFXElement parent, int row, int column) {
        super(parent);
        this.parent = parent;
        this.viewRow = row;
        this.viewColumn = column;
    }

    @Override public IJavaFXElement getParent() {
        return parent;
    }

    @Override public String createHandle() {
        JSONObject pa = new JSONObject().put("cell", new JSONArray().put(viewRow).put(getViewColumnName()));
        JSONObject o = new JSONObject().put("selector", "select-by-properties").put("parameters",
                new JSONArray().put(new JSONObject().put("select", pa.toString()).toString()));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override public Node getPseudoComponent() {
        TableView<?> tableView = (TableView<?>) parent.getComponent();
        TableCell<?, ?> cell = getCellAt(tableView, viewRow, viewColumn);
        if (cell != null) {
            tableView.scrollTo(viewRow);
            tableView.scrollToColumnIndex(viewColumn);
        }
        return cell;
    }

    @Override public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        TableView<?> tableView = (TableView<?>) getComponent();
        if (getVisibleCellAt(tableView, viewRow, viewColumn) == null) {
            tableView.scrollTo(viewRow);
            tableView.scrollToColumnIndex(viewColumn);
            return Arrays.asList();
        }
        if (selector.equals("editor")) {
            return Arrays.asList(JavaFXElementFactory.createElement(getEditor(), driver, window));
        }
        return super.getByPseudoElement(selector, params);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" }) private Node getEditor() {
        TableCell cell = (TableCell) getPseudoComponent();
        cell.getTableView().edit(viewRow, cell.getTableColumn());
        Node cellComponent = cell.getGraphic();
        cellComponent.getProperties().put("marathon.celleditor", true);
        cellComponent.getProperties().put("marathon.cell", cell);
        return cellComponent;
    }

    public int getViewRow() {
        return viewRow + 1;
    }

    public int getViewColumn() {
        return viewColumn + 1;
    }

    public String getViewColumnName() {
        String columnName = getColumnName((TableView<?>) parent.getComponent(), viewColumn);
        if (columnName == null) {
            return "" + (viewColumn + 1);
        }
        return columnName;
    }

    public int getRow() {
        return viewRow;
    }

    public String getColumn() {
        String columnName = getColumnName((TableView<?>) parent.getComponent(), viewColumn);
        if (columnName == null) {
            return "" + viewColumn;
        }
        return columnName;
    }

    public int getCol() {
        return viewColumn;
    }

    @Override public String _getText() {
        TableCell<?, ?> cell = (TableCell<?, ?>) getPseudoComponent();
        JavaFXElement cellElement = (JavaFXElement) JavaFXElementFactory.createElement(cell, driver, window);
        return cellElement._getValue();
    }

    @Override public Point2D _getMidpoint() {
        Node cell = getPseudoComponent();
        Bounds boundsInParent = cell.getBoundsInParent();
        double x = boundsInParent.getWidth() / 2;
        double y = boundsInParent.getHeight() / 2;
        return cell.getParent().localToParent(cell.localToParent(x, y));
    }

    @Override public void click(int button, Node target, PickResult pickResult, int clickCount, double xoffset, double yoffset) {
        Node cell = getPseudoComponent();
        target = getTextObj((TableCell<?, ?>) cell);
        Point2D targetXY = target.localToParent(xoffset, yoffset);
        targetXY = node.localToScene(targetXY);
        super.click(button, target, new PickResult(target, targetXY.getX(), targetXY.getY()), clickCount, xoffset, yoffset);
    }

    private Node getTextObj(TableCell<?, ?> cell) {
        for (Node child : cell.getChildrenUnmodifiable()) {
            if (child instanceof Text) {
                return child;
            }
        }
        return cell;
    }

    @Override public Object _makeVisible() {
        TableView<?> tableView = (TableView<?>) parent.getComponent();
        Node cell = getPseudoComponent();
        if (cell == null || tableView.getItems() == null) {
            tableView.scrollTo(viewRow);
            tableView.scrollToColumnIndex(viewColumn);
            return false;
        }
        return true;
    }
}
