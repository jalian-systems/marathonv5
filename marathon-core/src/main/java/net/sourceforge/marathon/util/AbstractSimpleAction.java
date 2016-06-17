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
package net.sourceforge.marathon.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;

/**
 * this is a simple action class that allows you to specify stuff we care about
 * in the constructor each instance will have to subclass it to provide an
 * actionPeformed method as well
 */
public abstract class AbstractSimpleAction extends AbstractAction implements PropertyChangeListener {
    private static final long serialVersionUID = 1L;

    private Icon iconEnabled;
    private Icon iconDisabled;

    public AbstractSimpleAction(String name, String description, char mneumonic, Icon icon, Icon iconDisabled) {
        super(name);
        if (mneumonic != 0)
            putValue(Action.MNEMONIC_KEY, Integer.valueOf(mneumonic));
        if (description.equals(""))
            putValue(Action.SHORT_DESCRIPTION, name);
        else
            putValue(Action.SHORT_DESCRIPTION, description);
        iconEnabled = icon;
        addPropertyChangeListener(this);
        this.iconDisabled = iconDisabled;
        putValue(Action.SMALL_ICON, iconEnabled);
    }

    public JMenuItem getMenuItem() {
        JMenuItem item = new JMenuItem(this);
        item.setIcon(iconEnabled);
        item.setDisabledIcon(iconDisabled);
        item.setPressedIcon(iconDisabled);
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
        if (iconEnabled == null) {
            button.setText((String) getValue(NAME));
            return button;
        }
        button.setText(null);
        button.setPressedIcon(iconDisabled);
        button.setDisabledIcon(iconDisabled);
        button.setIcon(iconEnabled);
        return button;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("enabled")) {
            if (((Boolean) evt.getNewValue()).booleanValue())
                putValue(Action.SMALL_ICON, iconEnabled);
            else
                putValue(Action.SMALL_ICON, iconDisabled);
        }
    }

    public Icon getIconEnabled() {
        return iconEnabled;
    }

    public Icon getIconDisabled() {
        return iconDisabled;
    }
}
