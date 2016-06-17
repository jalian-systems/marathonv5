/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.javafxagent.components;

import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.TextInputControl;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaAgentKeys;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXTextInputControlElement extends JavaFXElement {

    public JavaFXTextInputControlElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" }) @Override public boolean marathon_select(String value) {
        TextInputControl tc = (TextInputControl) getComponent();
        Boolean isCellEditor = (Boolean) tc.getProperties().get("marathon.celleditor");
        tc.setText("");
        if (isCellEditor != null && isCellEditor) {
            super.sendKeys(value, JavaAgentKeys.ENTER);
            Cell cell = (Cell) tc.getProperties().get("marathon.cell");
            cell.commitEdit(value);
        } else {
            super.sendKeys(value);
        }
        return true;
    }

    @Override public String _getText() {
        return ((TextInputControl) getComponent()).getText();
    }
}
