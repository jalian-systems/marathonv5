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
package net.sourceforge.marathon.runtime;

import java.util.logging.Logger;

import javafx.scene.Node;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.fx.api.ISubPropertiesLayout;
import net.sourceforge.marathon.runtime.fx.api.ListLayout;

public class ClassPathLayout extends ListLayout implements ISubPropertiesLayout {

    public static final Logger LOGGER = Logger.getLogger(ClassPathLayout.class.getName());

    public ClassPathLayout(ModalDialog<?> parent) {
        super(parent, true);
    }

    @Override
    public String getName() {
        return "Class Path";
    }

    @Override
    public Node getIcon() {
        return FXUIUtils.getIcon("cp_obj");
    }

    @Override
    public boolean isValidInput(boolean showAlert) {
        return true;
    }

    @Override
    public boolean isAddArchivesNeeded() {
        return true;
    }

    @Override
    public boolean isAddFoldersNeeded() {
        return true;
    }

    @Override
    public boolean isAddClassesNeeded() {
        return false;
    }

    @Override
    public boolean isSingleSelection() {
        return false;
    }

    @Override
    public String getPropertyKey() {
        return Constants.PROP_APPLICATION_PATH;
    }
}
