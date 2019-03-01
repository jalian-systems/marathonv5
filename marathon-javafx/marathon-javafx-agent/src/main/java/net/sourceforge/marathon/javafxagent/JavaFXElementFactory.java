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
package net.sourceforge.marathon.javafxagent;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTreeCell;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.ComboBoxTreeCell;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;
import net.sourceforge.marathon.javafxagent.components.JavaFXCheckBoxElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXCheckBoxListCellElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXCheckBoxTableCellElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXCheckBoxTreeCellElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXCheckBoxTreeTableCell;
import net.sourceforge.marathon.javafxagent.components.JavaFXChoiceBoxElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXChoiceBoxListCellElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXChoiceBoxTableCellElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXChoiceBoxTreeCellElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXChoiceBoxTreeTableCell;
import net.sourceforge.marathon.javafxagent.components.JavaFXColorPickerElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXComboBoxElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXComboBoxListCellElemnt;
import net.sourceforge.marathon.javafxagent.components.JavaFXComboBoxTableCellElemnt;
import net.sourceforge.marathon.javafxagent.components.JavaFXComboBoxTreeCellElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXComboBoxTreeTableCell;
import net.sourceforge.marathon.javafxagent.components.JavaFXDatePickerElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXHTMLEditor;
import net.sourceforge.marathon.javafxagent.components.JavaFXListViewElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXProgressBarElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXSliderElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXSpinnerElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXSplitPaneElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXTabPaneElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXTableViewCellElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXTableViewElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXTextInputControlElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXToggleButtonElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXTreeTableCellElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXTreeTableViewElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXTreeViewElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXWebViewElement;
import net.sourceforge.marathon.javafxagent.components.richtextfx.GenericStyledArea;
import net.sourceforge.marathon.javafxagent.components.richtextfx.RichTextFXGenericStyledAreaElement;

public class JavaFXElementFactory {

    public static final Logger LOGGER = Logger.getLogger(JavaFXElementFactory.class.getName());

    public static IJavaFXElement createElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        IJavaFXElement found = window.findElementFromMap(component);
        if (found != null) {
            return found;
        }
        Class<? extends IJavaFXElement> klass = get(component);
        if (klass == null) {
            return new JavaFXElement(component, driver, window);
        }
        try {
            Constructor<? extends IJavaFXElement> constructor = klass.getConstructor(Node.class, IJavaFXAgent.class,
                    JFXWindow.class);
            IJavaFXElement newInstance = constructor.newInstance(component, driver, window);
            return newInstance;
        } catch (Exception e) {
            throw new RuntimeException("createElement failed", e);
        }
    }

    private static class InstanceCheckFinder implements IJavaElementFinder {
        private Class<? extends Node> componentKlass;
        private Class<? extends IJavaFXElement> javaElementKlass;

        public InstanceCheckFinder(Class<? extends Node> componentKlass, Class<? extends IJavaFXElement> javaElementKlass) {
            this.componentKlass = componentKlass;
            this.javaElementKlass = javaElementKlass;
        }

        @Override
        public Class<? extends IJavaFXElement> get(Node component) {
            if (componentKlass.isInstance(component)) {
                return javaElementKlass;
            }
            return null;
        }
    }

    private static LinkedList<IJavaElementFinder> entries = new LinkedList<IJavaElementFinder>();

    static {
        reset();
    }

    public static void reset() {
        add(Node.class, JavaFXElement.class);
        add(TextInputControl.class, JavaFXTextInputControlElement.class);
        add(HTMLEditor.class, JavaFXHTMLEditor.class);
        add(CheckBox.class, JavaFXCheckBoxElement.class);
        add(ToggleButton.class, JavaFXToggleButtonElement.class);
        add(Slider.class, JavaFXSliderElement.class);
        add(Spinner.class, JavaFXSpinnerElement.class);
        add(SplitPane.class, JavaFXSplitPaneElement.class);
        add(ProgressBar.class, JavaFXProgressBarElement.class);
        add(ChoiceBox.class, JavaFXChoiceBoxElement.class);
        add(ColorPicker.class, JavaFXColorPickerElement.class);
        add(ComboBox.class, JavaFXComboBoxElement.class);
        add(DatePicker.class, JavaFXDatePickerElement.class);
        add(TabPane.class, JavaFXTabPaneElement.class);
        add(ListView.class, JavaFXListViewElement.class);
        add(TreeView.class, JavaFXTreeViewElement.class);
        add(TableView.class, JavaFXTableViewElement.class);
        add(TreeTableView.class, JavaFXTreeTableViewElement.class);
        add(CheckBoxListCell.class, JavaFXCheckBoxListCellElement.class);
        add(ChoiceBoxListCell.class, JavaFXChoiceBoxListCellElement.class);
        add(ComboBoxListCell.class, JavaFXComboBoxListCellElemnt.class);
        add(CheckBoxTreeCell.class, JavaFXCheckBoxTreeCellElement.class);
        add(ChoiceBoxTreeCell.class, JavaFXChoiceBoxTreeCellElement.class);
        add(ComboBoxTreeCell.class, JavaFXComboBoxTreeCellElement.class);
        add(TableCell.class, JavaFXTableViewCellElement.class);
        add(CheckBoxTableCell.class, JavaFXCheckBoxTableCellElement.class);
        add(ChoiceBoxTableCell.class, JavaFXChoiceBoxTableCellElement.class);
        add(ComboBoxTableCell.class, JavaFXComboBoxTableCellElemnt.class);
        add(TreeTableCell.class, JavaFXTreeTableCellElement.class);
        add(CheckBoxTreeTableCell.class, JavaFXCheckBoxTreeTableCell.class);
        add(ChoiceBoxTreeTableCell.class, JavaFXChoiceBoxTreeTableCell.class);
        add(ComboBoxTreeTableCell.class, JavaFXComboBoxTreeTableCell.class);
        add(WebView.class, JavaFXWebViewElement.class);
        add(GenericStyledArea.GENERIC_STYLED_AREA_CLASS, RichTextFXGenericStyledAreaElement.class);
    }

    public static Class<? extends IJavaFXElement> get(Node component) {
        for (IJavaElementFinder entry : entries) {
            Class<? extends IJavaFXElement> k = entry.get(component);
            if (k != null) {
                return k;
            }
        }
        return null;
    }

    public static void add(Class<? extends Node> component, Class<? extends IJavaFXElement> javaelement) {
        add(new InstanceCheckFinder(component, javaelement));
    }

    @SuppressWarnings("unchecked")
    public static void add(String componentName, Class<? extends IJavaFXElement> javaelement) {
        Class<? extends Node> component;
        try {
            component = (Class<? extends Node>) Class.forName(componentName);
            add(new InstanceCheckFinder(component, javaelement));
        } catch (ClassNotFoundException e) {
            return;
        }
    }

    public static void add(IJavaElementFinder e) {
        entries.addFirst(e);
    }
}
