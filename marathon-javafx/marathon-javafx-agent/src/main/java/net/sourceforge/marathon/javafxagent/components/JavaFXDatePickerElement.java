package net.sourceforge.marathon.javafxagent.components;

import java.time.DateTimeException;
import java.time.LocalDate;

import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXDatePickerElement extends JavaFXElement {

    public JavaFXDatePickerElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        DatePicker datePicker = (DatePicker) getComponent();
        if (!value.equals("")) {
            try {
                LocalDate date = datePicker.getConverter().fromString(value);
                datePicker.setValue(date);
            } catch (Throwable t) {
                throw new DateTimeException("Invalid value for '" + value + "' for date-picker '");
            }
            return true;
        }
        return false;
    }

    @Override public String _getText() {
        return getDatePickerText((DatePicker) getComponent(), ((DatePicker) getComponent()).getValue());
    }
}
