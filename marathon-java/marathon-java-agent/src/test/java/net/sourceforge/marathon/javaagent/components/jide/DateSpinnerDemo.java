package net.sourceforge.marathon.javaagent.components.jide;
/*
 * @(#)${NAME}
 *
 * Copyright 2002 - 2004 JIDE Software Inc. All rights reserved.
 */

import java.awt.GridLayout;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.spinner.DateSpinner;

/**
 * Demoed Component: {@link com.jidesoft.spinner.DateSpinner} <br>
 * Required jar files: jide-common.jar, jide-grids.jar <br>
 * Required L&F: any L&F
 */
public class DateSpinnerDemo extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public DateSpinnerDemo() {
        setLayout(new GridLayout(0, 1, 10, 10));
        DateSpinner date = new DateSpinner("MM/dd/yyyy");
        add(date);
        DateSpinner time = new DateSpinner("hh:mm:ssa", Calendar.getInstance().getTime());
        add(time);
    }

    static public void main(String[] s) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                JFrame jFrame = new JFrame();
                DateSpinnerDemo dateSpinner = new DateSpinnerDemo();
                jFrame.getContentPane().add(dateSpinner);
                jFrame.pack();
                jFrame.setVisible(true);
            }
        });
    }
}
