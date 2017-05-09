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
import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaAgentException;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXCheckBoxElement extends JavaFXElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXCheckBoxElement.class.getName());

    public static final String[] states = new String[] { "unchecked", "indeterminate", "checked" };

    public JavaFXCheckBoxElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        CheckBox cb = (CheckBox) node;
        Boolean isCellEditor = (Boolean) cb.getProperties().get("marathon.celleditor");
        if (isCellEditor != null && isCellEditor) {
            String[] split = value.split(":");
            value = split[1];
        }
        if (!isValidState(value)) {
            throw new JavaAgentException(value + " is not a valid state for CheckBox.", null);
        }
        int selection = 0;
        for (String state : states) {
            if (state.equalsIgnoreCase(value)) {
                break;
            }
            selection++;
        }
        int current = getSelection(cb);
        if (cb.isAllowIndeterminate()) {
            if (current != selection) {
                int nclicks = selection - current;
                if (nclicks < 0) {
                    nclicks += 3;
                }
                for (int i = 0; i < nclicks; i++) {
                    click();
                }
            }
        } else {
            if (current != selection) {
                click();
            }
        }
        return true;
    }

    private boolean isValidState(String value) {
        return Arrays.asList(states).contains(value);
    }

    @Override public String _getText() {
        return getCheckBoxText((CheckBox) getComponent());
    }

    @Override public String _getValue() {
        int selection = getSelection((CheckBox) node);
        return JavaFXCheckBoxElement.states[selection];
    }
}
