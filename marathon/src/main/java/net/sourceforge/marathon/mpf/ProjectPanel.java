package net.sourceforge.marathon.mpf;

import java.io.File;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.FileSelectionListener;
import net.sourceforge.marathon.runtime.api.IFileSelectedAction;
import net.sourceforge.marathon.runtime.api.IPropertiesPanel;
import net.sourceforge.marathon.runtime.api.UIUtils;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class ProjectPanel implements IPropertiesPanel, IFileSelectedAction {
    public static final Icon ICON = new ImageIcon(ProjectPanel.class.getClassLoader().getResource(
            "net/sourceforge/marathon/mpf/images/prj_obj.gif"));;
    // Current projectDirectory: For use in other panels
    public static String projectDir = "";
    private JTextField nameField;
    private JTextField dirField;
    private JTextArea descriptionField;
    private MPFConfigurationUI parent;
    private JButton browse;
    private String testDir;
    private String fixtureDir;
    private String moduleDir;
    private String checklistDir;
    private JPanel panel;

    public ProjectPanel(MPFConfigurationUI configurationUI) {
        parent = configurationUI;
    }

    public JPanel createPanel() {
        initComponents();
        PanelBuilder builder = new PanelBuilder(new FormLayout("left:pref, 3dlu, pref:grow, 3dlu, fill:pref",
                "pref, 3dlu, pref, 3dlu, fill:pref:grow, 3dlu, pref, 3dlu, pref"));
        builder.border(Borders.DIALOG);
        CellConstraints labelConstraints = new CellConstraints();
        CellConstraints compConstraints = new CellConstraints();
        builder.addLabel("&Name: ", labelConstraints.xy(1, 1), nameField, compConstraints.xywh(3, 1, 3, 1));
        builder.addLabel("Directory: ", labelConstraints.xy(1, 3), dirField, compConstraints.xy(3, 3));
        builder.add(browse, compConstraints.xy(5, 3));
        JScrollPane scrollPane = new JScrollPane(descriptionField, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JLabel label = builder.addLabel("&Description: ", labelConstraints.xy(1, 5, CellConstraints.LEFT, CellConstraints.TOP),
                scrollPane, compConstraints.xywh(3, 5, 3, 1));
        label.setLabelFor(descriptionField);
        return builder.getPanel();
    }

    private void initComponents() {
        nameField = new JTextField(20);
        dirField = new JTextField(20);
        dirField.setEditable(false);
        dirField.setFocusable(false);
        dirField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void removeUpdate(DocumentEvent e) {
                projectDir = dirField.getText();
            }
            
            @Override public void insertUpdate(DocumentEvent e) {
                projectDir = dirField.getText();
            }
            
            @Override public void changedUpdate(DocumentEvent e) {
                projectDir = dirField.getText();
            }
        });
        browse = UIUtils.createBrowseButton();
        browse.setMnemonic('o');
        FileSelectionListener fileSelectionListener = new FileSelectionListener(this, null, parent, null,
                "Select Project Directory");
        fileSelectionListener.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        browse.addActionListener(fileSelectionListener);
        descriptionField = new JTextArea(4, 30);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
    }

    public String getName() {
        return "Project";
    }

    public Icon getIcon() {
        return ICON;
    }

    public void getProperties(Properties props) {
        props.setProperty(Constants.PROP_PROJECT_NAME, nameField.getText());
        props.setProperty(Constants.PROP_PROJECT_DIR, dirField.getText().replace(File.separatorChar, '/'));
        props.setProperty(Constants.PROP_PROJECT_DESCRIPTION, descriptionField.getText());

        if (testDir != null)
            props.setProperty(Constants.PROP_TEST_DIR, testDir);
        if (fixtureDir != null)
            props.setProperty(Constants.PROP_FIXTURE_DIR, fixtureDir);
        if (moduleDir != null)
            props.setProperty(Constants.PROP_MODULE_DIRS, moduleDir);
        if (checklistDir != null)
            props.setProperty(Constants.PROP_CHECKLIST_DIR, checklistDir);
    }

    public void setProperties(Properties props) {
        // Also store the directory props and give them back
        testDir = props.getProperty(Constants.PROP_TEST_DIR);
        fixtureDir = props.getProperty(Constants.PROP_FIXTURE_DIR);
        moduleDir = props.getProperty(Constants.PROP_MODULE_DIRS);
        checklistDir = props.getProperty(Constants.PROP_CHECKLIST_DIR);
        nameField.setText(props.getProperty(Constants.PROP_PROJECT_NAME, ""));
        nameField.setCaretPosition(0);
        String pdir = props.getProperty(Constants.PROP_PROJECT_DIR, "").replace('/', File.separatorChar);
        projectDir = pdir;
        dirField.setText(pdir);
        dirField.setCaretPosition(0);
        if (!dirField.getText().equals("")) {
            browse.setEnabled(false);
        }
        descriptionField.setText(props.getProperty(Constants.PROP_PROJECT_DESCRIPTION, ""));
        descriptionField.setCaretPosition(0);
    }

    public boolean isValidInput() {
        if (nameField.getText() == null || nameField.getText().equals("")) {
            JOptionPane.showMessageDialog(parent, "Project name can't be empty", "Project Name", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        if (dirField.getText() == null || dirField.getText().equals("")) {
            JOptionPane.showMessageDialog(parent, "Project directory can't be empty", "Project Directory",
                    JOptionPane.ERROR_MESSAGE);
            dirField.requestFocus();
            return false;
        }
        return true;
    }

    public void filesSelected(File[] files, Object cookie) {
        dirField.setText(files[0].getAbsolutePath());
    }

    public JPanel getPanel() {
        if (panel == null)
            panel = createPanel();
        return panel;
    }
}
