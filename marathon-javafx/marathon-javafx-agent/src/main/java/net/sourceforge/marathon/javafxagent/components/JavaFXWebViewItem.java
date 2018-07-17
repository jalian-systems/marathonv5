package net.sourceforge.marathon.javafxagent.components;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.Node;
import javafx.scene.input.PickResult;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.IPseudoElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;

public class JavaFXWebViewItem extends JavaFXElement implements IPseudoElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXWebViewItem.class.getName());

    private JavaFXWebViewElement parent;
    private String selector;

    public JavaFXWebViewItem(JavaFXWebViewElement parent, String selector) {
        super(parent);
        this.parent = parent;
        this.selector = selector;
    }

    @Override
    public IJavaFXElement getParent() {
        return parent;
    }

    @Override
    public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("editor"))
            return Arrays.asList(this);
        return super.getByPseudoElement(selector, params);
    }

    @Override
    public String createHandle() {
        JSONObject o = new JSONObject().put("selector", "select-by-properties").put("parameters",
                new JSONArray().put(new JSONObject().put("select", selector).toString()));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override
    public boolean marathon_select(String value) {
        return parent.select(selector, value);
    }

    @Override
    public Node getPseudoComponent() {
        return null;
    }

    @Override
    public void click() {
        parent.click(selector);
    }

    @Override
    public void click(int button, Node target, PickResult pickResult, int clickCount, double xoffset, double yoffset) {
        parent.click(selector);
    }

    @Override
    public String _getText() {
        return parent.getText(selector);
    }

    @Override
    protected String _getLabeledBy() {
        return parent.getLabeledBy(selector);
    }

    @Override
    public String _getValue() {
        return parent.getValue(selector);
    }

    public Map<String, String> getAttributes() {
        return parent.getAttributes(selector);
    }

}
