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
package net.sourceforge.marathon.jxbrowser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.json.JSONObject;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElementFactory;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXHomePageElement extends JavaFXElement {

    public static final Logger lOGGER = Logger.getLogger(JavaFXHomePageElement.class.getName());
    private int index;

    public JavaFXHomePageElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("select-by-properties")) {
            return findItemByProperties(new JSONObject((String) params[0]));
        }
        return super.getByPseudoElement(selector, params);
    }

    private List<IJavaFXElement> findItemByProperties(JSONObject o) {
        List<IJavaFXElement> r = new ArrayList<>();
        if (o.has("select")) {
            if (o.getString("select") != null) {
                index = Integer.parseInt(o.getString("select"));
                JavaFXHomePageCellElemenet e = new JavaFXHomePageCellElemenet(this, o.getString("select"));
                if (!(boolean) e._makeVisible()) {
                    return Arrays.asList();
                }
                r.add(e);
            }
        }
        return r;
    }

    @Override public boolean marathon_select(String value) {
        ListView<?> listView = (ListView<?>) node;
        ListCell<?> cell = getVisibleCellAt(listView, index);
        Button button = getButton(cell, value);
        if (button == null) {
            listView.scrollTo(index);
            return false;
        }
        IJavaFXElement ele = JavaFXElementFactory.createElement(button, driver, window);
        ele.click();
        return true;
    }

    private Button getButton(ListCell<?> cell, String text) {
        Set<Node> nodes = cell.lookupAll("*");
        for (Node node : nodes) {
            if (node instanceof Button) {
                String bText = ((Button) node).getText();
                if (bText.equals(text)) {
                    return (Button) node;
                }
            }
        }
        return null;
    }
}
