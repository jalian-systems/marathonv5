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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.Node;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXTreeTableViewElement extends JavaFXElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXTreeTableViewElement.class.getName());

    public JavaFXTreeTableViewElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override
    public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("root")) {
            return Arrays.asList(new JavaFXTreeTableViewCellElement(this, 0, 0));
        } else if (selector.equals("mnth-cell")) {
            return Arrays.asList(new JavaFXTreeTableViewCellElement(this, ((Integer) params[0]).intValue() - 1,
                    ((Integer) params[1]).intValue() - 1));
        } else if (selector.equals("all-cells")) {
            TreeTableView<?> tableView = (TreeTableView<?>) getComponent();
            int rowCount = tableView.getExpandedItemCount();
            int columnCount = tableView.getColumns().size();
            ArrayList<IJavaFXElement> r = new ArrayList<>();
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < columnCount; j++) {
                    r.add(new JavaFXTreeTableViewCellElement(this, i, j));
                }
            }
            return r;
        } else if (selector.equals("select-by-properties")) {
            return findSelectByProperties(new JSONObject((String) params[0]));
        }
        return super.getByPseudoElement(selector, params);
    }

    private List<IJavaFXElement> findSelectByProperties(JSONObject o) {
        List<IJavaFXElement> r = new ArrayList<>();
        if (o.has("select")) {
            JSONObject jo = new JSONObject((String) o.get("select"));
            JSONArray cell = (JSONArray) jo.get("cell");
            JavaFXTreeTableViewCellElement e = new JavaFXTreeTableViewCellElement(this, cell.getString(0),
                    getTreeTableColumnIndex((TreeTableView<?>) getComponent(), cell.getString(1)));
            if (!(boolean) e._makeVisible()) {
                return Arrays.asList();
            }
            r.add(e);
        }
        return r;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean marathon_select(String value) {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getComponent();
        TreeTableViewSelectionModel<?> selectionModel = treeTableView.getSelectionModel();
        if (value.equals("")) {
            selectionModel.clearSelection();
            return true;
        } else if (value.equals("all")) {
            selectionModel.clearSelection();
            if (!selectionModel.isCellSelectionEnabled()) {
                int rowCount = treeTableView.getExpandedItemCount();
                for (int i = 0; i < rowCount; i++) {
                    selectionModel.select(i);
                }
                return true;
            } else {
                selectionModel.clearSelection();
                selectionModel.selectRange(0, getTreeTableColumnAt(treeTableView, 0), treeTableView.getExpandedItemCount() - 1,
                        getTreeTableColumnAt(treeTableView, treeTableView.getColumns().size() - 1));
                return true;
            }
        } else if (!selectionModel.isCellSelectionEnabled()) {
            selectionModel.clearSelection();
            int[] selectedRows = getTreeTableSelectedRows(treeTableView, value);
            for (int selectedRow : selectedRows) {
                if (getVisibleCellAt(treeTableView, selectedRow, 0) == null) {
                    treeTableView.scrollTo(selectedRow);
                }
                selectionModel.select(selectedRow);
            }
            return true;
        } else {
            selectionModel.clearSelection();
            selectTreeTableCells(treeTableView, value);
            return true;
        }
    }

    @Override
    public String _getText() {
        return getTreeTableSelection((TreeTableView<?>) getComponent());
    }
}
