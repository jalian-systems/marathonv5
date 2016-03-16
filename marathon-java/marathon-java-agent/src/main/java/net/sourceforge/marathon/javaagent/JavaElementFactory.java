package net.sourceforge.marathon.javaagent;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.util.LinkedList;

import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultTreeCellEditor.EditorContainer;

import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;
import net.sourceforge.marathon.javaagent.components.DefaultEditorJavaElement;
import net.sourceforge.marathon.javaagent.components.JColorChooserJavaElement;
import net.sourceforge.marathon.javaagent.components.JComboBoxJavaElement;
import net.sourceforge.marathon.javaagent.components.JEditorPaneJavaElement;
import net.sourceforge.marathon.javaagent.components.JFileChooserJavaElement;
import net.sourceforge.marathon.javaagent.components.JListJavaElement;
import net.sourceforge.marathon.javaagent.components.JMenuItemJavaElement;
import net.sourceforge.marathon.javaagent.components.JProgressBarAndSliderJavaElement;
import net.sourceforge.marathon.javaagent.components.JSliderJavaElement;
import net.sourceforge.marathon.javaagent.components.JSpinnerJavaElement;
import net.sourceforge.marathon.javaagent.components.JSplitPaneJavaElement;
import net.sourceforge.marathon.javaagent.components.JTabbedPaneJavaElement;
import net.sourceforge.marathon.javaagent.components.JTableHeaderJavaElement;
import net.sourceforge.marathon.javaagent.components.JTableJavaElement;
import net.sourceforge.marathon.javaagent.components.JTextComponentJavaElement;
import net.sourceforge.marathon.javaagent.components.JToggleButtonJavaElement;
import net.sourceforge.marathon.javaagent.components.JTreeEditingContainerJavaElement;
import net.sourceforge.marathon.javaagent.components.JTreeJavaElement;

public class JavaElementFactory {

    public static IJavaElement createElement(Component component, IJavaAgent driver, JWindow window) {
        IJavaElement found = window.findElementFromMap(component);
        if (found != null) {
            return found;
        }
        Class<? extends IJavaElement> klass = get(component);
        if (klass == null)
            return new JavaElement(component, driver, window);
        try {
            Constructor<? extends IJavaElement> constructor = klass.getConstructor(Component.class, IJavaAgent.class,
                    JWindow.class);
            IJavaElement newInstance = constructor.newInstance(component, driver, window);
            return newInstance;
        } catch (Exception e) {
            throw new RuntimeException("createElement failed", e);
        }
    }

    private static class InstanceCheckFinder implements IJavaElementFinder {
        private Class<? extends Component> componentKlass;
        private Class<? extends IJavaElement> javaElementKlass;

        public InstanceCheckFinder(Class<? extends Component> componentKlass, Class<? extends IJavaElement> javaElementKlass) {
            this.componentKlass = componentKlass;
            this.javaElementKlass = javaElementKlass;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * net.sourceforge.marathon.javaagent.IJavaElementFinder#get(java.awt
         * .Component)
         */
        @Override public Class<? extends IJavaElement> get(Component component) {
            if (componentKlass.isInstance(component))
                return javaElementKlass;
            return null;
        }
    }

    private static LinkedList<IJavaElementFinder> entries = new LinkedList<IJavaElementFinder>();

    static {
        reset();
    }

    public static void reset() {
        add(Component.class, JavaElement.class);
        add(JList.class, JListJavaElement.class);
        add(JTabbedPane.class, JTabbedPaneJavaElement.class);
        add(JComboBox.class, JComboBoxJavaElement.class);
        add(JTable.class, JTableJavaElement.class);
        add(JTableHeader.class, JTableHeaderJavaElement.class);
        add(JTree.class, JTreeJavaElement.class);
        add(JToggleButton.class, JToggleButtonJavaElement.class);
        add(JSpinner.class, JSpinnerJavaElement.class);
        add(JProgressBar.class, JProgressBarAndSliderJavaElement.class);
        add(JSlider.class, JProgressBarAndSliderJavaElement.class);
        add(JSplitPane.class, JSplitPaneJavaElement.class);
        add(JTextComponent.class, JTextComponentJavaElement.class);
        add(EditorContainer.class, JTreeEditingContainerJavaElement.class);
        add(JEditorPane.class, JEditorPaneJavaElement.class);
        add(JMenuItem.class, JMenuItemJavaElement.class);
        add(JSlider.class, JSliderJavaElement.class);
        add(JSpinner.class, JSpinnerJavaElement.class);
        add(DefaultEditor.class, DefaultEditorJavaElement.class);
        add(JColorChooser.class, JColorChooserJavaElement.class);
        add(JFileChooser.class, JFileChooserJavaElement.class);
    }

    public static Class<? extends IJavaElement> get(Component component) {
        for (IJavaElementFinder entry : entries) {
            Class<? extends IJavaElement> k = entry.get(component);
            if (k != null)
                return k;
        }
        return null;
    }

    public static void add(Class<? extends Component> component, Class<? extends IJavaElement> javaelement) {
        add(new InstanceCheckFinder(component, javaelement));
    }

    public static void add(IJavaElementFinder e) {
        entries.addFirst(e);
    }
}
