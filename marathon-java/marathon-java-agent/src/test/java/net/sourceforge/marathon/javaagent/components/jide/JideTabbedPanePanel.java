package net.sourceforge.marathon.javaagent.components.jide;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.swing.JideTabbedPane;

public class JideTabbedPanePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public JideTabbedPanePanel() {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(UIDefaultsLookup.getColor("control"));
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        add(createTabbedPane(), BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                JideTabbedPanePanel jideTabbedPaneDemo = new JideTabbedPanePanel();
                JFrame frame = new JFrame("JideTabbedPane Demo");
                frame.getContentPane().add(jideTabbedPaneDemo);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private static JideTabbedPane createTabbedPane() {
        final JideTabbedPane tabbedPane = new JideTabbedPane(JideTabbedPane.TOP);
        tabbedPane.setOpaque(true);

        final String[] titles = new String[] { "Mail", "Calendar", "Contacts", "Tasks", "Notes", "Folder List", "Shortcuts",
                "Journal" };

        for (int i = 0; i < titles.length; i++) {
            JTextArea jtextArea = new JTextArea();
            jtextArea.setText("This is " + titles[i] + " tab");
            JScrollPane scrollPane = new JScrollPane(jtextArea);
            scrollPane.setPreferredSize(new Dimension(530, 530));
            tabbedPane.addTab(titles[i], scrollPane);
        }
        tabbedPane.setEnabledAt(2, false);
        return tabbedPane;
    }

}
