package net.sourceforge.marathon.util;

import javax.swing.Action;
import javax.swing.JButton;

public class UIUtils {

    public static JButton createActionButton(Action action) {
        return new JButton(action);
    }

    public static JButton createInsertAssertionButton() {
        return new JButton("Insert Assertion");
    }

    public static JButton createInsertWaitButton() {
        return new JButton("Insert Wait");
    }

}
