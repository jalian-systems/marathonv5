package net.sourceforge.marathon.jxbrowser;

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

public class JavaFXBrowserViewItem extends JavaFXElement implements IPseudoElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXBrowserViewItem.class.getName());

    private JavaFXBrowserViewElement parent;
    private String selector;
    private long frameId;

    public JavaFXBrowserViewItem(JavaFXBrowserViewElement parent, String selector, long frameId) {
        super(parent);
        this.parent = parent;
        this.selector = selector;
        this.frameId = frameId;
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
                new JSONArray().put(new JSONObject().put("select", frameId + ":" + selector).toString()));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override
    public boolean marathon_select(String value) {
        return parent.select(selector, value, frameId);
    }

    @Override
    public Node getPseudoComponent() {
        return null;
    }

    @Override
    public void click() {
        parent.click(selector, frameId);
    }

    @Override
    public void click(int button, Node target, PickResult pickResult, int clickCount, double xoffset, double yoffset) {
        parent.click(selector, frameId);
    }

    @Override
    public String _getText() {
        return JavaFXBrowserViewElement.getText(getComponent(), selector, frameId);
    }

    @Override
    protected String _getLabeledBy() {
        return JavaFXBrowserViewElement.getLabeledBy(getComponent(), selector, frameId);
    }

    @Override
    public String _getValue() {
        return JavaFXBrowserViewElement.getValue(getComponent(), selector, frameId);
    }

    public Map<String, String> getAttributes() {
        return JavaFXBrowserViewElement.getAttributes(getComponent(), selector, frameId);
    }

}
