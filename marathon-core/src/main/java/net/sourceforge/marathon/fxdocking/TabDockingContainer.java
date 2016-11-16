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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.TabPaneSkin;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.sourceforge.marathon.fxdocking.DockKey.TabPolicy;
import net.sourceforge.marathon.fxdocking.DockableState.State;
import net.sourceforge.marathon.fxdocking.DockingConstants.Split;

public class TabDockingContainer extends TabPane implements IDockingContainer, TabbedDockableContainer {

    private Set<Dockable> dockables = new HashSet<>();
    private DockingDesktop desktop;

    ChangeListener<? super Tab> listener = new ChangeListener<Tab>() {
        @Override public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
            Dockable o = null, n = null;
            for (Dockable d : dockables) {
                if (oldValue != null && oldValue.getContent() == d.getComponent()) {
                    o = d;
                }
                if (newValue != null && newValue.getContent() == d.getComponent()) {
                    n = d;
                }
            }
            desktop.fireDockableSelectionEvent(o, n);
        }
    };

    public TabDockingContainer(DockingDesktop desktop, IDockingContainer parent, Dockable base, Dockable dockable, int order,
            boolean select) {
        this.desktop = desktop;
        dockables.add(dockable);
        dockables.add(base);
        setMaxHeight(Double.MAX_VALUE);
        setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);
        getProperties().put(DockingDesktop.DOCKING_CONTAINER, parent);
        base.setContainer(this);
        dockable.setContainer(this);
        getTabs().add(newTab(base));
        getTabs().add(newTab(dockable));
        if (select) {
            getSelectionModel().select(1);
        }
        getSelectionModel().selectedItemProperty().addListener(listener);
        setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
        setSide(base.getDockKey().getSide());
    }

    public TabDockingContainer(DockingDesktop desktop, IDockingContainer parent, Dockable base) {
        this.desktop = desktop;
        dockables.add(base);
        setMaxHeight(Double.MAX_VALUE);
        setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);
        getProperties().put(DockingDesktop.DOCKING_CONTAINER, parent);
        base.setContainer(this);
        getTabs().add(newTab(base));
        getSelectionModel().selectedItemProperty().addListener(listener);
        setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
        setSide(base.getDockKey().getSide());
    }

    @Override public void remove(Dockable dockable) {
        dockables.remove(dockable);
        remove(dockable.getComponent());
        desktop.fireDockableStateChangeEvent(dockable, State.DOCKED, State.CLOSED);
    }

    @Override public void split(Dockable base, Dockable dockable, Split position, double proportion) {
        IDockingContainer parent = (IDockingContainer) getProperties().get(DockingDesktop.DOCKING_CONTAINER);
        parent.replace(this, () -> new SplitDockingContainer(desktop, parent, this, dockable, position, proportion));
    }

    @Override public void tab(Dockable base, Dockable dockable, int order, boolean select) {
        dockables.add(base);
        dockables.add(dockable);
        base.setContainer(this);
        dockable.setContainer(this);
        ObservableList<Tab> tabs = getTabs();
        if (order > tabs.size()) {
            order = tabs.size();
        }
        getTabs().add(order, newTab(dockable));
        if (select) {
            getSelectionModel().select(order);
        }
    }

    private Tab newTab(Dockable dockable) {
        DockKey dockKey = dockable.getDockKey();
        Tab tab = new Tab(dockKey.getName(), dockable.getComponent());
        if (dockKey.getPolicy() == TabPolicy.NotClosable) {
            tab.setClosable(false);
        }
        if (dockKey.isCloseOptionsNeeded()) {
            MenuItem closeMenuItem = new MenuItem("Close");
            closeMenuItem.setOnAction((e) -> requestClose(tab));
            ContextMenu contextMenu = new ContextMenu(closeMenuItem);
            contextMenu.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
                if (isNowShowing) {
                    populateMenuItems(contextMenu, tab);
                }
            });
            tab.setContextMenu(contextMenu);
        }
        dockKey.nameProperty().addListener((event, o, n) -> tab.setText(n));
        tab.setOnClosed((event) -> {
            remove(dockable);
        });
        tab.setGraphic(dockKey.getIcon());
        tab.setOnCloseRequest((event) -> {
            desktop.fireDockableStateWillChangeEvent(dockable, State.DOCKED, State.CLOSED, event);
        });
        return tab;
    }

    private void populateMenuItems(ContextMenu contextMenu, Tab tab) {
        int tabCount = getTabs().size();
        int tabIndex = getTabs().indexOf(tab);
        ObservableList<MenuItem> items = contextMenu.getItems();
        items.clear();
        MenuItem closeMenuItem = new MenuItem("Close");
        closeMenuItem.setOnAction((e) -> requestClose(tab));
        items.add(closeMenuItem);
        if (tabCount > 1) {
            MenuItem closeRestMenuItem = new MenuItem("Close Others");
            closeRestMenuItem.setOnAction((e) -> closeOtherTabs(tab));
            items.add(closeRestMenuItem);
        }
        if (tabCount > 1 && tabIndex != 0) {
            MenuItem closeLeftTabsMenuItem = new MenuItem("Close Tabs to the Left");
            closeLeftTabsMenuItem.setOnAction((e) -> closeTabsToLeft(tab));
            items.add(closeLeftTabsMenuItem);
        }
        if (tabCount > 1 && tabIndex != tabCount - 1) {
            MenuItem closeRigthTabsMenuItem = new MenuItem("Close Tabs to the Right");
            closeRigthTabsMenuItem.setOnAction((e) -> closeTabsToRight(tab));
            items.add(closeRigthTabsMenuItem);
        }
        if (tabCount > 1) {
            MenuItem closeAllMenuItem = new MenuItem("Close All");
            closeAllMenuItem.setOnAction((e) -> closeAllTabs());
            items.addAll(new SeparatorMenuItem(), closeAllMenuItem);
        }
    }

    public void requestClose(Tab tab) {
        TabPaneBehavior behavior = getBehavior(tab);
        if (behavior.canCloseTab(tab)) {
            behavior.closeTab(tab);
        }
    }

    private TabPaneBehavior getBehavior(Tab tab) {
        return ((TabPaneSkin) tab.getTabPane().getSkin()).getBehavior();
    }

    private void closeOtherTabs(Tab tab) {
        List<Tab> tabs = getTabs().stream().filter((t) -> tab != t).collect(Collectors.toList());
        tabs.stream().forEach((t) -> requestClose(t));
    }

    private void closeTabsToLeft(Tab tab) {
        List<Tab> leftTabs = getTabs().stream().filter((t) -> getTabs().indexOf(t) < getTabs().indexOf(tab))
                .collect(Collectors.toList());
        leftTabs.stream().forEach((t) -> requestClose(t));
    }

    private void closeAllTabs() {
        List<Tab> allTabs = getTabs().stream().collect(Collectors.toList());
        allTabs.stream().forEach((t) -> requestClose(t));
    }

    private void closeTabsToRight(Tab tab) {
        List<Tab> rightTabs = getTabs().stream().filter((t) -> getTabs().indexOf(t) > getTabs().indexOf(tab))
                .collect(Collectors.toList());
        rightTabs.stream().forEach((t) -> requestClose(t));
    }

    @Override public void remove(Node node) {
        node.getProperties().remove(DockingDesktop.DOCKING_CONTAINER);
        ObservableList<Tab> tabs = getTabs();
        Tab found = null;
        for (Tab tab : tabs) {
            if (tab.getContent() == node) {
                found = tab;
                break;
            }
        }
        if (found != null) {
            tabs.remove(found);
        }
        if (tabs.size() == 0) {
            ((IDockingContainer) getProperties().get(DockingDesktop.DOCKING_CONTAINER)).remove(this);
        }
    }

    @Override public void replace(Node base, INewDockingContainer indc) {
        ObservableList<Tab> tabs = getTabs();
        Tab found = null;
        for (Tab tab : tabs) {
            if (tab.getContent() == base) {
                found = tab;
                break;
            }
        }
        if (found != null) {
            int index = tabs.indexOf(found);
            tabs.remove(index);
            found.setContent(indc.get());
            tabs.add(index, found);
        }
    }

    @Override public void getDockables(List<DockableState> dockables) {
        ObservableList<Tab> tabs = getTabs();
        for (Tab tab : tabs) {
            if (tab.getContent() instanceof IDockingContainer) {
                ((IDockingContainer) tab.getContent()).getDockables(dockables);
            } else {
                for (Dockable dockable : this.dockables) {
                    Node component = dockable.getComponent();
                    Node content = tab.getContent();
                    if (component == content) {
                        dockables.add(dockable.getDockableState());
                    }
                }
            }
        }
    }

    @Override public void debugPrint(String indent) {
        System.out.println(indent + "tab(" + getProperties().get(DockingDesktop.DOCKING_CONTAINER) + ")");
        ObservableList<Tab> tabs = getTabs();
        for (Tab tab : tabs) {
            if (tab.getContent() instanceof IDockingContainer) {
                ((IDockingContainer) tab.getContent()).debugPrint(indent + "  ");
            } else {
                System.out.println(indent + "  " + tab.getText());
            }
        }
    }

    @Override public Dockable getSelectedDockable() {
        Tab selected = getSelectionModel().getSelectedItem();
        if (selected != null) {
            for (Dockable dockable : dockables) {
                if (dockable.getComponent() == selected.getContent()) {
                    return dockable;
                }
            }
        }
        return null;
    }

    @Override public int indexOfDockable(Dockable dockable) {
        int index = findIndex(dockable);
        return index;
    }

    private int findIndex(Dockable dockable) {
        ObservableList<Tab> tabs = getTabs();
        int index = 0;
        for (Tab tab : tabs) {
            if (tab.getContent() == dockable.getComponent()) {
                return index;
            }
            index++;
        }
        return -1;
    }

    @Override public void setSelectedDockable(Dockable dockable) {
        ObservableList<Tab> tabs = getTabs();
        for (Tab tab : tabs) {
            if (tab.getContent() == dockable.getComponent()) {
                getSelectionModel().select(tab);
            }
        }
    }
}
