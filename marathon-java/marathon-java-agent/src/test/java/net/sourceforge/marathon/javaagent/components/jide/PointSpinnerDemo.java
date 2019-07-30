package net.sourceforge.marathon.javaagent.components.jide;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.spinner.PointSpinner;

public class PointSpinnerDemo extends JPanel {

    private static final long serialVersionUID = 1L;

    public PointSpinnerDemo() {
        setLayout(new GridLayout(0, 1, 10, 10));

        final PointSpinner pointSpinner = new PointSpinner();
        add(pointSpinner);

    }

    static public void main(String[] s) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                JFrame jFrame = new JFrame();
                PointSpinnerDemo pointSpinner = new PointSpinnerDemo();
                jFrame.getContentPane().add(pointSpinner);
                jFrame.pack();
                jFrame.setVisible(true);
            }
        });
    }
}
