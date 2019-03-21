package net.sourceforge.marathon.javaagent.components.jide;
/*
 * @(#)JideSplitPaneDemo.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideSplitPane;

/**
 * Demoed Component: {@link com.jidesoft.swing.JideSplitPane} <br>
 * Required jar files: jide-common.jar <br>
 * Required L&F: Jide L&F extension required
 */
public class JideSplitPaneDemo extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static JideSplitPane _jideSplitPane;

    public JideSplitPaneDemo() {
        JideSplitPane splitPane = createSplitPane();
        add(splitPane);
    }

    public String getName() {
        return "JideSplitPane Demo";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                JideSplitPaneDemo splitPanedemo = new JideSplitPaneDemo();
                JFrame jFrame = new JFrame();
                jFrame.getContentPane().add(splitPanedemo);
                jFrame.pack();
                jFrame.setVisible(true);

            }
        });

    }

    private static JideSplitPane createSplitPane() {
        JTree tree = new JTree();
        JTable table = new JTable(new DefaultTableModel(10, 3));
        JList list = new JList(new Object[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", }) {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            public Dimension getPreferredScrollableViewportSize() {
                Dimension size = super.getPreferredScrollableViewportSize();
                size.width = 100;
                return size;
            }
        };

        _jideSplitPane = new JideSplitPane(JideSplitPane.HORIZONTAL_SPLIT);
        _jideSplitPane.setProportionalLayout(true);
        _jideSplitPane.add(new JScrollPane(tree), JideBoxLayout.FLEXIBLE);
        _jideSplitPane.add(new JScrollPane(table), JideBoxLayout.VARY);
        _jideSplitPane.add(new JScrollPane(list), JideBoxLayout.FLEXIBLE);
        return _jideSplitPane;
    }
}