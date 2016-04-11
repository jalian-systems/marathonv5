package net.sourceforge.marathon.javafxagent.components;

import org.json.JSONArray;

import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXChoiceBoxElement extends JavaFXElement {

    public JavaFXChoiceBoxElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        ChoiceBox<?> choiceBox = (ChoiceBox<?>) getComponent();
        String text = stripHTMLTags(value);
        int selectedItem = getChoiceBoxItemIndex(choiceBox, text);
        if (selectedItem == -1)
            return false;
        choiceBox.getSelectionModel().select(selectedItem);
        return true;
    }

    public String getContent() {
        return new JSONArray(getContent((ChoiceBox<?>) getComponent())).toString();
    }

    @Override public String _getText() {
        return getChoiceBoxText((ChoiceBox<?>) getComponent(),
                ((ChoiceBox<?>) getComponent()).getSelectionModel().getSelectedIndex());
    }
}
