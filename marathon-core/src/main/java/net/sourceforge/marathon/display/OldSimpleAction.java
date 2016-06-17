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
package net.sourceforge.marathon.display;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;

/**
 * this is a simple action class that allows you to specify stuff we care about
 * in the constructor each instance will have to subclass it to provide an
 * actionPeformed method as well
 */
public abstract class OldSimpleAction extends AbstractAction implements PropertyChangeListener {
    private static final long serialVersionUID = 4342290998523999163L;
    private Icon icon_enabled;
    private Icon icon_disabled;

    public OldSimpleAction(String name, char mneumonic) {
        super(name);
        if (mneumonic != 0)
            putValue(Action.MNEMONIC_KEY, Integer.valueOf(mneumonic));
        putValue(Action.SHORT_DESCRIPTION, name);
    }

    public OldSimpleAction(String name, char mneumonic, Icon icon) {
        this(name, mneumonic);
        icon_enabled = icon;
    }

    public OldSimpleAction(String name, char mneumonic, Icon icon, Icon iconDisabled) {
        this(name, mneumonic, icon);
        addPropertyChangeListener(this);
        icon_disabled = iconDisabled;
    }

    public JMenuItem getMenuItem() {
        JMenuItem item = new JMenuItem(this);
        item.setIcon(icon_enabled);
        item.setDisabledIcon(icon_disabled);
        item.setPressedIcon(icon_disabled);
        return item;
    }

    public JButton getButton() {
        JButton button = new JButton(this) {
            private static final long serialVersionUID = 1L;

            public boolean isFocusTraversable() {
                return false;
            }
        };
        button.setName((String) getValue(NAME));
        if (icon_enabled == null) {
            button.setText((String) getValue(NAME));
            return button;
        }
        button.setText(null);
        button.setPressedIcon(icon_disabled);
        button.setDisabledIcon(icon_disabled);
        button.setIcon(icon_enabled);
        return button;
    }

    public JToggleButton getToggleButton() {
        JToggleButton button = new JToggleButton(this) {
            private static final long serialVersionUID = 1L;

            public boolean isFocusTraversable() {
                return false;
            }
        };
        button.setName((String) getValue(NAME));
        if (icon_enabled == null) {
            button.setText((String) getValue(NAME));
            return button;
        }
        button.setText(null);
        button.setPressedIcon(icon_disabled);
        button.setDisabledIcon(icon_disabled);
        button.setIcon(icon_enabled);
        return button;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("enabled")) {
            if (((Boolean) evt.getNewValue()).booleanValue())
                putValue(Action.SMALL_ICON, icon_enabled);
            else
                putValue(Action.SMALL_ICON, icon_disabled);
        }
    }

    public Icon getIconEnabled() {
        return icon_enabled;
    }

    public Icon getIconDisabled() {
        return icon_disabled;
    }
}
