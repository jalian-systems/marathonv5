/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.javadriver.dnd;

/*
 * DropDemo.java requires the following file:
 *     ListTransferHandler.java
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import net.sourceforge.marathon.javaagent.DnDHandler;
import net.sourceforge.marathon.javaagent.DragAndDropException;

@SuppressWarnings({ "unchecked", "rawtypes" }) public class DropDemo extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JComboBox dropCombo;
    private JList list;
    private JTextArea textArea;
    protected int dndAction;

    public DropDemo() {
        super(new GridLayout(3, 1));
        add(createArea());
        add(createList());
        Box radioBox = new Box(BoxLayout.Y_AXIS);
        ButtonGroup group = new ButtonGroup();
        JRadioButton r1 = new JRadioButton(new AbstractAction("Use COPY") {
            private static final long serialVersionUID = 1L;

            @Override public void actionPerformed(ActionEvent e) {
                dndAction = DnDConstants.ACTION_COPY;
            }
        });
        radioBox.add(r1);
        group.add(r1);
        JRadioButton r2 = new JRadioButton(new AbstractAction("Use MOVE") {
            private static final long serialVersionUID = 1L;

            @Override public void actionPerformed(ActionEvent e) {
                dndAction = DnDConstants.ACTION_MOVE;
            }
        });
        radioBox.add(r2);
        group.add(r2);
        r2.setSelected(true);
        JButton dndList2List = new JButton(new AbstractAction("DnD-List to List") {
            private static final long serialVersionUID = 1L;

            @Override public void actionPerformed(ActionEvent e) {
                Rectangle cellBounds = list.getCellBounds(3, 3);
                DnDHandler dnd = new DnDHandler(list, list, cellBounds.x + cellBounds.width / 2,
                        cellBounds.y + cellBounds.height / 2, dndAction);
                try {
                    dnd.performDrop();
                } catch (DragAndDropException e1) {
                    e1.printStackTrace();
                }
            }
        });
        JButton dndList2Text = new JButton(new AbstractAction("DnD-List to TextArea") {
            private static final long serialVersionUID = 1L;

            @Override public void actionPerformed(ActionEvent e) {
                Rectangle cellBounds = list.getCellBounds(3, 3);
                DnDHandler dnd = new DnDHandler(list, textArea, cellBounds.x + cellBounds.width / 2,
                        cellBounds.y + cellBounds.height / 2, dndAction);
                try {
                    dnd.performDrop();
                } catch (DragAndDropException e1) {
                    e1.printStackTrace();
                }
            }
        });
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(radioBox, BorderLayout.NORTH);
        panel.add(dndList2List, BorderLayout.CENTER);
        panel.add(dndList2Text, BorderLayout.SOUTH);
        add(panel);
    }

    private JPanel createList() {
        DefaultListModel listModel = new DefaultListModel();

        for (int i = 0; i < 10; i++) {
            listModel.addElement("List Item " + i);
        }

        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(400, 100));

        list.setDragEnabled(true);
        list.setTransferHandler(new ListTransferHandler());

        dropCombo = new JComboBox(new String[] { "USE_SELECTION", "ON", "INSERT", "ON_OR_INSERT" });
        dropCombo.addActionListener(this);
        JPanel dropPanel = new JPanel();
        dropPanel.add(new JLabel("List Drop Mode:"));
        dropPanel.add(dropCombo);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(dropPanel, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createTitledBorder("List"));
        return panel;
    }

    private JPanel createArea() {
        String text = "Drag from or drop into this area.\nThe default action is MOVE;\nhold the Control key to COPY.";

        textArea = new JTextArea();
        textArea.setText(text);
        textArea.setDragEnabled(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 100));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createTitledBorder("Text Area"));
        return panel;
    }

    public void actionPerformed(ActionEvent ae) {
        Object val = dropCombo.getSelectedItem();
        if (val == "USE_SELECTION") {
            list.setDropMode(DropMode.USE_SELECTION);
        } else if (val == "ON") {
            list.setDropMode(DropMode.ON);
        } else if (val == "INSERT") {
            list.setDropMode(DropMode.INSERT);
        } else if (val == "ON_OR_INSERT") {
            list.setDropMode(DropMode.ON_OR_INSERT);
        }
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    public static JFrame createAndShowGUI() {
        // Create and set up the window.
        JFrame frame = new JFrame("DropDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        JComponent newContentPane = new DropDemo();
        newContentPane.setOpaque(true); // content panes must be opaque
        frame.setContentPane(newContentPane);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
        return frame;
    }

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGUI();
            }
        });
    }
}
