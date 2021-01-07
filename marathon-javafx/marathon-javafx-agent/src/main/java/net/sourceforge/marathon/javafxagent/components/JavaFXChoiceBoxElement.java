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
import javafx.scene.control.ChoiceBox;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;
import net.sourceforge.marathon.json.JSONArray;

public class JavaFXChoiceBoxElement extends JavaFXElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXChoiceBoxElement.class.getName());

    public JavaFXChoiceBoxElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override
    public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("nth-option")) {
            return Arrays.asList(new JavaFXChoiceBoxOptionElement(this, ((Integer) params[0]).intValue() - 1));
        } else if (selector.equals("all-options") || selector.equals("all-cells")) {
            ChoiceBox<?> listView = (ChoiceBox<?>) getComponent();
            ArrayList<IJavaFXElement> r = new ArrayList<>();
            int nItems = listView.getItems().size();
            for (int i = 0; i < nItems; i++) {
                r.add(new JavaFXChoiceBoxOptionElement(this, i));
            }
            return r;
        }
        return super.getByPseudoElement(selector, params);
    }

    @Override
    public boolean marathon_select(String value) {
        ChoiceBox<?> choiceBox = (ChoiceBox<?>) getComponent();
        String text = stripHTMLTags(value);
        int selectedItem = getChoiceBoxItemIndex(choiceBox, text);
        if (selectedItem == -1) {
            return false;
        }
        choiceBox.getSelectionModel().select(selectedItem);
        return true;
    }

    public String getContent() {
        return new JSONArray(getContent((ChoiceBox<?>) getComponent())).toString();
    }

    @Override
    public String _getText() {
        return getChoiceBoxText((ChoiceBox<?>) getComponent(),
                ((ChoiceBox<?>) getComponent()).getSelectionModel().getSelectedIndex());
    }
}
