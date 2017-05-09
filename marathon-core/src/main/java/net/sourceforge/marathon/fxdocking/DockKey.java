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

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Side;
import javafx.scene.Node;

public class DockKey {

    public static final Logger LOGGER = Logger.getLogger(DockKey.class.getName());

    public enum TabPolicy {
        NotClosable, Closable
    }

    private String key;
    private SimpleStringProperty name;
    private String tooltip;
    private Node icon;
    @SuppressWarnings("unused") private DockGroup dockGroup;
    private TabPolicy policy;
    private boolean closeOptions = false;
    private Side side;

    public DockKey(String dockKey, String name) {
        this(dockKey, name, null, null, null, Side.TOP);
    }

    public DockKey(String dockKey, String name, String tooltip, Node icon, TabPolicy policy, Side side) {
        this.key = dockKey;
        this.policy = policy;
        this.name = new SimpleStringProperty(name);
        this.tooltip = tooltip;
        this.icon = icon;
        this.side = side;
    }

    @Override public String toString() {
        return "DockKey [dockKey=" + key + ", name=" + name + ", tooltip=" + tooltip + "]";
    }

    public void setDockGroup(DockGroup dockGroup) {
        this.dockGroup = dockGroup;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String title) {
        name.set(title);
    }

    public String getName() {
        return name.get();
    }

    public Node getIcon() {
        return icon;
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (key == null ? 0 : key.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DockKey other = (DockKey) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

    public String getKey() {
        return key;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public TabPolicy getPolicy() {
        return policy;
    }

    public void setCloseOptions(boolean closeOptions) {
        this.closeOptions = closeOptions;
    }

    public boolean isCloseOptionsNeeded() {
        return closeOptions;
    }

    public Side getSide() {
        return side;
    }
}
