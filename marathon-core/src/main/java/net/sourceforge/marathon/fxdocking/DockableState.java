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

import org.json.JSONObject;

public class DockableState {

    public static final Logger LOGGER = Logger.getLogger(DockableState.class.getName());

    private Dockable dockable;
    private State state;

    public enum State {
        CLOSED, DOCKED, MAXIMIZED
    }

    public DockableState(Dockable dockable, State state) {
        this.dockable = dockable;
        this.state = state;
    }

    public DockableState(JSONObject jsonObject) {
    }

    public Dockable getDockable() {
        return dockable;
    }

    public boolean isClosed() {
        return state == State.CLOSED;
    }

    public boolean isMaximized() {
        return state == State.MAXIMIZED;
    }

    public boolean isDocked() {
        return state == State.DOCKED;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override public String toString() {
        return "DockableState [dockable=" + dockable.getDockKey() + "]";
    }

    public JSONObject toJSON() {
        return null;
    }

}
