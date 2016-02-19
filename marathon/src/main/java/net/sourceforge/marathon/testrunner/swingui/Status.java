package net.sourceforge.marathon.testrunner.swingui;

import java.awt.Font;

import javax.swing.JLabel;

public class Status extends JLabel {
    private static final long serialVersionUID = 1L;

    public Status() {
        super("Ready");
        Font font = getFont();
        int size = font.getSize();
        setFont(font.deriveFont(Font.ITALIC, size - 2));
    }

    public void reset() {
        setText("Ready");
    }
}
