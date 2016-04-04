package net.sourceforge.marathon.javafxagent.components;

import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXToggleButtonElement extends JavaFXElement {

    public JavaFXToggleButtonElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        boolean selected = Boolean.parseBoolean(value);
        boolean current = ((ToggleButton) node).isSelected();
        if (selected != current)
            click();
        return true;
    }

    @Override public String _getText() {
        return getToggleText((ToggleButton) getComponent());
    }

}
