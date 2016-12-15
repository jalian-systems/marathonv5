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

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import net.sourceforge.marathon.fxdocking.ToolBarContainer.Orientation;

public class ToolBarPanel extends HBox {
    private Orientation orientation;

    public ToolBarPanel(Orientation orientation) {
        this.orientation = orientation;
    }

    private Pane createFiller() {
        Pane empty = new Pane();
        HBox.setHgrow(empty, Priority.ALWAYS);
        return empty;
    }

    public void add(VLToolBar toolbar) {
        ObservableList<Node> children = getChildren();
        if (children.size() > 0 && orientation == Orientation.LEFT) {
            VLToolBar last = (VLToolBar) children.get(children.size() - 1);
            HBox.setHgrow(last, Priority.NEVER);
            HBox.setHgrow(toolbar, Priority.ALWAYS);
        }
        if (children.size() == 0 && orientation == Orientation.RIGHT) {
            // YUK!!!
            toolbar.getItems().add(0, createFiller());
            HBox.setHgrow(toolbar, Priority.ALWAYS);
        }
        children.add(toolbar);
    }
}
