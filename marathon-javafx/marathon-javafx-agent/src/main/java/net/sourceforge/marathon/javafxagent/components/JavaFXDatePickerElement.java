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

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXDatePickerElement extends JavaFXElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXDatePickerElement.class.getName());

    public JavaFXDatePickerElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        DatePicker datePicker = (DatePicker) getComponent();
        if (!value.equals("")) {
            try {
                LocalDate date = datePicker.getConverter().fromString(value);
                datePicker.setValue(date);
            } catch (Throwable t) {
                throw new DateTimeException("Invalid value for '" + value + "' for date-picker '");
            }
            return true;
        }
        return false;
    }

    @Override public String _getText() {
        return getDatePickerText((DatePicker) getComponent(), ((DatePicker) getComponent()).getValue());
    }
}
