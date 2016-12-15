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

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.cell.CheckBoxTreeCell;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXCheckBoxTreeCellElement extends JavaFXElement {

    public JavaFXCheckBoxTreeCellElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public String _getValue() {
        @SuppressWarnings("rawtypes")
        CheckBoxTreeCell cell = (CheckBoxTreeCell) getComponent();
        @SuppressWarnings("unchecked")
        ObservableValue<Boolean> call = (ObservableValue<Boolean>) cell.getSelectedStateCallback().call(cell.getTreeItem());
        int selection = call.getValue() ? 2 : 0;
        String cellText = cell.getText();
        if (cellText == null) {
            cellText = "";
        }
        String text = cellText + ":" + JavaFXCheckBoxElement.states[selection];
        return text;
    }
}
