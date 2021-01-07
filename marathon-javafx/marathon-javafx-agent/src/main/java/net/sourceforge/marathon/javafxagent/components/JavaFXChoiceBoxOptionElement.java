package net.sourceforge.marathon.javafxagent.components;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.PickResult;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.IPseudoElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.json.JSONObject;

public class JavaFXChoiceBoxOptionElement extends JavaFXElement implements IPseudoElement {

    private JavaFXElement parent;
    private int option;

    public JavaFXChoiceBoxOptionElement(JavaFXElement parent, int option) {
        super(parent);
        this.parent = parent;
        this.option = option;
    }

    @Override
    public IJavaFXElement getParent() {
        return parent;
    }

    @Override
    public String createHandle() {
        JSONObject o = new JSONObject().put("selector", "nth-option").put("parameters", new JSONArray().put(option + 1));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override
    public Node getPseudoComponent() {
        return null;
    }

    @Override
    public String _getText() {
        return getChoiceBoxText((ChoiceBox<?>) getComponent(), option);
    }

    @Override
    public void click(int button, Node target, PickResult pickResult, int clickCount, double xoffset, double yoffset) {
        Platform.runLater(() -> ((ChoiceBox<?>) getComponent()).getSelectionModel().select(option));
    }
}