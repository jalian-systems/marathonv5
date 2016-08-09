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
import javax.swing.SwingUtilities;

public class WaitMessageDialog {
    private static final String DEFAULT_MESSAGE = "This window closes once Marathon is ready for recording";
    private static MessageDialog _instance = new MessageDialog();

    private static class MessageDialog extends JFrame {
        private static final long serialVersionUID = 1L;
        private String message = DEFAULT_MESSAGE;
        private JLabel messageLabel;

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
            messageLabel = new JLabel(message);
            messageLabel.setOpaque(true);
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            messageLabel.setBackground(Color.BLACK);
            messageLabel.setForeground(Color.WHITE);
            Dimension preferredSize = messageLabel.getPreferredSize();
            preferredSize.height = 30;
            messageLabel.setPreferredSize(preferredSize);
            contentPane.add(messageLabel, BorderLayout.SOUTH);
            pack();
        }

        public void setMessage(String message) {
            if(message.equals(this.message))
                return;
            this.message = message ;
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    messageLabel.setText(MessageDialog.this.message);
                }
            });
        }

    }

    public static void setVisible(boolean b, String message) {
        if (_instance.isVisible() != b)
            _instance.setVisible(b);
        _instance.setMessage(message);
    }

    public static void setVisible(boolean b) {
        setVisible(b, DEFAULT_MESSAGE);
    }
}