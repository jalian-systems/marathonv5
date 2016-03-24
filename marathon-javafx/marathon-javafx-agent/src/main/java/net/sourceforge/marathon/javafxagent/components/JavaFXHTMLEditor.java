package net.sourceforge.marathon.javafxagent.components;

import javafx.scene.Node;
import javafx.scene.web.HTMLEditor;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXHTMLEditor extends JavaFXElement {

    public JavaFXHTMLEditor(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        HTMLEditor htmlEditor = (HTMLEditor) getComponent();
        htmlEditor.setHtmlText(value);
        return true;
    }

    @Override public String getTagName() {
        return "html-editor";
    }
}
