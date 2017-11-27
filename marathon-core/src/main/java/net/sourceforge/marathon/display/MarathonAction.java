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
package net.sourceforge.marathon.display;

import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.ToggleGroup;
import net.sourceforge.marathon.editor.IEditorProvider;
import net.sourceforge.marathon.runtime.api.IScriptModel;

public abstract class MarathonAction implements IMarathonAction {

    public static class SeparatorAction extends MarathonAction {
        public SeparatorAction(String menuName, boolean toolbar, boolean menu) {
            super(menuName, null, "", null, toolbar, menu);
        }
    
        @Override public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int beginCaretPostion,
                int endCaretPosition, int startLine) throws Exception {
        }
    
        @Override public boolean isSeperator() {
            return true;
        }
    
    }

    public static final Logger LOGGER = Logger.getLogger(MarathonAction.class.getName());

    private String name;
    private String description;
    private String mneumonic;
    private final IEditorProvider editorProvider;
    private final boolean toolbar;
    private final boolean menubar;
    private String menuName;
    private String accelKey;
    private char menuMnemonic;
    private String command;

    public MarathonAction(String name, String description, String mneumonic, IEditorProvider editorProvider, boolean toolbar,
            boolean menubar) {
        this(name, description, mneumonic, editorProvider, toolbar, menubar, null);
    }

    public MarathonAction(String name, String description, String mneumonic, IEditorProvider editorProvider, boolean toolbar,
            boolean menubar, String command) {
        this.name = name;
        this.description = description;
        this.mneumonic = mneumonic;
        this.editorProvider = editorProvider;
        this.toolbar = toolbar;
        this.menubar = menubar;
        this.command = command;
    }

    @Override public String getName() {
        return name;
    }

    @Override public String getDescription() {
        return description;
    }

    @Override public String getMneumonic() {
        return mneumonic;
    }

    public IEditorProvider getEditorProvider() {
        return editorProvider;
    }

    @Override public boolean isToolBarAction() {
        return toolbar;
    }

    @Override public boolean isMenuBarAction() {
        return menubar;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    @Override public String getMenuName() {
        return menuName;
    }

    public void setAccelKey(String accelKey) {
        this.accelKey = accelKey;
    }

    @Override public String getAccelKey() {
        return accelKey;
    }

    @Override public boolean isSeperator() {
        return false;
    }

    public void setMenuMnemonic(char mnemonicChar) {
        this.menuMnemonic = mnemonicChar;
    }

    @Override public char getMenuMnemonic() {
        return menuMnemonic;
    }

    @Override public ToggleGroup getButtonGroup() {
        return null;
    }

    @Override public boolean isSelected() {
        return false;
    }

    @Override public boolean isPopupMenu() {
        return false;
    }

    @Override public Menu getPopupMenu() {
        return null;
    }

    @Override public String getCommand() {
        return command;
    }
    
    @Override public Node getIcon() {
        return null;
    }
}
