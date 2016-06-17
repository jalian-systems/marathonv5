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

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JMenu;

import net.sourceforge.marathon.editor.IEditorProvider;

public abstract class MarathonAction implements IMarathonAction {

    private Icon enabledIcon;
    private Icon disabledIcon;
    private String name;
    private String description;
    private char mneumonic;
    private final IEditorProvider editorProvider;
    private final boolean toolbar;
    private final boolean menubar;
    private String menuName;
    private String accelKey;
    private char menuMnemonic;

    public MarathonAction(String name, String description, char mneumonic, Icon enabledIcon, Icon disabledIcon,
            IEditorProvider editorProvider, boolean toolbar, boolean menubar) {
        this.name = name;
        this.description = description;
        this.mneumonic = mneumonic;
        this.enabledIcon = enabledIcon;
        this.disabledIcon = disabledIcon;
        this.editorProvider = editorProvider;
        this.toolbar = toolbar;
        this.menubar = menubar;
    }

    public Icon getDisabledIcon() {
        return disabledIcon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Icon getEnabledIcon() {
        return enabledIcon;
    }

    public char getMneumonic() {
        return mneumonic;
    }

    public IEditorProvider getEditorProvider() {
        return editorProvider;
    }

    public boolean isToolBarAction() {
        return toolbar;
    }

    public boolean isMenuBarAction() {
        return menubar;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setAccelKey(String accelKey) {
        this.accelKey = accelKey;
    }

    public String getAccelKey() {
        return accelKey;
    }

    public boolean isSeperator() {
        return false;
    }

    public void setMenuMnemonic(char mnemonicChar) {
        this.menuMnemonic = mnemonicChar;
    }

    public char getMenuMnemonic() {
        return menuMnemonic;
    }

    public ButtonGroup getButtonGroup() {
        return null;
    }

    public boolean isSelected() {
        return false;
    }

    @Override public boolean isPopupMenu() {
        return false;
    }

    @Override public JMenu getPopupMenu() {
        return null;
    }
}
