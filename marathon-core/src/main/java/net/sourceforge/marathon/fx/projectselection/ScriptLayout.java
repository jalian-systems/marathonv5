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
package net.sourceforge.marathon.fx.projectselection;

import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.fx.api.CompositeLayout;
import net.sourceforge.marathon.runtime.fx.api.IPropertiesLayout;

public class ScriptLayout extends CompositeLayout implements IPropertiesLayout {

    public static final Logger LOGGER = Logger.getLogger(ScriptLayout.class.getName());

    public ScriptLayout(ModalDialog<?> parent) {
        super(parent);
    }

    @Override
    public String getName() {
        return "Language";
    }

    @Override
    public Node getIcon() {
        return FXUIUtils.getIcon("script_obj");
    }

    @Override
    protected String getResourceName() {
        return "scriptmodel";
    }

    @Override
    protected String getOptionFieldName() {
        return "Script: ";
    }

    @Override
    protected String getClassProperty() {
        return Constants.PROP_PROJECT_SCRIPT_MODEL;
    }

    @Override
    protected boolean isSelectable() {
        return false;
    }

    @Override
    protected void errorMessage() {
        FXUIUtils.showMessageDialog(parent.getStage(), "Select a Script Language", "Script Language", AlertType.ERROR);
    }
}
