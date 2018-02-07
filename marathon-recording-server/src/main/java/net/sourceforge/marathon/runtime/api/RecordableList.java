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
package net.sourceforge.marathon.runtime.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class RecordableList {

    public static final Logger LOGGER = Logger.getLogger(RecordableList.class.getName());

    private List<IScriptElement> impl = new ArrayList<IScriptElement>();

    public int size() {
        return impl.size();
    }

    public ComponentId getComponentId() {
        return null;
    }

    public WindowId getWindowId() {
        return null;
    }

    public String toScriptCode() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < impl.size(); i++) {
            IScriptElement recordable = impl.get(i);
            if (recordable instanceof CompositeScriptElement && buffer.length() > 0) {
                buffer.append("\n");
            }
            buffer.append(recordable.toScriptCode());
            if (recordable instanceof CompositeScriptElement && nextIsNotAContainer(i)) {
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }

    private boolean nextIsNotAContainer(int i) {
        return i + 1 < impl.size() && !(impl.get(i + 1) instanceof CompositeScriptElement);
    }

    public IScriptElement get(int i) {
        return impl.get(i);
    }

    public void add(IScriptElement recordable) {
        impl.add(recordable);
    }

    public Iterator<IScriptElement> iterator() {
        return impl.iterator();
    }

    @Override public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RecordableList)) {
            return false;
        }
        return this.impl.equals(((RecordableList) o).impl);
    }

    @Override public int hashCode() {
        return impl.hashCode();
    }

    public IScriptElement last() {
        if (size() == 0) {
            return null;
        }
        return impl.get(size() - 1);
    }

    public void removeLast() {
        if (size() > 0) {
            impl.remove(size() - 1);
        }
    }

    public boolean canOverride(IScriptElement other) {
        return false;
    }

    public void addFirst(IScriptElement tag) {
        impl.add(0, tag);
    }
}
