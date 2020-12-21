package net.sourceforge.marathon.javaagent.components.jide;

import java.awt.Component;

import com.jidesoft.swing.JideSplitPane;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;
import net.sourceforge.marathon.json.JSONArray;

public class JideSplitPaneElement extends AbstractJavaElement {

    public JideSplitPaneElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override
    public boolean marathon_select(String value) {
        JideSplitPane splitPane = (JideSplitPane) getComponent();
        JSONArray locations = new JSONArray(value);
        int[] dividerLocations = new int[locations.length()];
        for (int i = 0; i < locations.length(); i++) {
            dividerLocations[i] = locations.getInt(i);
        }
        splitPane.setDividerLocations(dividerLocations);
        return true;
    }

    @Override
    public String getTagName() {
        return "jide-split-pane";
    }

    @Override
    public String _getText() {
        JideSplitPane splitpane = (JideSplitPane) getComponent();
        return new JSONArray(splitpane.getDividerLocations()).toString();
    }
}
