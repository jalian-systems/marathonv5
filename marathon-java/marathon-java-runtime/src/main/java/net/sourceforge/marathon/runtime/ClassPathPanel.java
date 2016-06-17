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
package net.sourceforge.marathon.runtime;

import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;

import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.ISubPropertiesPanel;
import net.sourceforge.marathon.runtime.api.ListPanel;

public class ClassPathPanel extends ListPanel implements ISubPropertiesPanel {
    public static final Icon ICON = new ImageIcon(
            ClassPathPanel.class.getClassLoader().getResource("net/sourceforge/marathon/mpf/images/cp_obj.gif"));;

    public ClassPathPanel(JDialog parent) {
        super(parent, true);
    }

    public Icon getIcon() {
        return ICON;
    }

    public String getName() {
        return "Class Path";
    }

    public String getPropertyKey() {
        return Constants.PROP_APPLICATION_PATH;
    }

    public boolean isAddArchivesNeeded() {
        return true;
    }

    public boolean isValidInput() {
        return true;
    }

    public boolean isAddFoldersNeeded() {
        return true;
    }

    public boolean isAddClassesNeeded() {
        return false;
    }

    public int getMnemonic() {
        return KeyEvent.VK_C;
    }

    @Override public boolean isSingleSelection() {
        return false;
    }
}
