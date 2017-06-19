package net.sourceforge.marathon.javaagent;

import java.awt.Component;

import javax.swing.JProgressBar;

import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JProgressBarJavaElement extends AbstractJavaElement {


    public JProgressBarJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public String _getText() {
        return getText((JProgressBar) component);
    }

    public static String getText(JProgressBar progressBar) {
        return Integer.toString(progressBar.getValue());
    }

}
