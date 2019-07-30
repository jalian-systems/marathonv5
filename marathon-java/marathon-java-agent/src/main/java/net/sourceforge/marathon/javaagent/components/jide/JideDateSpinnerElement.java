package net.sourceforge.marathon.javaagent.components.jide;

import java.awt.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jidesoft.spinner.DateSpinner;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JideDateSpinnerElement extends AbstractJavaElement {

    public JideDateSpinnerElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override
    public boolean marathon_select(String value) {
        if (!value.equals("")) {
            DateSpinner dateSpinner = (DateSpinner) getComponent();
            SimpleDateFormat format = dateSpinner._timeEditor.getFormat();
            try {
                Date parseValue = format.parse(value);
                dateSpinner.setValue(parseValue);
                return true;
            } catch (ParseException e) {
                throw new RuntimeException("Invalid value for '" + value + "' for date-picker '");
            }
        }
        return super.marathon_select(value);
    }

    @Override
    public String _getText() {
        DateSpinner dateSpinner = (DateSpinner) getComponent();
        return dateSpinner._timeEditor.getTextField().getText();
    }

    @Override
    public String getTagName() {
        return "date-spinner";
    }
}
