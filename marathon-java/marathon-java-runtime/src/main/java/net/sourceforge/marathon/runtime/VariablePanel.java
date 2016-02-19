package net.sourceforge.marathon.runtime;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.marathon.runtime.api.ButtonBarFactory;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.EscapeDialog;
import net.sourceforge.marathon.runtime.api.IPropertiesPanel;
import net.sourceforge.marathon.runtime.api.ISubPropertiesPanel;
import net.sourceforge.marathon.runtime.api.UIUtils;

import com.jgoodies.forms.builder.ButtonStackBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class VariablePanel implements IPropertiesPanel, ISubPropertiesPanel {
    public static final Icon ICON = new ImageIcon(VariablePanel.class.getClassLoader().getResource(
            "net/sourceforge/marathon/mpf/images/prop_obj.gif"));

    private static class PropertyDialog extends EscapeDialog {
        private static final long serialVersionUID = 1L;
        private JTextField propertyField = new JTextField(30);
        private JTextField valueField = new JTextField(30);
        private String key = null;
        private String value = null;
        private JButton okButton = null;
        private JButton cancelButton;

        public PropertyDialog(JDialog parent) {
            super(parent, "Create Property", true);
            setLocationRelativeTo(parent);
            FormLayout layout = new FormLayout("3dlu, left:pref:grow, 3dlu, pref:grow, 3dlu",
                    "3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu");
            PanelBuilder builder = new PanelBuilder(layout);
            CellConstraints constraints = new CellConstraints();
            builder.addLabel("Property name:", constraints.xy(2, 2));
            builder.add(propertyField, constraints.xy(4, 2));
            propertyField.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    okButton.setEnabled(propertyField.getText().length() > 0);
                }

                public void insertUpdate(DocumentEvent e) {
                    okButton.setEnabled(propertyField.getText().length() > 0);
                }

                public void removeUpdate(DocumentEvent e) {
                    okButton.setEnabled(propertyField.getText().length() > 0);
                }
            });
            builder.addLabel("Value:", constraints.xy(2, 4));
            builder.add(valueField, constraints.xy(4, 4));
            okButton = UIUtils.createOKButton();
            okButton.setEnabled(false);
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    key = propertyField.getText();
                    value = valueField.getText();
                    dispose();
                }
            });
            cancelButton = UIUtils.createCancelButton();
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            JPanel buttonPanel = ButtonBarFactory.buildOKCancelBar(okButton, cancelButton);
            builder.add(buttonPanel, constraints.xyw(2, 6, 3));
            getContentPane().add(builder.getPanel());
            pack();
        }

        public PropertyDialog(JDialog parent, Property property) {
            this(parent);
            propertyField.setText(property.getProperty());
            valueField.setText(property.getValue());
        }

        public Property getProperty() {
            setVisible(true);
            if (key == null)
                return null;
            return new Property(key, value);
        }

        @Override public JButton getOKButton() {
            return okButton;
        }

        @Override public JButton getCloseButton() {
            return cancelButton;
        }
    }

    private static class Property {
        private String property;
        private String value;

        public Property(String property, String value) {
            this.property = property;
            this.value = value;
        }

        public String getProperty() {
            return property;
        }

        public String getValue() {
            return value;
        }

    }

    private static class PropertyTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private String[] columnNames = { "Property", "Value" };
        private List<Property> dataList;

        public PropertyTableModel() {
            dataList = new ArrayList<Property>();
        }

        public void removeRow(int selectedRow) {
            dataList.remove(selectedRow);
            fireTableDataChanged();
        }

        public int getColumnCount() {
            return 2;
        }

        public String getColumnName(int column) {
            return columnNames[column];
        }

        public int getRowCount() {
            return dataList.size();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Property prop = (Property) dataList.get(rowIndex);
            switch (columnIndex) {
            case 0:
                return prop.getProperty();
            case 1:
                return prop.getValue();
            default:
                return "";
            }
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        public void addRow(Property property) {
            dataList.add(property);
            fireTableDataChanged();
        }

        public void getProperties(Properties props) {
            int size = dataList.size();
            for (int i = 0; i < size; i++) {
                Property property = (Property) dataList.get(i);
                props.setProperty(Constants.PROP_PROPPREFIX + property.getProperty(), property.getValue());
            }
        }

        public Property getPropertyAt(int index) {
            return (Property) dataList.get(index);
        }

        public void setProperties(Properties props) {
            Enumeration<Object> enumeration = props.keys();
            while (enumeration.hasMoreElements()) {
                String property = (String) enumeration.nextElement();
                if (property.startsWith(Constants.PROP_PROPPREFIX)) {
                    addRow(new Property(property.substring(Constants.PROP_PROPPREFIX.length()), props.getProperty(property)));
                }
            }
        }

        public void updateRow(int index, Property property) {
            dataList.set(index, property);
            fireTableDataChanged();
        }
    }

    PropertyTableModel model = new PropertyTableModel();
    private JButton removeButton = null;
    private JButton addButton = null;
    private JButton editButton = null;
    private JTable table = null;
    private JDialog parent = null;
    private JPanel panel;

    public VariablePanel(JDialog parent) {
        this.parent = parent;
    }

    void initComponents() {
        table = new JTable(model);
        addButton = UIUtils.createAddButton();
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PropertyDialog dialog = new PropertyDialog(parent);
                Property property = dialog.getProperty();
                if (property != null) {
                    model.addRow(property);
                }
            }
        });
        editButton = UIUtils.createEditButton();
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = table.getSelectedRow();
                Property property = model.getPropertyAt(index);
                PropertyDialog dialog = new PropertyDialog(parent, property);
                property = dialog.getProperty();
                if (property != null) {
                    model.updateRow(index, property);
                    table.getSelectionModel().setSelectionInterval(index, index);
                }
            }
        });
        editButton.setMnemonic(KeyEvent.VK_D);
        editButton.setEnabled(false);
        removeButton = UIUtils.createRemoveButton();
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1)
                    model.removeRow(selectedRow);
            }
        });
        removeButton.setEnabled(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = table.getSelectedRow();
                removeButton.setEnabled(selectedRow != -1);
                editButton.setEnabled(selectedRow != -1);
            }
        });
        table.setPreferredScrollableViewportSize(new Dimension(200, 200));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int index = table.getSelectedRow();
                if (index == -1 || e.getClickCount() < 2)
                    return;
                editButton.doClick();
            }
        });
    }

    public JPanel createPanel() {
        initComponents();
        JScrollPane scrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        PanelBuilder builder = new PanelBuilder(new FormLayout("fill:pref:grow, 3dlu, center:pref:none", "fill:p:grow"));
        builder.border(Borders.DIALOG);
        CellConstraints constraints = new CellConstraints();
        builder.add(scrollPane, constraints.xy(1, 1));
        builder.add(getButtonStackPanel(), constraints.xy(3, 1));
        return builder.getPanel();
    }

    private JPanel getButtonStackPanel() {
        ButtonStackBuilder buttonStack = new ButtonStackBuilder();
        buttonStack.addButton(addButton, editButton, removeButton);
        return buttonStack.getPanel();
    }

    public String getName() {
        return "Properties";
    }

    public Icon getIcon() {
        return ICON;
    }

    public void getProperties(Properties props) {
        model.getProperties(props);
    }

    public void setProperties(Properties props) {
        model.setProperties(props);
    }

    public boolean isValidInput() {
        return true;
    }

    public JPanel getPanel() {
        if (panel == null)
            panel = createPanel();
        return panel;
    }

    @Override public int getMnemonic() {
        return KeyEvent.VK_V;
    }
}
