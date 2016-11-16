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

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.event.Event;
import javafx.scene.layout.BorderPane;
import net.sourceforge.marathon.fxdocking.DockableState.State;
import net.sourceforge.marathon.fxdocking.DockingConstants.Split;
import net.sourceforge.marathon.runtime.api.Constants;

public class DockingDesktop extends BorderPane {

    public static final String DOCKING_CONTAINER = "dockContainer";
    @SuppressWarnings("unused") private String name;

    private VBoxDockingContainer head;

    private Map<Class<?>, Set<Object>> eventListeners = new HashMap<>();

    public DockingDesktop(String name) {
        this.name = name;
        head = new VBoxDockingContainer(this);
        setCenter(head);
    }

    public void close(Dockable dockable) {
        IDockingContainer container = dockable.getContainer();
        if (container != null) {
            container.remove(dockable);
        }
    }

    public void unregisterDockable(Dockable dockable) {
        close(dockable);
    }

    public Dockable getSelectedDockable() {
        return null;
    }

    private void addEventListener(Class<?> c, Object l) {
        Set<Object> list = eventListeners.get(c);
        if (list == null) {
            list = new HashSet<>();
            eventListeners.put(c, list);
        }
        list.add(l);
    }

    public void addDockableSelectionListener(DockableSelectionListener dockingListener) {
        addEventListener(DockableSelectionListener.class, dockingListener);
    }

    public void addDockableStateWillChangeListener(DockableStateWillChangeListener stateListener) {
        addEventListener(DockableStateWillChangeListener.class, stateListener);
    }

    public void addDockableStateChangeListener(DockableStateChangeListener changeListener) {
        addEventListener(DockableStateChangeListener.class, changeListener);
    }

    public DockableState[] getDockables() {
        List<DockableState> dockables = new ArrayList<>();
        ((IDockingContainer) head).getDockables(dockables);
        return dockables.toArray(new DockableState[dockables.size()]);
    }

    public void addDockable(Dockable dockable) {
        IDockingContainer container = dockable.getContainer();
        if (container != null) {
            container.remove(dockable);
        }
        head.add(dockable);
    }

    private interface CreateDock {
        void create();
    };

    public void split(Dockable base, Dockable dockable, Split position, double proportion) {
        createDock(base, dockable,
                () -> (base.getContainer() == null ? head : base.getContainer()).split(base, dockable, position, proportion));
    }

    public void createTab(Dockable base, Dockable dockable, int order, boolean select) {
        createDock(base, dockable,
                () -> (base.getContainer() == null ? head : base.getContainer()).tab(base, dockable, order, select));
    }

    private void createDock(Dockable base, Dockable dockable, CreateDock cd) {
        IDockingContainer dockableContainer = dockable.getContainer();
        if (dockableContainer != null) {
            dockableContainer.remove(dockable);
        }
        cd.create();
    }

    public DockableState getDockableState(Dockable dockable) {
        if (dockable.getContainer() != null) {
            return dockable.getDockableState();
        }
        return null;
    }

    public JSONObject saveDockableState() {
        JSONArray a = new JSONArray();
        DockableState[] dockables = getDockables();
        Path projectPath = Constants.getProjectPath();
        for (DockableState dockableState : dockables) {
            String path = dockableState.getDockable().getDockKey().getKey();
            if (path.contains(File.separator)) {
                Path filePath = new File(path).toPath().toAbsolutePath();
                Path relativize = projectPath.relativize(filePath);
                a.put(relativize.toString().replace('\\', '/'));
            } else {
                a.put(path);
            }
        }
        JSONObject o = new JSONObject();
        o.put("dockables", a);
        return o;
    }

    public List<Dockable> readDockableState(JSONObject state, DockableResolver dockableResolver) {
        List<Dockable> lds = new ArrayList<>();
        JSONArray a = state.getJSONArray("dockables");
        for (int i = 0; i < a.length(); i++) {
            Dockable dockable = dockableResolver.resolveDockable(a.getString(i));
            if (dockable != null) {
                lds.add(dockable);
            }
        }
        return lds;
    }

    public void fireDockableSelectionEvent(Dockable previous, Dockable selected) {
        Set<Object> listeners = eventListeners.get(DockableSelectionListener.class);
        for (Object listener : listeners) {
            ((DockableSelectionListener) listener).selectionChanged(new DockableSelectionEvent(selected, previous));
        }
    }

    public void fireDockableStateWillChangeEvent(Dockable dockable, State currentState, State futureState, Event tabEvent) {
        Set<Object> listeners = eventListeners.get(DockableStateWillChangeListener.class);
        for (Object listener : listeners) {
            ((DockableStateWillChangeListener) listener)
                    .dockableStateWillChange(new DockableStateWillChangeEvent(dockable, currentState, futureState, tabEvent));
        }
    }

    public void fireDockableStateChangeEvent(Dockable dockable, State oldState, State newState) {
        Set<Object> listeners = eventListeners.get(DockableStateChangeListener.class);
        for (Object listener : listeners) {
            ((DockableStateChangeListener) listener)
                    .dockableStateChanged(new DockableStateChangeEvent(dockable, oldState, newState));
        }
    }

}
