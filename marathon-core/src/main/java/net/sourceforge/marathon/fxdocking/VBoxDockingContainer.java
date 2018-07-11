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

import java.util.List;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.sourceforge.marathon.fxdocking.DockingConstants.Split;

public class VBoxDockingContainer extends VBox implements IDockingContainer {

    public static final Logger LOGGER = Logger.getLogger(VBoxDockingContainer.class.getName());

    private DockingDesktop desktop;

    public VBoxDockingContainer(DockingDesktop desktop) {
        this.desktop = desktop;
        setMaxHeight(Double.MAX_VALUE);
        setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);
    }

    public void add(Dockable dockable) {
        getChildren().add(new TabDockingContainer(desktop, this, dockable));
    }

    @Override
    public void remove(Dockable dockable) {
        remove(dockable.getComponent());
    }

    @Override
    public void split(Dockable base, Dockable dockable, Split position, double proportion) {
        replaceBaseWith(base, () -> {
            return new SplitDockingContainer(desktop, this, base, dockable, position, proportion);
        });
    }

    @Override
    public void tab(Dockable base, Dockable dockable, int order, boolean select) {
        replaceBaseWith(base, () -> {
            return new TabDockingContainer(desktop, this, base, dockable, order, select);
        });
    }

    @Override
    public void remove(Node container) {
        container.getProperties().remove(DockingDesktop.DOCKING_CONTAINER);
        getChildren().remove(container);
    }

    private void replaceBaseWith(Dockable base, INewDockingContainer dc) {
        ObservableList<Node> children = getChildren();
        int baseIndex = children.indexOf(base.getComponent());
        children.remove(base.getComponent());
        children.add(baseIndex, dc.get());
    }

    @Override
    public void replace(Node base, INewDockingContainer indc) {
        ObservableList<Node> children = getChildren();
        int baseIndex = children.indexOf(base);
        children.remove(base);
        children.add(baseIndex, indc.get());
    }

    @Override
    public void getDockables(List<DockableState> dockables) {
        ObservableList<Node> children = getChildren();
        for (Node node : children) {
            ((IDockingContainer) node).getDockables(dockables);
        }
    }

    @Override
    public void debugPrint(String indent) {
        System.out.println(indent + "vbox(" + getProperties().get(DockingDesktop.DOCKING_CONTAINER) + ")");
        ObservableList<Node> children = getChildren();
        for (Node node : children) {
            ((IDockingContainer) node).debugPrint(indent + "  ");
        }
        System.out.println(indent + "vbox=============");
    }
}
