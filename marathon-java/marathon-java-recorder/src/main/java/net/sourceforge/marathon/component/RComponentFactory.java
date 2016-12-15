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
package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.lang.reflect.Constructor;
import java.util.LinkedList;

import javax.swing.AbstractButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;

import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RComponentFactory {
    private JSONOMapConfig omapConfig;

    private static class InstanceCheckFinder implements IRComponentFinder {
        private Class<? extends Component> componentKlass;
        private Class<? extends RComponent> rComponentKlass;

        public InstanceCheckFinder(Class<? extends Component> componentKlass, Class<? extends RComponent> javaElementKlass) {
            this.componentKlass = componentKlass;
            this.rComponentKlass = javaElementKlass;
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * net.sourceforge.marathon.javaagent.IJavaElementFinder#get(java.awt
         * .Component)
         */
        @Override public Class<? extends RComponent> get(Component component) {
            if (componentKlass.isInstance(component)) {
                return rComponentKlass;
            }
            return null;
        }
    }

    private static LinkedList<IRComponentFinder> entries = new LinkedList<IRComponentFinder>();

    static {
    }

    public static void add(Class<? extends Component> componentKlass, Class<? extends RComponent> rComponentKlass) {
        add(new InstanceCheckFinder(componentKlass, rComponentKlass));
    }

    public static void add(IRComponentFinder f) {
        entries.addFirst(f);
    }

    public static void reset() {
        entries.clear();
        add(Component.class, RUnknownComponent.class);
        add(Window.class, RWindow.class);
        add(JTable.class, RTable.class);
        add(JTableHeader.class, RTableHeader.class);
        add(AbstractButton.class, RAbstractButton.class);
        add(JToggleButton.class, RToggleButton.class);
        add(JComboBox.class, RComboBox.class);
        add(JTextComponent.class, RTextComponent.class);
        add(JTree.class, RTree.class);
        add(JList.class, RList.class);
        add(JTabbedPane.class, RTabbedPane.class);
        add(JMenuItem.class, RMenuItem.class);
        add(JSlider.class, RSlider.class);
        add(JSpinner.class, RSpinner.class);
        add(DefaultEditor.class, RDefaultEditor.class);
        add(JColorChooser.class, RColorChooser.class);
        add(JSplitPane.class, RSplitPane.class);
        add(BasicSplitPaneDivider.class, RSplitPane.class);
        add(JFileChooser.class, RFileChooser.class);
        add(JEditorPane.class, REditorPane.class);
        add(JLabel.class, RLabel.class);
        add(JScrollBar.class, RIgnoreComponent.class);
    }

    static {
        reset();
    }

    public RComponentFactory(JSONOMapConfig objectMapConfiguration) {
        this.omapConfig = objectMapConfiguration;
    }

    public RComponent findRComponent(Component component, Point point, IJSONRecorder recorder) {
        return findRawRComponent(getComponent(component, point), point, recorder);
    }

    public RComponent findRawRComponent(Component source, Point point, IJSONRecorder recorder) {
        for (IRComponentFinder entry : entries) {
            Class<? extends RComponent> k = entry.get(source);
            if (k == null) {
                continue;
            }
            try {
                Constructor<? extends RComponent> cons = k.getConstructor(Component.class, JSONOMapConfig.class, Point.class,
                        IJSONRecorder.class);
                return cons.newInstance(source, omapConfig, point, recorder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Component getComponent(Component component, Point location) {
        Component parent = component.getParent();
        Component grandparent = parent != null ? parent.getParent() : null;
        Component greatgrandparent = grandparent != null ? grandparent.getParent() : null;

        Component realComponent = component;
        if (getColorChooser(component) != null) {
            realComponent = getColorChooser(component);
        } else if (getFileChooser(component) != null) {
            realComponent = getFileChooser(component);
        } else if (component.getClass().getName().indexOf("ScrollableTabPanel") > 0) {
            // See: testTabbedPaneWhenInScrollTabLayout
            realComponent = grandparent;
        } else if (component instanceof JTableHeader) {
        } else if (component instanceof JProgressBar) {
        } else if (component instanceof JSlider) {
        } else if (parent instanceof JTable) {
            setLocationForTable((JTable) parent, location);
            realComponent = getComponent(parent, location);
        } else if (parent instanceof JComboBox) {
            realComponent = getComponent(parent, location);
        } else if (greatgrandparent instanceof ComboPopup) {
            realComponent = null;
            if (greatgrandparent instanceof BasicComboPopup) {
                realComponent = getComponent(((BasicComboPopup) greatgrandparent).getInvoker(), location);
            }
        } else if (component instanceof ComboPopup) {
            realComponent = null;
            if (component instanceof BasicComboPopup) {
                realComponent = getComponent(((BasicComboPopup) component).getInvoker(), location);
            }
        } else if (parent instanceof JSpinner) {
            realComponent = parent;
        } else if (grandparent instanceof JSpinner) {
            realComponent = grandparent;
        } else if (grandparent instanceof JTree) {
            realComponent = grandparent;
        } else if (parent instanceof JTree) {
            realComponent = parent;
        }
        return realComponent;
    }

    private JColorChooser getColorChooser(Component component) {
        Component parent = component;
        while (parent != null) {
            if (parent instanceof JColorChooser) {
                return (JColorChooser) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    private JFileChooser getFileChooser(Component component) {
        Component parent = component;
        while (parent != null) {
            if (parent instanceof JFileChooser) {
                return (JFileChooser) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * Sets the location to a point the table.
     *
     * @param table
     * @param location
     */
    private void setLocationForTable(JTable table, Point location) {
        if (location != null) {
            Rectangle cellRect = table.getCellRect(table.getEditingRow(), table.getEditingColumn(), false);
            location.setLocation(cellRect.getLocation());
        }
    }

}
