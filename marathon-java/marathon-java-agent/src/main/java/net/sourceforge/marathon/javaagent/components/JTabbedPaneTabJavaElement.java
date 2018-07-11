/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.javaagent.components;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.IPseudoElement;
import net.sourceforge.marathon.javaagent.InvalidElementStateException;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.javaagent.NoSuchElementException;
import net.sourceforge.marathon.javaagent.UnsupportedCommandException;

public class JTabbedPaneTabJavaElement extends AbstractJavaElement implements IPseudoElement {

    public static final Logger LOGGER = Logger.getLogger(JTabbedPaneTabJavaElement.class.getName());

    private int tabIndex;
    private JTabbedPaneJavaElement parent;

    public JTabbedPaneTabJavaElement(JTabbedPaneJavaElement parent, int tabIndex) {
        super(parent);
        this.parent = parent;
        this.tabIndex = tabIndex;
    }

    @Override
    public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("component")) {
            return Arrays.asList(JavaElementFactory.createElement(getTabComponent(tabIndex), getDriver(), getWindow()));
        }
        if (selector.equals("tab-component")) {
            Component tc = getTabTabComponent(tabIndex);
            if (tc != null)
                return Arrays.asList(JavaElementFactory.createElement(tc, getDriver(), getWindow()));
            else
                return Arrays.asList();
        }
        throw new UnsupportedCommandException("JTabbedPane does not support pseudoelement " + selector, null);
    }

    private Component getTabComponent(final int tabIndex) {
        validateTab();
        return EventQueueWait.exec(new Callable<Component>() {
            @Override
            public Component call() throws Exception {
                return ((JTabbedPane) getComponent()).getComponentAt(tabIndex);
            }
        });
    }

    private Component getTabTabComponent(final int tabIndex) {
        validateTab();
        return EventQueueWait.exec(new Callable<Component>() {
            @Override
            public Component call() throws Exception {
                return ((JTabbedPane) getComponent()).getTabComponentAt(tabIndex);
            }
        });
    }

    @Override
    public String createHandle() {
        JSONObject o = new JSONObject().put("selector", "nth-tab").put("parameters", new JSONArray().put(tabIndex + 1));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override
    public String _getText() {
        return getText((JTabbedPane) parent.getComponent(), tabIndex);
    }

    public static String getText(JTabbedPane tabbedPane, int index) {
        String original = getItemText(tabbedPane, index);
        String itemText = original;
        int suffixIndex = 0;

        for (int i = 0; i < index; i++) {
            String current = getItemText(tabbedPane, i);

            if (current.equals(original)) {
                itemText = String.format("%s(%d)", original, ++suffixIndex);
            }
        }
        return itemText;
    }

    protected static String getItemText(JTabbedPane tabbedPane, int index) {
        String titleAt = tabbedPane.getTitleAt(index);
        if (titleAt == null || "".equals(titleAt)) {
            return getTabNameFromIcon(tabbedPane, index);
        }
        return titleAt;
    }

    private static String getTabNameFromIcon(JTabbedPane tp, int index) {
        Icon iconAt = tp.getIconAt(index);
        if (iconAt == null || !(iconAt instanceof ImageIcon) || ((ImageIcon) iconAt).getDescription() == null) {
            return "tabIndex-" + index;
        }
        String description = ((ImageIcon) iconAt).getDescription();
        return nameFromImageDescription(description);
    }

    private static String nameFromImageDescription(String description) {
        try {
            String name = new URL(description).getPath();
            if (name.lastIndexOf('/') != -1) {
                name = name.substring(name.lastIndexOf('/') + 1);
            }
            if (name.lastIndexOf('.') != -1) {
                name = name.substring(0, name.lastIndexOf('.'));
            }
            return name;
        } catch (MalformedURLException e) {
            return description;
        }
    }

    @Override
    public IJavaElement getParent() {
        return parent;
    }

    @Override
    public void _moveto() {
        Rectangle bounds = getTabBounds();
        getDriver().getDevices().moveto(parent.getComponent(), bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    private Rectangle getTabBounds() {
        return ((JTabbedPane) parent.getComponent()).getBoundsAt(tabIndex);
    }

    private boolean makeTabVisible(JTabbedPane tp, int selectedTab) {
        validateTab();
        if (!(tp.getUI() instanceof BasicTabbedPaneUI)) {
            try {
                EventQueueWait.call(tp, "setSelectedIndex", selectedTab);
            } catch (NoSuchMethodException e) {
                throw new InvalidElementStateException(
                        "Unable to call setSelectedIndex on JTabbedPane. selectedTab = " + selectedTab, e);
            }
            return true;
        }
        boolean isVisible = false;
        int n = tp.getTabCount();
        int loopCount = n;
        Action backward = tp.getActionMap().get("scrollTabsBackwardAction");
        Action forward = tp.getActionMap().get("scrollTabsForwardAction");
        while (!isVisible && loopCount-- > 0) {
            int firstVisibleTab = -1, lastVisibleTab = -1;
            for (int i = 0; i < n; i++) {
                Rectangle tabBounds = tp.getBoundsAt(i);
                int tabForCoordinate = tp.getUI().tabForCoordinate(tp, tabBounds.x + tabBounds.width / 2,
                        tabBounds.y + tabBounds.height / 2);
                if (tabForCoordinate != -1) {
                    if (firstVisibleTab == -1) {
                        firstVisibleTab = tabForCoordinate;
                    }
                    lastVisibleTab = tabForCoordinate;
                }
            }
            isVisible = firstVisibleTab <= selectedTab && selectedTab <= lastVisibleTab;
            if (isVisible) {
                continue;
            }
            if (selectedTab < firstVisibleTab) {
                backward.actionPerformed(new ActionEvent(tp, ActionEvent.ACTION_PERFORMED, ""));
            } else {
                forward.actionPerformed(new ActionEvent(tp, ActionEvent.ACTION_PERFORMED, ""));
            }
        }
        return isVisible;
    }

    @Override
    public boolean _isDisplayed() {
        Rectangle bounds = ((JTabbedPane) parent.getComponent()).getBoundsAt(tabIndex);
        return bounds != null
                && SwingUtilities.isRectangleContainingRectangle(((JTabbedPane) parent.getComponent()).getVisibleRect(), bounds);
    }

    @Override
    public boolean _isEnabled() {
        return ((JTabbedPane) parent.getComponent()).isEnabledAt(tabIndex);
    }

    @Override
    public Component getPseudoComponent() {
        throw new RuntimeException("No physical pseudo component available for JTabbedPane's tab items");
    }

    private void validateTab() {
        EventQueueWait.exec(new Runnable() {
            @Override
            public void run() {
                JTabbedPane pane = (JTabbedPane) parent.getComponent();
                int tabCount = pane.getTabCount();
                if (tabIndex < 0 || tabIndex >= tabCount) {
                    throw new NoSuchElementException("Invalid tab index for JTabbedPane: " + tabIndex, null);
                }
            }
        });
    }

    @Override
    public Object _makeVisible() {
        makeTabVisible((JTabbedPane) parent.getComponent(), tabIndex);
        return null;
    }

    @Override
    public Point _getMidpoint() {
        // makeTabVisible((JTabbedPane) parent.getComponent(), tabIndex);
        if (!isDisplayed()) {
            throw new InvalidElementStateException("The tabitem " + (tabIndex + 1) + " is not visible", null);
        }
        Rectangle bounds = getTabBounds();
        return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }
}
