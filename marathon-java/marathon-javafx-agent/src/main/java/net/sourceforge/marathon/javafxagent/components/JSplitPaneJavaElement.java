package net.sourceforge.marathon.javafxagent.components;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;

import javax.swing.JSplitPane;

import net.sourceforge.marathon.javafxagent.AbstractJavaElement;
import net.sourceforge.marathon.javafxagent.IJavaAgent;
import net.sourceforge.marathon.javafxagent.IJavaElement;
import net.sourceforge.marathon.javafxagent.JavaElementFactory;
import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

public class JSplitPaneJavaElement extends AbstractJavaElement {

    public JSplitPaneJavaElement(Component component, IJavaAgent driver, JWindow window) {
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
