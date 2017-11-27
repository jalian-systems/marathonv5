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

import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.ToggleGroup;
import net.sourceforge.marathon.runtime.api.IScriptModel;

public interface IMarathonAction {

    String getName();

    String getDescription();

    String getMneumonic();

    void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int beginCaretPostion, int endCaretPosition,
            int startLine) throws Exception;

    boolean isToolBarAction();

    boolean isMenuBarAction();

    String getMenuName();

    String getAccelKey();

    boolean isSeperator();

    char getMenuMnemonic();

    ToggleGroup getButtonGroup();

    boolean isSelected();

    boolean isPopupMenu();

    Menu getPopupMenu();

    String getCommand();

    Node getIcon();
}
