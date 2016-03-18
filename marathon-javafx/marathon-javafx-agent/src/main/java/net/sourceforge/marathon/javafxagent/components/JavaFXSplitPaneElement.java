package net.sourceforge.marathon.javafxagent.components;

import org.json.JSONArray;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXSplitPaneElement extends JavaFXElement {

    public JavaFXSplitPaneElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        SplitPane splitPane = (SplitPane) getComponent();
        JSONArray locations = new JSONArray(value);
        double[] dividerLocations = new double[locations.length()];
        for (int i = 0; i < locations.length(); i++) {
            dividerLocations[i] = locations.getDouble(i);
        }
        splitPane.setDividerPositions(dividerLocations);
        return true;
    }

}
