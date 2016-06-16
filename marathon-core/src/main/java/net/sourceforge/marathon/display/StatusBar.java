package net.sourceforge.marathon.display;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.marathon.editor.IStatusBar;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class StatusBar extends JPanel implements IStatusBar {
    private static final long serialVersionUID = 1L;
    private JLabel rowLabel;
    private JLabel columnLabel;
    private JLabel insertLabel;
    private JLabel msgLabel;
    private JLabel fixtureLabel;
    private JLabel extraLabel;

    public JLabel getFixtureLabel() {
        return fixtureLabel;
    }

    public JLabel getRowLabel() {
        return rowLabel;
    }

    public JLabel getColumnLabel() {
        return columnLabel;
    }

    public JLabel getExtraLabel() {
        return extraLabel;
    }

    public StatusBar() {
        FormLayout layout = new FormLayout("fill:pref:grow, pref, pref, pref, pref, pref", "pref");
        setLayout(layout);
        CellConstraints constraints = new CellConstraints();
        msgLabel = createLabel("");
        msgLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(msgLabel, constraints.xy(1, 1));
        extraLabel = createLabel("Firefox");
        extraLabel.setFont(extraLabel.getFont().deriveFont(Font.ITALIC));
        add(extraLabel, constraints.xy(2, 1));
        fixtureLabel = createLabel("default");
        fixtureLabel.setFont(fixtureLabel.getFont().deriveFont(Font.BOLD));
        add(fixtureLabel, constraints.xy(3, 1));
        rowLabel = createLabel("9999");
        add(rowLabel, constraints.xy(4, 1));
        columnLabel = createLabel("9999");
        add(columnLabel, constraints.xy(5, 1));
        insertLabel = createLabel(" Overwrite ");
        add(insertLabel, constraints.xy(6, 1));
        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    }

    private JLabel createLabel(String s) {
        JComboBox<String> box = new JComboBox<String>();
        box.setPrototypeDisplayValue(s);
        box.getPreferredSize();
        JLabel label = new JLabel(" ", SwingConstants.CENTER);
        label.setPreferredSize(box.getPreferredSize());
        label.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.GRAY));
        return label;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.marathon.editor.IStatusBar#setCaretLocation(int,
     * int)
     */
    public void setCaretLocation(int row, int col) {
        if (row == 0 && col == 0) {
            this.rowLabel.setText("");
            this.columnLabel.setText("");
        } else {
            this.rowLabel.setText(Integer.toString(row));
            this.columnLabel.setText(Integer.toString(col));
        }
    }

    public void setApplicationState(String text) {
        msgLabel.setText(text);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.marathon.editor.IStatusBar#setIsOverwriteEnabled(boolean)
     */
    public void setIsOverwriteEnabled(boolean isOverwriteEnabled) {
        if (isOverwriteEnabled) {
            this.insertLabel.setText("Overwrite");
        } else {
            this.insertLabel.setText("Insert");
        }
    }

    public void setFixture(String fixture) {
        fixtureLabel.setText(fixture);
    }

    public Component getInsertLabel() {
        return insertLabel;
    }
}
