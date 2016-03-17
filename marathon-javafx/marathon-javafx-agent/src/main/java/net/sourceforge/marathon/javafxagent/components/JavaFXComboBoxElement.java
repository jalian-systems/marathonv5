package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXComboBoxElement extends JavaFXElement {

    public JavaFXComboBoxElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("nth-option")) {
            return Arrays.asList(new JavaFXComboBoxOptionElement(this, ((Integer) params[0]).intValue() - 1));
        } else if (selector.equals("all-options") || selector.equals("all-cells")) {
            ComboBox<?> listView = (ComboBox<?>) getComponent();
            ArrayList<IJavaFXElement> r = new ArrayList<>();
            int nItems = listView.getItems().size();
            for (int i = 0; i < nItems; i++) {
                r.add(new JavaFXComboBoxOptionElement(this, i));
            }
            return r;
        }
        return super.getByPseudoElement(selector, params);
    }

    @Override public boolean marathon_select(String value) {
        ComboBox<?> comboBox = (ComboBox<?>) getComponent();
        String text = stripHTMLTags(value);
        int selectedItem = getComboBoxItemIndex(comboBox, text);
        if (selectedItem == -1) {
            if (comboBox.isEditable()) {
                comboBox.getEditor().setText(value);
                return true;
            }
            return false;
        }
        comboBox.getSelectionModel().select(selectedItem);
        return true;
    }

    public String getContent() {
        return new JSONArray(getContent((ComboBox<?>) getComponent())).toString();
    }
}
