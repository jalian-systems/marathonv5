package net.sourceforge.marathon.javafxagent.components;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import net.sourceforge.marathon.javafxagent.IJavaAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

public class JavaFXTextInputControlElement extends JavaFXElement {

	public JavaFXTextInputControlElement(Node component, IJavaAgent driver, JWindow window) {
		super(component, driver, window);
	}

	@Override
	public boolean marathon_select(String value) {
		((TextField)getComponent()).setText("");
		super.sendKeys(value);
		return true;
	}
}
