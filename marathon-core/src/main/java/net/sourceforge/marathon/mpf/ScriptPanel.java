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
package net.sourceforge.marathon.mpf;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.sourceforge.marathon.runtime.api.CompositePanel;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IPropertiesPanel;

public class ScriptPanel extends CompositePanel implements IPropertiesPanel {

    public ScriptPanel(JDialog parent) {
        super(parent);
    }

    public static final Icon ICON = new ImageIcon(
            ProjectPanel.class.getClassLoader().getResource("net/sourceforge/marathon/mpf/images/script_obj.gif"));

    @Override protected String getResourceName() {
        return "scriptmodel";
    }

    @Override public String getName() {
        return "Language";
    }

    @Override public Icon getIcon() {
        return ICON;
    }

    @Override protected String getClassProperty() {
        return Constants.PROP_PROJECT_SCRIPT_MODEL;
    }

    @Override protected boolean isSelectable() {
        return false;
    }

    @Override protected String getOptionFieldName() {
        return "S&cript:";
    }

    @Override protected void errorMessage() {
        JOptionPane.showMessageDialog(parent, "Select a Script Language", "Script Language", JOptionPane.ERROR_MESSAGE);
    }
}
