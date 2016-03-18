package net.sourceforge.marathon.javafxagent.components;

import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXProgressBarElement extends JavaFXElement {

    public JavaFXProgressBarElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        ProgressBar progressBar = (ProgressBar) getComponent();
        progressBar.setProgress(Double.parseDouble(value));
        return true;
    }

}
