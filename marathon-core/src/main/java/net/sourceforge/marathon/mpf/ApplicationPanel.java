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
import net.sourceforge.marathon.runtime.api.IRuntimeLauncherModel;

public class ApplicationPanel extends CompositePanel implements IPropertiesPanel {

    public static final Icon ICON = new ImageIcon(
            ProjectPanel.class.getClassLoader().getResource("net/sourceforge/marathon/mpf/images/app_obj.gif"));

    public static final String NODIALOGBORDER = "no-dialog-border";

    public ApplicationPanel(JDialog parent) {
        super(parent);
    }

    protected String getResourceName() {
        return "launcher-" + Constants.getFramework();
    }

    public ApplicationPanel(JDialog parent, String nodialogborder) {
        super(parent, nodialogborder);
    }

    public String getName() {
        return "Application";
    }

    public Icon getIcon() {
        return ICON;
    }

    protected String getClassProperty() {
        return Constants.PROP_PROJECT_LAUNCHER_MODEL;
    }

    public IRuntimeLauncherModel getSelectedModel() {
        try {
            return (IRuntimeLauncherModel) getLauncherModel(getClassName());
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
        return null;
    }

    @Override protected String getOptionFieldName() {
        return "La&uncher: ";
    }

    @Override protected void errorMessage() {
        JOptionPane.showMessageDialog(parent, "Select an application launcher", "Application Launcher", JOptionPane.ERROR_MESSAGE);
    }
}
