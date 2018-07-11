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
package net.sourceforge.marathon.fxdocking;

import java.util.logging.Logger;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;

public class VLToolBar extends ToolBar {

    public static final Logger LOGGER = Logger.getLogger(VLToolBar.class.getName());

    @SuppressWarnings("unused")
    private boolean collapse;

    public VLToolBar() {
    }

    public void addSeparator() {
        getItems().add(new Separator(Orientation.VERTICAL));
    }

    public void setCollapsible(boolean collapse) {
        this.collapse = collapse;
    }

    public void add(Node node) {
        getItems().add(node);
    }

}
