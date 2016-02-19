package net.sourceforge.marathon.javaagent.components;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;

import javax.swing.JSplitPane;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JSplitPaneJavaElement extends AbstractJavaElement {

    public JSplitPaneJavaElement(Component component, JavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        JSplitPane pane = (JSplitPane) component;
        if (selector.equals("left") || selector.equals("top")) {
            return Arrays.asList(JavaElementFactory.createElement(pane.getTopComponent(), getDriver(), getWindow()));
        } else if (selector.equals("right") || selector.equals("bottom")) {
            return Arrays.asList(JavaElementFactory.createElement(pane.getBottomComponent(), getDriver(), getWindow()));
        }
        return super.getByPseudoElement(selector, params);
    }

    @Override public boolean marathon_select(String value) {
        ((JSplitPane) component).setDividerLocation(Integer.parseInt(value));
        return true;
    }
}
