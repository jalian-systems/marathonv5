package net.sourceforge.marathon.javaagent.components;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JSlider;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JProgressBarAndSliderJavaElement extends AbstractJavaElement {

    public JProgressBarAndSliderJavaElement(Component component, JavaAgent driver, JWindow window) {
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
