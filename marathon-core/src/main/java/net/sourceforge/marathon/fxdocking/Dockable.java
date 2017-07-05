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

import javafx.collections.ObservableMap;
import javafx.scene.Node;
import net.sourceforge.marathon.fxdocking.DockableState.State;

public abstract class Dockable {

    public static final Logger LOGGER = Logger.getLogger(Dockable.class.getName());

    abstract public DockKey getDockKey();

    abstract public Node getComponent();

    @Override public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (getDockKey() == null ? 0 : getDockKey().hashCode());
        return result;
    }

    @Override public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Dockable other = (Dockable) obj;
        if (getDockKey() == null) {
            if (other.getDockKey() != null) {
                return false;
            }
        } else if (!getDockKey().equals(other.getDockKey())) {
            return false;
        }
        return true;
    }

    public IDockingContainer getContainer() {
        return (IDockingContainer) getComponent().getProperties().get(DockingDesktop.DOCKING_CONTAINER);
    }

    public void setContainer(IDockingContainer container) {
        ObservableMap<Object, Object> properties = getComponent().getProperties();
        if (container == null) {
            properties.remove(DockingDesktop.DOCKING_CONTAINER);
        } else {
            properties.put(DockingDesktop.DOCKING_CONTAINER, container);
        }
    }

    public DockableState getDockableState() {
        return new DockableState(this, State.DOCKED);
    }
}
