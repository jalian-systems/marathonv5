package net.sourceforge.marathon.runtime.api;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

public abstract class EscapeDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public EscapeDialog() {
    }

    public EscapeDialog(Dialog parent, String title, boolean modal) {
        super(parent, title, modal);
    }

    public EscapeDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);
    }

    @Override public void setVisible(boolean arg0) {
        enableDefaultActions();
        super.setVisible(arg0);
    }

    private void enableDefaultActions() {
        setCloseButton(getCloseButton());
        setOKButton(getOKButton());
    }

    private void setOKButton(JButton okButton) {
        if (okButton != null)
            getRootPane().setDefaultButton(okButton);
    }

    private void setCloseButton(final JButton button) {
        if (button == null)
            return;
        Action action = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                button.doClick();
            }
        };
        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", action);
    }

    public abstract JButton getOKButton();

    public abstract JButton getCloseButton();

}
