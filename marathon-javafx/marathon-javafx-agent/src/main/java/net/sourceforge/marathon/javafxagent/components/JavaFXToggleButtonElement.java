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

import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXToggleButtonElement extends JavaFXElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXToggleButtonElement.class.getName());

    public JavaFXToggleButtonElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        boolean selected = Boolean.parseBoolean(value);
        boolean current = ((ToggleButton) node).isSelected();
        if (selected != current) {
            click();
        }
        return true;
    }

    @Override public String _getText() {
        return getToggleText((ToggleButton) getComponent());
    }

}
