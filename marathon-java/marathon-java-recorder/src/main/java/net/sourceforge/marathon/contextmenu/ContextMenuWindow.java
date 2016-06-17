/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.contextmenu;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.component.RComponentFactory;
import net.sourceforge.marathon.component.RComponent;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.util.UIUtils;

public class ContextMenuWindow extends JWindow implements IRecordingArtifact, AWTEventListener {

    private static final long serialVersionUID = 1L;
    private TransparentFrame overlayFrame;
    private RComponentFactory finder;
    private final Window parentWindow;
    private Component parentComponent;
    protected int startX;
    protected int startY;
    private JLabel titleLabel;
    private ArrayList<IContextMenu> contextMenus;
    private boolean ignoreMouseEvents;
    private IJSONRecorder recorder;

    public ContextMenuWindow(Window window, IJSONRecorder recorder, RComponentFactory finder) {
        super(window);
        this.parentWindow = window;
        this.recorder = recorder;
        this.finder = finder;
        contextMenus = new ArrayList<IContextMenu>();
        if (recorder.isCreatingObjectMap()) {
        } else {
            contextMenus.add(new DefaultContextMenu(this, recorder, finder));
        }
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        setWindowMove(toolBar);
        Action close = new AbstractAction("Close") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                ContextMenuWindow.this.setVisible(false);
            }
        };
        JButton closeButton = UIUtils.createActionButton(close);
        closeButton.setText("X");
        toolBar.add(closeButton);
        titleLabel = new JLabel("   Name Of Component");
        setWindowMove(titleLabel);
        toolBar.add(titleLabel);
        toolBar.setFloatable(false);
        Container contentPane = getContentPane();
        contentPane.add(toolBar, BorderLayout.NORTH);
        JTabbedPane tabbedPane = new JTabbedPane();
        Iterator<IContextMenu> iterator = contextMenus.iterator();
        while (iterator.hasNext()) {
            IContextMenu menu = (IContextMenu) iterator.next();
            tabbedPane.addTab(menu.getName(), menu.getContent());
        }
        contentPane.add(tabbedPane, BorderLayout.CENTER);
        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", close);
        setSize(640, 480);
    }

    private void setWindowMove(Component c) {
        c.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
            }
        });
        c.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                ContextMenuWindow.this.setLocation(ContextMenuWindow.this.getX() + e.getX() - startX,
                        ContextMenuWindow.this.getY() + e.getY() - startY);
            }

        });
    }

    public void setComponent(Component component, Point point, boolean isTriggered) {
        Iterator<IContextMenu> iterator = contextMenus.iterator();
        while (iterator.hasNext()) {
            IContextMenu menu = (IContextMenu) iterator.next();
            menu.setComponent(component, point, isTriggered);
        }
        RComponent RComponent = finder.findRComponent(component, point, recorder);
        if (RComponent == null) {
            return;
        }
        if (isTriggered) {
            overlayFrame = new TransparentFrame(RComponent);
            overlayFrame.setVisible(true);
        }
        String info = RComponent.getCellInfo();
        titleLabel.setText("   " + RComponent.getRComponentName() + (info == null ? "" : " (" + info + ")"));
        pack();
    }

    public void show(Component parent, int x, int y) {
        if (parentComponent == null)
            parentComponent = parent;
        Point p = new Point(x, y);
        SwingUtilities.convertPointToScreen(p, parent);
        setLocation(p);
        setVisible(true);
    }

    public void setVisible(boolean b) {
        if (b) {
            Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK);
        } else {
            disposeOverlay();
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
            if (parentWindow != null)
                parentWindow.requestFocus();
            if (parentComponent != null)
                parentComponent.requestFocusInWindow();
        }
        super.setVisible(b);
    }

    private void disposeOverlay() {
        if (overlayFrame != null) {
            overlayFrame.dispose();
            overlayFrame = null;
        }
    }

    public void eventDispatched(AWTEvent event) {
        if (ignoreMouseEvents)
            return;
        Component root = SwingUtilities.getRoot((Component) event.getSource());
        if (root instanceof IRecordingArtifact || root.getName().startsWith("###")) {
            return;
        }
        if (!(event instanceof MouseEvent))
            return;
        MouseEvent mouseEvent = (MouseEvent) event;
        mouseEvent.consume();
        if (event.getID() == MouseEvent.MOUSE_PRESSED) {
            disposeOverlay();
            Component mouseComponent = SwingUtilities.getDeepestComponentAt(mouseEvent.getComponent(), mouseEvent.getX(),
                    mouseEvent.getY());
            if (mouseComponent == null)
                return;
            mouseEvent = SwingUtilities.convertMouseEvent(mouseEvent.getComponent(), mouseEvent, mouseComponent);
            setComponent(mouseComponent, mouseEvent.getPoint(), true);
            return;
        }
    }

    public void setIgnoreMouseEvents(boolean ignoreMouseEvents) {
        this.ignoreMouseEvents = ignoreMouseEvents;
    }

}
