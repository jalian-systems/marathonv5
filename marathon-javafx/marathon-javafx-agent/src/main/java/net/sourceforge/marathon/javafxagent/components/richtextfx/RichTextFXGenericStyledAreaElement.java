package net.sourceforge.marathon.javafxagent.components.richtextfx;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.control.Cell;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaAgentKeys;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class RichTextFXGenericStyledAreaElement extends JavaFXElement {

    public static final Logger LOGGER = Logger.getLogger(RichTextFXGenericStyledAreaElement.class.getName());

    public RichTextFXGenericStyledAreaElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean marathon_select(String value) {
        GenericStyledArea gsa = new GenericStyledArea(getComponent());
        Boolean isCellEditor = (Boolean) gsa.getProperties().get("marathon.celleditor");
        gsa.clear();
        if (isCellEditor != null && isCellEditor) {
            super.sendKeys(value, JavaAgentKeys.ENTER);
            Cell cell = (Cell) gsa.getProperties().get("marathon.cell");
            cell.commitEdit(value);
        } else {
            super.sendKeys(convertNewLines(value));
        }
        return true;
    }

    private CharSequence[] convertNewLines(String s) {
        List<CharSequence> sequences = new ArrayList<>();
        char[] scs = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : scs) {
            if (c == '\n' || c == ' ') {
                if (sb.length() != 0) {
                    sequences.add(sb.toString());
                    sb.setLength(0);
                }
                sequences.add(c == '\n' ? JavaAgentKeys.ENTER : JavaAgentKeys.SPACE);
            } else {
                sb.append(c);
            }
        }
        if (sb.length() != 0)
            sequences.add(sb.toString());
        return sequences.toArray(new CharSequence[0]);
    }

    @Override
    public String _getText() {
        return new GenericStyledArea(getComponent()).getText();
    }

    @Override
    public void _clear() {
        verifyCanInteractWithElement();
        GenericStyledArea gsa = new GenericStyledArea(getComponent());
        gsa.clear();
    }

    @Override
    public String getTagName() {
        return GenericStyledArea.getTagName(getComponent());
    }
}
