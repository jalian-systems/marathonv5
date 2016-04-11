package net.sourceforge.marathon.javafxagent.components;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.IPseudoElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;

public class JavaFXComboBoxOptionElement extends JavaFXElement implements IPseudoElement {

    private JavaFXElement parent;
    private int option;

    public JavaFXComboBoxOptionElement(JavaFXElement parent, int option) {
        super(parent);
        this.parent = parent;
        this.option = option;
    }

    @Override public IJavaFXElement getParent() {
        return parent;
    }

    @Override public String createHandle() {
        JSONObject o = new JSONObject().put("selector", "nth-option").put("parameters",
                new JSONArray().put(new JSONObject().put("select", option + 1).toString()));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override public Node getPseudoComponent() {
        return null;
    }

    @Override public String _getText() {
        return getComboBoxText((ComboBox<?>) getComponent(), option, true);
    }
}
