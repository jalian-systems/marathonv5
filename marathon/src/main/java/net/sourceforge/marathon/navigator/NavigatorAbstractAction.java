package net.sourceforge.marathon.navigator;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.Icon;

import net.sourceforge.marathon.display.OldSimpleAction;

public abstract class NavigatorAbstractAction extends OldSimpleAction {
    private static final long serialVersionUID = -2726821864269261800L;
    private Navigator navigator;

    abstract public void actionPerformed(ActionEvent e, File[] file);

    abstract public boolean getEnabledState(File[] files);

    public void actionPerformed(ActionEvent e) {
        File[] files = navigator.getSelectedFiles();
        if (getEnabledState(files))
            actionPerformed(e, files);
    }

    public NavigatorAbstractAction(Navigator navigator, String name) {
        this(navigator, name, null);
    }

    public NavigatorAbstractAction(Navigator navigator, String name, Icon icon) {
        this(navigator, name, icon, null);
    }

    public NavigatorAbstractAction(Navigator navigator, String name, Icon icon_enabled, Icon icon_disabled) {
        super(name, (char) 0, icon_enabled, icon_disabled);
        putValue(Action.SMALL_ICON, icon_enabled);
        this.navigator = navigator;
    }
}
