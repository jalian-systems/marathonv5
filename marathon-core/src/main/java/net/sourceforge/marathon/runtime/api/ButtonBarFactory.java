package net.sourceforge.marathon.runtime.api;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.ButtonBarBuilder;

public class ButtonBarFactory {

    public static JPanel buildOKCancelBar(JButton okButton, JButton cancelButton) {
        return buildRightAlignedBar(okButton, cancelButton);
    }

    public static JPanel buildOKCancelApplyBar(JButton okButton, JButton cancelButton, JButton testButton) {
        return buildRightAlignedBar(okButton, cancelButton, testButton);
    }

    public static JPanel buildRightAlignedBar(JButton... buttons) {
        return ButtonBarBuilder.create().addGlue().addButton(buttons).getPanel();
    }

    public static JPanel buildOKBar(JButton okButton) {
        return buildRightAlignedBar(okButton);
    }

    public static Component buildOKCancelHelpBar(JButton ok, JButton cancel, JButton loadDefaults) {
        return buildRightAlignedBar(ok, cancel, loadDefaults);
    }

}
