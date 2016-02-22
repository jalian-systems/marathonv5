package net.sourceforge.marathon.javafxagent.components;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JSlider;

import net.sourceforge.marathon.javafxagent.AbstractJavaElement;
import net.sourceforge.marathon.javafxagent.IJavaAgent;
import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

public class JProgressBarAndSliderJavaElement extends AbstractJavaElement {

    public JProgressBarAndSliderJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public String _getText() {
        if (component instanceof JProgressBar)
            return "" + ((JProgressBar) component).getValue();
        if (component instanceof JSlider)
            return "" + ((JSlider) component).getValue();
        return null;
    }
}
