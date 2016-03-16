package net.sourceforge.marathon.javafxagent.components;

import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXColorPickerElement extends JavaFXElement {

    public JavaFXColorPickerElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        ColorPicker colorPicker = (ColorPicker) getComponent();
        if (!value.equals("")) {
            try {
                colorPicker.setValue(Color.valueOf(value));
                return true;
            } catch (Throwable t) {
                throw new IllegalArgumentException("Invalid value for '" + value + "' for color-picker '");
            }
        }
        return false;
    }
}
