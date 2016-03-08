package net.sourceforge.marathon.javaagent.components;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JColorChooser;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

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
