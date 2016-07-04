package net.sourceforge.marathon.runtime;

import java.util.Properties;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import net.sourceforge.marathon.runtime.api.ISubPropertiesPanel;

public class WebAppLauncherPanel implements ISubPropertiesPanel {

    public static final String AUT_WEBAPP_URL_PREFIX = "marathon.webapp.url.prefix";
    public static final String AUT_WEBAPP_URL_PATH = "marathon.webapp.url.path";
    private final JDialog parent;

    private JPanel panel;
    private JTextField urlPrefixField;
    private JTextField urlPathField;

    public WebAppLauncherPanel(JDialog parent) {
        this.parent = parent;
    }

    public JPanel getPanel() {
        if (panel == null)
            panel = createPanel();
        return panel;
    }

    private JPanel createPanel() {
        initComponents();
        PanelBuilder builder = new PanelBuilder(new FormLayout("left:pref, 3dlu, fill:pref:grow",
                "pref, 3dlu, pref, 3dlu, fill:pref:grow, 3dlu, fill:pref:grow, 3dlu, pref, 3dlu, pref"));
        builder.border(Borders.DIALOG);
        CellConstraints labelConstraints = new CellConstraints();
        CellConstraints compConstraints = new CellConstraints();
        builder.addLabel("&URL (Prefix): ", labelConstraints.xy(1, 1), urlPrefixField, compConstraints.xywh(3, 1, 1, 1));
        builder.addLabel("&Start Path: ", labelConstraints.xy(1, 3), urlPathField, compConstraints.xywh(3, 3, 1, 1));
        return builder.getPanel();
    }

    private void initComponents() {
        urlPrefixField = new JTextField();
        urlPathField = new JTextField();
    }

    public String getName() {
        return "Web Application Launcher";
    }

    public Icon getIcon() {
        return null;
    }

    public void getProperties(Properties props) {
        props.setProperty(AUT_WEBAPP_URL_PREFIX, urlPrefixField.getText());
        props.setProperty(AUT_WEBAPP_URL_PATH, urlPathField.getText());
    }

    public void setProperties(Properties props) {
        urlPrefixField.setText(props.getProperty(AUT_WEBAPP_URL_PREFIX));
        urlPathField.setText(props.getProperty(AUT_WEBAPP_URL_PATH));
    }

    public boolean isValidInput() {
        if (urlPrefixField.getText() == null || urlPrefixField.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(parent, "URL can't be empty", "Launcher", JOptionPane.ERROR_MESSAGE);
            urlPrefixField.requestFocus();
            return false;
        }
        return true;
    }

    @Override public int getMnemonic() {
        return 0;
    }

}
