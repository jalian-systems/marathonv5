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
package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.sourceforge.marathon.javaagent.components.JMenuItemJavaElement;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RMenuItem extends RComponent {

    public static final Logger LOGGER = Logger.getLogger(RMenuItem.class.getName());

    private Component mainMenu;

    public RMenuItem(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override
    protected void mouseButton1Pressed(MouseEvent me) {
        if (isAMenuWithoutChildren()) {
            recorder.recordSelectMenu(this, ((JMenu) component).getText());
            return;
        }
        String selectedMenuPath = buildMenuElementArray((JMenuItem) component);
        if (mainMenu != null) {
            RComponentFactory finder = new RComponentFactory(omapConfig);
            RComponent rComponent = finder.findRComponent(mainMenu, null, recorder);
            if (isAMenuItem()) {
                recorder.recordSelectMenu(rComponent, selectedMenuPath);
                return;
            }
        }
    }

    private boolean isAMenuWithoutChildren() {
        return component instanceof JMenu && getMenuRowCount() == 0;
    }

    private boolean isAMenuItem() {
        return !(component instanceof JMenu);
    }

    private String buildMenuElementArray(JMenuItem leaf) {
        Vector<JMenuItem> elements = new Vector<JMenuItem>();

        elements.insertElementAt(leaf, 0);
        Component current = leaf.getParent();

        while (current != null) {
            if (current instanceof JPopupMenu) {
                JPopupMenu pop = (JPopupMenu) current;
                current = pop.getInvoker();
            } else if (current instanceof JMenu) {
                JMenu menu = (JMenu) current;
                elements.insertElementAt(menu, 0);
                current = menu.getParent();
            } else if (current instanceof JMenuBar) {
                break;
            } else {
                current = current.getParent();
            }
        }
        mainMenu = elements.get(0);
        JMenuItem parent = null;
        StringBuilder sb = new StringBuilder();
        RComponentFactory finder = new RComponentFactory(omapConfig);
        for (JMenuItem jMenuItem : elements) {
            RComponent rComponent = finder.findRComponent(jMenuItem, null, recorder);
            recorder.recordMenuItem(rComponent);
            String text = JMenuItemJavaElement.getText(JMenuItemJavaElement.getItemText(jMenuItem), jMenuItem,
                    parent == null ? new Component[0] : ((JMenu) parent).getMenuComponents());
            parent = jMenuItem;
            sb.append(text).append(">>");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    public int getMenuRowCount() {
        return getRowCount(getComponent(), 0);
    }

    private int getRowCount(Component component, int count) {
        Component[] items = ((JMenu) component).getMenuComponents();
        if (items == null) {
            return 0;
        }
        for (int i = 0; i < items.length; i++) {
            if (items[i] instanceof JMenu) {
                count = getRowCount(items[i], count);
            }
            if (!(items[i] instanceof JSeparator)) {
                count++;
            }
        }
        return count;
    }

}
