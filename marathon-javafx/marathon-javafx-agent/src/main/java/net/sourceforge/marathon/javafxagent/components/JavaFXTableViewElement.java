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
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXTableViewElement extends JavaFXElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXTableViewElement.class.getName());

    public JavaFXTableViewElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("mnth-cell")) {
            return Arrays.asList(
                    new JavaFXTableCellElement(this, ((Integer) params[0]).intValue() - 1, ((Integer) params[1]).intValue() - 1));
        } else if (selector.equals("all-cells")) {
            TableView<?> tableView = (TableView<?>) getComponent();
            int rowCount = tableView.getItems().size();
            int columnCount = tableView.getColumns().size();
            ArrayList<IJavaFXElement> r = new ArrayList<>();
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < columnCount; j++) {
                    r.add(new JavaFXTableCellElement(this, i, j));
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
            JavaFXTableCellElement e = new JavaFXTableCellElement(this, cell.getInt(0), getColumnIndex(cell.getString(1)));
            if (!(boolean) e._makeVisible()) {
                return Arrays.asList();
            }
            r.add(e);
        }
        return r;
    }

    @Override public boolean marathon_select(String value) {
        TableView<?> tableView = (TableView<?>) node;
        TableViewSelectionModel<?> selectionModel = tableView.getSelectionModel();
        if ("".equals(value)) {
            selectionModel.clearSelection();
            return true;
        } else if (value.equals("all")) {
            int rowSize = tableView.getItems().size();
            for (int i = 0; i < rowSize; i++) {
                selectionModel.select(i);
            }
            return true;
        } else if (selectionModel.isCellSelectionEnabled()) {
            selectCells(tableView, value);
            return true;
        } else {
            int[] selectedRows = getSelectedRows(value);
            selectionModel.clearSelection();
            for (int rowIndex : selectedRows) {
                if (getVisibleCellAt(tableView, rowIndex, tableView.getColumns().size() - 1) == null) {
                    tableView.scrollTo(rowIndex);
                }
                selectionModel.select(rowIndex);
            }
            return true;
        }
    }

    @Override public String _getText() {
        return getSelection((TableView<?>) getComponent());
    }
}
