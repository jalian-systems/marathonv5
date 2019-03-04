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
import javafx.scene.control.Cell;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTreeCell;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.util.StringConverter;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXChoiceBoxCellElement extends JavaFXElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXChoiceBoxCellElement.class.getName());

    public JavaFXChoiceBoxCellElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public String _getValue() {
        StringConverter converter = getConverter();
        Object item = ((Cell) node).getItem();
        if (converter != null) {
            return converter.toString(item);
        }
        return item.toString();
    }

    @SuppressWarnings("rawtypes")
    private StringConverter getConverter() {
        if (node instanceof ChoiceBoxListCell<?>)
            return ((ChoiceBoxListCell) node).getConverter();
        else if (node instanceof ChoiceBoxTableCell<?, ?>)
            return ((ChoiceBoxTableCell) node).getConverter();
        else if (node instanceof ChoiceBoxTreeCell<?>)
            return ((ChoiceBoxTreeCell) node).getConverter();
        else if (node instanceof ChoiceBoxTreeTableCell<?, ?>)
            return ((ChoiceBoxTreeTableCell) node).getConverter();
        return null;
    }

}
