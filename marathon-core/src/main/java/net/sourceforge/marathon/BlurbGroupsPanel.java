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
package net.sourceforge.marathon;

import java.util.logging.Logger;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import net.sourceforge.marathon.display.AbstractGroupsPanel;
import net.sourceforge.marathon.fxdocking.DockKey;
import net.sourceforge.marathon.fxdocking.DockKey.TabPolicy;
import net.sourceforge.marathon.model.Group.GroupType;
import net.sourceforge.marathon.resource.IResourceActionHandler;
import net.sourceforge.marathon.resource.IResourceActionSource;
import net.sourceforge.marathon.resource.IResourceChangeListener;
import net.sourceforge.marathon.resource.Resource;

public class BlurbGroupsPanel extends AbstractGroupsPanel {

    public static final Logger LOGGER = Logger.getLogger(BlurbGroupsPanel.class.getName());

    private DockKey DOCK_KEY;
    private Node node;

    public BlurbGroupsPanel(GroupType type) {
        DOCK_KEY = new DockKey(type.dockName(), type.dockName(), type.dockDescription(), type.dockIcon(), TabPolicy.Closable,
                Side.LEFT);
        Node text = new Text(type.dockName() + " Available only in MarathonITE");
        StackPane sp = new StackPane(text);
        node = sp;
    }

    @Override public void deleted(IResourceActionSource source, Resource resource) {
    }

    @Override public void updated(IResourceActionSource source, Resource resource) {
    }

    @Override public void moved(IResourceActionSource source, Resource from, Resource to) {
    }

    @Override public void copied(IResourceActionSource source, Resource from, Resource to) {
    }

    @Override public void initialize(IResourceActionHandler handler, IResourceChangeListener listener) {
    }

    @Override public DockKey getDockKey() {
        return DOCK_KEY;
    }

    @Override public Node getComponent() {
        return node;
    }

}
