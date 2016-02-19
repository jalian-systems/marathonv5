package net.sourceforge.marathon.contextmenu;

import java.awt.Component;
import java.awt.Point;

public interface IContextMenu {

    String getName();

    Component getContent();

    void setComponent(Component component, Point point, boolean isTriggered);

}
