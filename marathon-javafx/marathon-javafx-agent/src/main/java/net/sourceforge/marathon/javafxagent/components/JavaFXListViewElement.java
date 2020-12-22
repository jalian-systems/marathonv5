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

import javafx.scene.Node;
import javafx.scene.control.ListView;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;
import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.json.JSONObject;

public class JavaFXListViewElement extends JavaFXElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXListViewElement.class.getName());

    public JavaFXListViewElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override
    public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("nth-item")) {
            return Arrays.asList(new JavaFXListViewItemElement(this, ((Integer) params[0]).intValue() - 1));
        } else if (selector.equals("all-items") || selector.equals("all-cells")) {
            ListView<?> listView = (ListView<?>) getComponent();
            ArrayList<IJavaFXElement> r = new ArrayList<>();
            int nItems = listView.getItems().size();
            for (int i = 0; i < nItems; i++) {
                r.add(new JavaFXListViewItemElement(this, i));
            }
            return r;
        } else if (selector.equals("select-by-properties")) {
            return findItemByProperties(new JSONObject((String) params[0]));
        }
        return super.getByPseudoElement(selector, params);
    }

    private List<IJavaFXElement> findItemByProperties(JSONObject o) {
        List<IJavaFXElement> r = new ArrayList<>();
        if (o.has("select")) {
            if (o.getString("select") != null) {
                JavaFXListViewItemElement e = new JavaFXListViewItemElement(this, o.getString("select"));
                if (!((boolean) e._makeVisible()))
                    return Arrays.asList();
                r.add(e);
            }
        }
        return r;
    }

    @Override
    public boolean marathon_select(String value) {
        return setItemSelection(value);
    }

    private boolean setItemSelection(String value) {
        ListView<?> listView = (ListView<?>) getComponent();
        JSONArray items = new JSONArray(value);
        List<Integer> listItemIndices = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            listItemIndices.add(getListItemIndex(listView, items.getString(i)));
        }
        listView.getSelectionModel().clearSelection();
        for (int i = 0; i < listItemIndices.size(); i++) {
            Integer index = listItemIndices.get(i);
            if (getVisibleCellAt(listView, index) == null) {
                listView.scrollTo(index);
            }
            listView.getSelectionModel().select(index);

        }
        return true;
    }

    @Override
    public String _getText() {
        return getListSelectionText((ListView<?>) getComponent());
    }
}
