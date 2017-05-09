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

import javafx.event.Event;
import net.sourceforge.marathon.fxdocking.DockableState.State;

public class DockableStateWillChangeEvent {

    public static final Logger LOGGER = Logger.getLogger(DockableStateWillChangeEvent.class.getName());

    private DockableState current;
    private DockableState future;
    private Event tabEvent;

    public DockableStateWillChangeEvent(Dockable dockable, State currentState, State futureState, Event tabEvent) {
        this.tabEvent = tabEvent;
        current = new DockableState(dockable, currentState);
        future = new DockableState(dockable, futureState);
    }

    public DockableState getFutureState() {
        return future;
    }

    public void cancel() {
        tabEvent.consume();
    }

    public DockableState getCurrentState() {
        return current;
    }
}
