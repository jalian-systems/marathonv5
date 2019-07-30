package net.sourceforge.marathon.javaagent.components.jide;
/*
 * @(#)CheckBoxListDemo.java 4/21/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Position;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.CheckBoxList;
import com.jidesoft.swing.SearchableUtils;

/**
 * Demoed Component: {@link com.jidesoft.swing.CheckBoxList} <br>
 * Required jar files: jide-common.jar <br>
 * Required L&F: any L&F
 */
public class CheckBoxListDemo extends JPanel {
    private static final long serialVersionUID = -5982509597978327419L;
    private DefaultListModel model;

    public CheckBoxListDemo() {
        createCheckBoxList();
    }

    private void createCheckBoxList() {
        model = new DefaultListModel();
        String[] name = { "India", "China", "USA", "UAE", "UK", "Australia", "Afghanistan", "Albania", "Antarctica" };
        for (String s : name) {
            model.addElement(s);
        }
        final CheckBoxList list = new CheckBoxList(model) {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public int getNextMatch(String prefix, int startIndex, Position.Bias bias) {
                return -1;
            }

            @Override
            public boolean isCheckBoxEnabled(int index) {
                return !model.getElementAt(index).equals("Afghanistan") && !model.getElementAt(index).equals("Albania")
                        && !model.getElementAt(index).equals("Antarctica");
            }
        };
        list.getCheckBoxListSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        SearchableUtils.installSearchable(list);
        list.getCheckBoxListSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {

                }
            }
        });
        list.setCheckBoxListSelectedIndices(new int[] { 2, 3 });
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Mouse Clicked Event : " + e);
            }
        });
        add(list);
    }

    static public void main(String[] s) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                JFrame frame = new JFrame("CheckBoxList Demo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new CheckBoxListDemo());
                frame.pack();
                frame.setVisible(true);
            }
        });

    }

}