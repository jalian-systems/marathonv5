package net.sourceforge.marathon.display;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JMenu;

import net.sourceforge.marathon.runtime.api.IScriptModel;

public interface IMarathonAction {

    Icon getDisabledIcon();

    String getName();

    String getDescription();

    Icon getEnabledIcon();

    char getMneumonic();

    void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int beginCaretPostion, int endCaretPosition,
            int startLine) throws Exception;

    boolean isToolBarAction();

    boolean isMenuBarAction();

    String getMenuName();

    String getAccelKey();

    boolean isSeperator();

    char getMenuMnemonic();

    ButtonGroup getButtonGroup();

    boolean isSelected();

    boolean isPopupMenu();

    JMenu getPopupMenu();

}
