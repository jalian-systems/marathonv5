package net.sourceforge.marathon.javafxagent.components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JColorChooser;

import net.sourceforge.marathon.javafxagent.AbstractJavaElement;
import net.sourceforge.marathon.javafxagent.IJavaAgent;
import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

public class JColorChooserJavaElement extends AbstractJavaElement {

    public JColorChooserJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        if (!value.equals("")) {
            try {
                ((JColorChooser) component).setColor(Color.decode(value));
                return true;
            } catch (Throwable t) {
                throw new NumberFormatException("Invalid value for '" + value + "' for color-chooser '");
            }
        }
        return false;
    }

}
