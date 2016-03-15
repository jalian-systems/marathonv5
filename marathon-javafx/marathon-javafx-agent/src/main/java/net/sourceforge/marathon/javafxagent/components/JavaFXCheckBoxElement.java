package net.sourceforge.marathon.javafxagent.components;

import java.util.Arrays;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaAgentException;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXCheckBoxElement extends JavaFXElement {

	public static final String[] states = new String[] { "unchecked", "indeterminate", "checked" };

	public JavaFXCheckBoxElement(Node component, IJavaFXAgent driver, JFXWindow window) {
		super(component, driver, window);
	}

	@Override
	public boolean marathon_select(String value) {
	    if(!isValidState(value))
	        throw new JavaAgentException(value + " is not a valid state for CheckBox.", null);
		int selection = 0;
		for (String state : states) {
			if (state.equalsIgnoreCase(value))
				break;
			selection++;
		}
		CheckBox cb = (CheckBox) node;
		int current = getSelection(cb);
		if (cb.isAllowIndeterminate()) {
			if (current != selection) {
				int nclicks = selection - current;
				if (nclicks < 0)
					nclicks += 3;
				for (int i = 0; i < nclicks; i++)
					click();
			}
		} else {
			if(current != selection)
				click();
		}
		return true;
	}

    private boolean isValidState(String value) {
        return Arrays.asList(states).contains(value);
    }

}
