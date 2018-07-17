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
package net.sourceforge.marathon.display;

import java.util.logging.Logger;

import javafx.collections.ListChangeListener;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeItem;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fxdocking.DockKey;
import net.sourceforge.marathon.fxdocking.DockKey.TabPolicy;
import net.sourceforge.marathon.fxdocking.Dockable;
import net.sourceforge.marathon.fxdocking.ToolBarContainer;
import net.sourceforge.marathon.fxdocking.ToolBarContainer.Orientation;
import net.sourceforge.marathon.fxdocking.ToolBarPanel;
import net.sourceforge.marathon.fxdocking.VLToolBar;
import net.sourceforge.marathon.resource.IResourceActionHandler;
import net.sourceforge.marathon.resource.IResourceActionSource;
import net.sourceforge.marathon.resource.IResourceChangeListener;
import net.sourceforge.marathon.resource.Project;
import net.sourceforge.marathon.resource.Resource;
import net.sourceforge.marathon.resource.ResourceView;
import net.sourceforge.marathon.resource.Watcher;
import net.sourceforge.marathon.resource.navigator.ProjectFolderResource;

public class NavigatorPanel extends Dockable implements IResourceActionSource, IResourceChangeListener {

    public static final Logger LOGGER = Logger.getLogger(NavigatorPanel.class.getName());

    private static final DockKey DOCK_KEY = new DockKey("Navigator", "Navigator", "Display files and folders",
            FXUIUtils.getIcon("browse"), TabPolicy.NotClosable, Side.LEFT);
    private Node component;
    private ResourceView resourceView;

    public NavigatorPanel(IResourceActionHandler handler, IResourceChangeListener listener, Project project) {
        ToolBarContainer container = ToolBarContainer.createDefaultContainer(Orientation.RIGHT);
        resourceView = new ResourceView(this, new ProjectFolderResource(new Watcher()), handler, listener);
        container.setContent(resourceView);
        ToolBarPanel toolBarPanel = container.getToolBarPanel();
        VLToolBar toolbar = createToolbar(resourceView);
        toolBarPanel.add(toolbar);
        component = container;
    }

    public VLToolBar createToolbar(ResourceView resourceView) {
        VLToolBar toolbar = new VLToolBar();
        Button expandAll = FXUIUtils.createButton("expandAll", "Expand the resource tree");
        expandAll.setOnAction((event) -> resourceView.expandAll());
        toolbar.add(expandAll);
        Button collapseAll = FXUIUtils.createButton("collapseAll", "Collapse the resource tree");
        collapseAll.setOnAction((event) -> resourceView.collapseAll());
        toolbar.add(collapseAll);
        toolbar.add(new Separator(javafx.geometry.Orientation.VERTICAL));
        Button cut = FXUIUtils.createButton("cut", "Cut the selected content to clipboard");
        cut.setOnAction((event) -> resourceView.cut());
        toolbar.add(cut);
        Button copy = FXUIUtils.createButton("copy", "Copy the selected content to clipboard");
        copy.setOnAction((event) -> resourceView.copy());
        toolbar.add(copy);
        Button paste = FXUIUtils.createButton("paste", "Paste clipboard contents");
        paste.setOnAction((event) -> resourceView.paste());
        toolbar.add(paste);
        ListChangeListener<? super TreeItem<Resource>> listener = new ListChangeListener<TreeItem<Resource>>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends TreeItem<Resource>> c) {
                cut.setDisable(resourceView.getSelectionModel().getSelectedItems().size() <= 0);
                copy.setDisable(resourceView.getSelectionModel().getSelectedItems().size() <= 0);
                paste.setDisable(resourceView.getSelectionModel().getSelectedItems().size() != 1);
            }
        };
        resourceView.getSelectionModel().getSelectedItems().addListener(listener);
        return toolbar;
    }

    @Override
    public DockKey getDockKey() {
        return DOCK_KEY;
    }

    @Override
    public Node getComponent() {
        return component;
    }

    @Override
    public void deleted(IResourceActionSource source, Resource resource) {
        resourceView.deleted(source, resource);
    }

    @Override
    public String toString() {
        return "Navigator Panel";
    }

    @Override
    public void updated(IResourceActionSource source, Resource resource) {
        resourceView.updated(source, resource);
    }

    @Override
    public void moved(IResourceActionSource source, Resource from, Resource to) {
        resourceView.moved(source, from, to);
    }

    @Override
    public void copied(IResourceActionSource source, Resource from, Resource to) {
        resourceView.copied(source, from, to);
    }
}
