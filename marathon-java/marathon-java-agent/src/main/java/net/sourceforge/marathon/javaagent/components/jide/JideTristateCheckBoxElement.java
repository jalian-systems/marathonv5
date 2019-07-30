package net.sourceforge.marathon.javaagent.components.jide;

import java.awt.Component;
import java.util.Arrays;

import com.jidesoft.swing.TristateCheckBox;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.JavaAgentException;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JideTristateCheckBoxElement extends AbstractJavaElement {

    public static final String[] states = new String[] { "unchecked", "checked", "indeterminate" };

    public JideTristateCheckBoxElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override
    public boolean marathon_select(String value) {

        TristateCheckBox tscb = (TristateCheckBox) component;

        if (!isValidState(value)) {
            throw new JavaAgentException(value + " is not a valid state for CheckBox.", null);
        }
        int selection = 0;
        for (String state : states) {
            if (state.equalsIgnoreCase(value)) {
                break;
            }
            selection++;
        }
        int current = tscb.getState();
        if (current != selection) {
            int nclicks = selection - current;
            if (nclicks < 0) {
                nclicks += 3;
            }
            for (int i = 0; i < nclicks; i++) {
                click();
            }
        }

        return true;

    }

    @Override
    public String getTagName() {
        return "tristate-check-box";
    }

    private boolean isValidState(String value) {
        return Arrays.asList(states).contains(value);
    }

    @Override
    public String _getText() {
        return ((TristateCheckBox) getComponent()).getName();
    }

}
