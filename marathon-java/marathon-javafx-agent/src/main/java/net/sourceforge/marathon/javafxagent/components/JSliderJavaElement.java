package net.sourceforge.marathon.javafxagent.components;

import java.awt.Component;

import javax.swing.JSlider;

import net.sourceforge.marathon.javafxagent.AbstractJavaElement;
import net.sourceforge.marathon.javafxagent.IJavaAgent;
import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

public class JSliderJavaElement extends AbstractJavaElement {

    public JSliderJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public String _getText() {
        JSlider slider = (JSlider) component;
        return getCurrentValue(slider);
    }
    
    public static String getCurrentValue(JSlider slider) {
        return Integer.toString(slider.getValue());
    }

    @Override public boolean marathon_select(String value) {
        ((JSlider) component).setValue(Integer.valueOf(Integer.parseInt(value)));
        return true;
    }
}
