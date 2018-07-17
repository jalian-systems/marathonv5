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
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.sourceforge.marathon.fxdocking.DockingConstants.Split;

public class SplitDockingContainer extends SplitPane implements IDockingContainer {

    public static final Logger LOGGER = Logger.getLogger(SplitDockingContainer.class.getName());

    public SplitDockingContainer(DockingDesktop desktop, IDockingContainer parent, Dockable base, Dockable dockable, Split position,
            double proportion) {
        setMaxHeight(Double.MAX_VALUE);
        setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);
        getProperties().put(DockingDesktop.DOCKING_CONTAINER, parent);
        setOrientation(position.getOrientation());
        ObservableList<Node> items = getItems();
        if (position == Split.LEFT || position == Split.TOP) {
            items.add(new TabDockingContainer(desktop, this, dockable));
            items.add(new TabDockingContainer(desktop, this, base));
        } else {
            items.add(new TabDockingContainer(desktop, this, base));
            items.add(new TabDockingContainer(desktop, this, dockable));
        }
        setDividerPositions(proportion);
    }

    public SplitDockingContainer(DockingDesktop desktop, IDockingContainer parent, Node base, Dockable dockable, Split position,
            double proportion) {
        setMaxHeight(Double.MAX_VALUE);
        setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);
        getProperties().put(DockingDesktop.DOCKING_CONTAINER, parent);
        base.getProperties().put(DockingDesktop.DOCKING_CONTAINER, this);
        setOrientation(position.getOrientation());
        ObservableList<Node> items = getItems();
        if (position == Split.LEFT || position == Split.TOP) {
            items.add(new TabDockingContainer(desktop, this, dockable));
            items.add(base);
        } else {
            items.add(base);
            items.add(new TabDockingContainer(desktop, this, dockable));
        }
        setDividerPositions(proportion);
    }

    @Override
    public void remove(Dockable dockable) {
        remove(dockable.getComponent());
    }

    @Override
    public void split(Dockable base, Dockable dockable, Split position, double proportion) {
        throw new RuntimeException("Not expected to reach here...");
    }

    @Override
    public void tab(Dockable base, Dockable dockable, int order, boolean select) {
        throw new RuntimeException("Not expected to reach here...");
    }

    @Override
    public void remove(Node container) {
        container.getProperties().remove(DockingDesktop.DOCKING_CONTAINER);
        getItems().remove(container);
        if (getItems().size() == 0) {
            ((IDockingContainer) getProperties().get(DockingDesktop.DOCKING_CONTAINER)).remove(this);
        }
    }

    @Override
    public void replace(Node base, INewDockingContainer indc) {
        double[] dividerPositions = getDividerPositions();
        ObservableList<Node> items = getItems();
        int indexOf = items.indexOf(base);
        items.remove(indexOf);
        items.add(indexOf, indc.get());
        setDividerPositions(dividerPositions);
    }

    @Override
    public void getDockables(List<DockableState> dockables) {
        ObservableList<Node> items = getItems();
        for (Node node : items) {
            ((IDockingContainer) node).getDockables(dockables);
        }
    }

    @Override
    public void debugPrint(String indent) {
        System.out.println(indent + "split(" + getProperties().get(DockingDesktop.DOCKING_CONTAINER) + ")");
        ObservableList<Node> items = getItems();
        for (Node node : items) {
            ((IDockingContainer) node).debugPrint(indent + "  ");
        }
    }
}
