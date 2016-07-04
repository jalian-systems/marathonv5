package net.sourceforge.marathon.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;

public class WaitMessageDialog {
    private static MessageDialog _instance = new MessageDialog();

    private static class MessageDialog extends JFrame {
        private static final long serialVersionUID = 1L;

        private MessageDialog() {
            setUndecorated(true);
            getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
            setAlwaysOnTop(true);
            initComponents();
            setLocationRelativeTo(null);
        }

        private void initComponents() {
            Container contentPane = getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(new JLabel(new ImageIcon(DisplayWindow.class.getResource("wait.gif"), "Wait Message")),
                    BorderLayout.CENTER);
            JLabel label = new JLabel("This window closes once Marathon is ready for recording");
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBackground(Color.BLACK);
            label.setForeground(Color.WHITE);
            Dimension preferredSize = label.getPreferredSize();
            preferredSize.height = 30;
            label.setPreferredSize(preferredSize);
            contentPane.add(label, BorderLayout.SOUTH);
            pack();
        }

    }

    public static void setVisible(boolean b) {
        if (_instance.isVisible() != b)
            _instance.setVisible(b);
    }
}