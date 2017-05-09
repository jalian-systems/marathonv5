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
package net.sourceforge.marathon.javafxrecorder.component;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.logging.Logger;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeCell;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.web.HTMLEditor;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXComponentFactory {
    
    public static final Logger LOGGER = Logger.getLogger(RFXComponentFactory.class.getName());

    private JSONOMapConfig omapConfig;

    private static class InstanceCheckFinder implements IRFXComponentFinder {
        private Class<? extends Node> componentKlass;
        private Class<? extends RFXComponent> rComponentKlass;
        private IRecordOn recordOn;

        public InstanceCheckFinder(Class<? extends Node> componentKlass, Class<? extends RFXComponent> javaElementKlass,
                IRecordOn recordOn) {
            this.componentKlass = componentKlass;
            this.rComponentKlass = javaElementKlass;
            this.recordOn = recordOn;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * net.sourceforge.marathon.javaagent.IJavaElementFinder#get(java.awt
         * .Node)
         */
        @Override public Class<? extends RFXComponent> get(Node component) {
            if (componentKlass.isInstance(component)) {
                return rComponentKlass;
            }
            return null;
        }

        @Override public Node getRecordOn(Node component, Point2D point) {
            if (recordOn != null) {
                return recordOn.getRecordOn(component, point);
            }
            return null;
        }
    }

    private static LinkedList<IRFXComponentFinder> entries = new LinkedList<IRFXComponentFinder>();

    static {
    }

    public static void add(Class<? extends Node> componentKlass, Class<? extends RFXComponent> rComponentKlass,
            IRecordOn recordOn) {
        add(new InstanceCheckFinder(componentKlass, rComponentKlass, recordOn));
    }

    public static void add(IRFXComponentFinder f) {
        entries.addFirst(f);
    }

    public static void reset() {
        entries.clear();
        add(Node.class, RFXUnknownComponent.class, null);
        add(Region.class, RFXIgnoreComponent.class, null);
        add(TextInputControl.class, RFXTextInputControl.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof TextInputControl) {
                        return parent;
                    }
                    parent = parent.getParent();
                }
                return null;
            }
        });
        add(HTMLEditor.class, RFXHTMLEditor.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof HTMLEditor) {
                        return parent;
                    }
                    parent = parent.getParent();
                }
                return null;
            }
        });
        add(ButtonBase.class, RFXButtonBase.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof ButtonBase) {
                        return parent;
                    }
                    parent = parent.getParent();
                }
                return null;
            }
        });
        add(MenuBar.class, RFXMenuBar.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof MenuBar) {
                        return parent;
                    }
                    parent = parent.getParent();
                }
                return null;
            }
        });
        add(CheckBox.class, RFXCheckBox.class, null);
        add(ToggleButton.class, RFXToggleButton.class, null);
        add(Slider.class, RFXSlider.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof Slider) {
                        return parent;
                    }
                    parent = parent.getParent();
                }
                return null;
            }
        });
        add(Spinner.class, RFXSpinner.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof Spinner) {
                        return parent;
                    }
                    parent = parent.getParent();
                }
                return null;
            }
        });
        add(TitledPane.class, RFXTitledPane.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                Node parent = component;
                if (hasTitleRegion(component)) {
                    while (parent != null) {
                        if (parent instanceof TitledPane) {
                            return parent;
                        }
                        parent = parent.getParent();
                    }
                }
                return null;
            }

            private boolean hasTitleRegion(Node component) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof StackPane && parent.getStyleClass().contains("title")) {
                        return true;
                    }
                    parent = parent.getParent();
                }
                return false;
            }
        });
        add(SplitPane.class, RFXSplitPane.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                Node parent = component;
                if (hasDivider(component)) {
                    while (parent != null) {
                        if (parent instanceof SplitPane) {
                            return parent;
                        }
                        parent = parent.getParent();
                    }
                }
                return null;
            }

            private boolean hasDivider(Node component) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof StackPane && parent.getStyleClass().contains("split-pane-divider")) {
                        return true;
                    }
                    parent = parent.getParent();
                }
                return false;
            }
        });
        add(ProgressBar.class, RFXProgressBar.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof ProgressBar) {
                        return parent;
                    }
                    parent = parent.getParent();
                }
                return null;
            }
        });
        add(TabPane.class, RFXTabPane.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                Node parent = component;
                if (hasTab(component)) {
                    while (parent != null) {
                        if (parent instanceof TabPane) {
                            return parent;
                        }
                        parent = parent.getParent();
                    }
                }
                return null;
            }

            private boolean hasTab(Node component) {
                Node parent = component;
                if (hasTabContainer(component)) {
                    while (parent != null) {
                        if (parent instanceof Label && parent.getStyleClass().contains("tab-label")) {
                            return true;
                        }
                        parent = parent.getParent();
                    }
                }
                return false;
            }

            private boolean hasTabContainer(Node component) {
                Node parent = component;
                while (parent != null) {
                    if (parent.getStyleClass().contains("tab-container")) {
                        return true;
                    }
                    parent = parent.getParent();
                }
                return false;
            }
        });
        add(ChoiceBox.class, RFXChoiceBox.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof ChoiceBox<?>) {
                        return parent;
                    }
                    parent = parent.getParent();
                }
                return null;
            }
        });
        add(ComboBox.class, RFXComboBox.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof ComboBox<?>) {
                        return parent;
                    }
                    parent = parent.getParent();
                }
                return null;
            }
        });
        add(ColorPicker.class, RFXColorPicker.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof ColorPicker) {
                        return parent;
                    }
                    parent = parent.getParent();
                }
                return null;
            }
        });
        add(DatePicker.class, RFXDatePicker.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof DatePicker) {
                        return parent;
                    }
                    parent = parent.getParent();
                }
                return null;
            }
        });
        add(ListView.class, RFXListView.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                if (hasListCellParent(component)) {
                    Node parent = component;
                    while (parent != null) {
                        if (parent instanceof ListView) {
                            return parent;
                        }
                        parent = parent.getParent();
                    }
                }
                return null;
            }

            private boolean hasListCellParent(Node component) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof ListCell<?>) {
                        return true;
                    }
                    parent = parent.getParent();
                }
                return false;
            }
        });
        add(TreeView.class, RFXTreeView.class, new IRecordOn() {
            @Override public Node getRecordOn(Node component, Point2D point) {
                if (hasTreeCellParent(component)) {
                    Node parent = component;
                    while (parent != null) {
                        if (parent instanceof TreeView) {
                            return parent;
                        }
                        parent = parent.getParent();
                    }
                }
                return null;
            }

            private boolean hasTreeCellParent(Node component) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof TreeCell<?>) {
                        return true;
                    }
                    parent = parent.getParent();
                }
                return false;
            }
        });
        add(TableView.class, RFXTableView.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                if (hasTableCellParent(component)) {
                    Node parent = component;
                    while (parent != null) {
                        if (parent instanceof TableView) {
                            return parent;
                        }
                        parent = parent.getParent();
                    }
                }
                return null;
            }

            private boolean hasTableCellParent(Node component) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof TableCell<?, ?>) {
                        return true;
                    }
                    parent = parent.getParent();
                }
                return false;
            }
        });
        add(TreeTableView.class, RFXTreeTableView.class, new IRecordOn() {

            @Override public Node getRecordOn(Node component, Point2D point) {
                if (hasTreeTableCellParent(component)) {
                    Node parent = component;
                    while (parent != null) {
                        if (parent instanceof TreeTableView) {
                            return parent;
                        }
                        parent = parent.getParent();
                    }
                }
                return null;
            }

            private boolean hasTreeTableCellParent(Node component) {
                Node parent = component;
                while (parent != null) {
                    if (parent instanceof TreeTableCell<?, ?>) {
                        return true;
                    }
                    parent = parent.getParent();
                }
                return false;
            }
        });
        add(ChoiceBoxListCell.class, RFXChoiceBoxListCell.class, null);
        add(CheckBoxListCell.class, RFXCheckBoxListCell.class, null);
        add(ComboBoxListCell.class, RFXComboBoxListCell.class, null);
        add(ChoiceBoxTreeCell.class, RFXChoiceBoxTreeCell.class, null);
        add(TreeCell.class, RFXTreeCell.class, null);
        add(CheckBoxTreeCell.class, RFXCheckBoxTreeCell.class, null);
        add(ComboBoxTreeCell.class, RFXComboBoxTreeCell.class, null);
        add(TableCell.class, RFXTableCell.class, null);
        add(CheckBoxTableCell.class, RFXCheckBoxTableCell.class, null);
        add(ComboBoxTableCell.class, RFXComboBoxTableCell.class, null);
        add(ChoiceBoxTableCell.class, RFXChoiceBoxTableCell.class, null);
        add(TreeTableCell.class, RFXTreeTableCell.class, null);
        add(CheckBoxTreeTableCell.class, RFXCheckBoxTreeTableCell.class, null);
        add(ComboBoxTreeTableCell.class, RFXComboBoxTreeTableCell.class, null);
        add(ChoiceBoxTreeTableCell.class, RFXChoiceBoxTreeTableCell.class, null);
    }

    static {
        reset();
    }

    public RFXComponentFactory(JSONOMapConfig objectMapConfiguration) {
        this.omapConfig = objectMapConfiguration;
    }

    public RFXComponent findRComponent(Node parent, Point2D point, IJSONRecorder recorder) {
        return findRawRComponent(getComponent(parent, point), point, recorder);
    }

    public RFXComponent findRawRComponent(Node source, Point2D point, IJSONRecorder recorder) {
        for (IRFXComponentFinder entry : entries) {
            Class<? extends RFXComponent> k = entry.get(source);
            if (k == null) {
                continue;
            }
            try {
                Constructor<? extends RFXComponent> cons = k.getConstructor(Node.class, JSONOMapConfig.class, Point2D.class,
                        IJSONRecorder.class);
                if (point != null) {
                    point = source.sceneToLocal(point);
                }
                return cons.newInstance(source, omapConfig, point, recorder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Node getComponent(Node component, Point2D point) {
        for (IRFXComponentFinder entry : entries) {
            Node recordOn = entry.getRecordOn(component, point);
            if (recordOn != null) {
                return recordOn;
            }
        }
        return component;
    }
}
