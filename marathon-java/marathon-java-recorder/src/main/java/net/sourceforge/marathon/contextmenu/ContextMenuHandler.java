package net.sourceforge.marathon.contextmenu;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.component.RComponentFactory;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;

public class ContextMenuHandler {

    private ContextMenuWindow contextMenu;
    private IJSONRecorder recorder;
    private RComponentFactory finder;

    public ContextMenuHandler(IJSONRecorder recorder, RComponentFactory finder) {
        this.recorder = recorder;
        this.finder = finder;
    }

    public void showPopup(MouseEvent mouseEvent) {
        if (isContextMenuOn())
            return;
        Component component = SwingUtilities.getDeepestComponentAt(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
        if (component == null)
            return;
        if (component instanceof JMenuItem && (!(component instanceof JMenu) || ((JMenu) component).isSelected()))
            return;
        Point point = SwingUtilities.convertPoint(mouseEvent.getComponent(), mouseEvent.getPoint(), component);
        showPopup(component, point);
    }

    public void showPopup(KeyEvent keyEvent) {
        if (isContextMenuOn())
            return;
        Component component = keyEvent.getComponent();
        if (component instanceof JMenuItem && (!(component instanceof JMenu) || ((JMenu) component).isSelected()))
            return;
        Point point = new Point(component.getX() + component.getWidth() / 2, component.getY() + component.getHeight() / 2);
        showPopup(component, point);
    }

    public boolean isContextMenuOn() {
        return contextMenu != null && contextMenu.isShowing();
    }

    public ContextMenuWindow showPopup(Component component, Point point) {
        Component root = SwingUtilities.getRoot(component);
        if (root instanceof Window)
            setContextMenu(new ContextMenuWindow((Window) root, recorder, finder));
        else
            throw new RuntimeException("Unknown root for component");
        contextMenu.setComponent(component, point, true);
        if (component instanceof JMenu)
            contextMenu.show(((JMenu) component).getParent(), point.x, point.y);
        else
            contextMenu.show(component, point.x, point.y);
        return contextMenu;
    }

    private void setContextMenu(ContextMenuWindow contextMenuWindow) {
        contextMenu = contextMenuWindow;
    }

}
