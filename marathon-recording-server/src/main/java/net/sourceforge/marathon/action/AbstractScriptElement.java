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
package net.sourceforge.marathon.action;

import net.sourceforge.marathon.runtime.api.ComponentId;
import net.sourceforge.marathon.runtime.api.IScriptElement;
import net.sourceforge.marathon.runtime.api.WindowId;

public abstract class AbstractScriptElement implements IScriptElement {

    private static final long serialVersionUID = 4204837019479682968L;
    private ComponentId componentId;
    private WindowId windowId;

    public AbstractScriptElement(ComponentId componentId, WindowId windowId) {
        this.componentId = componentId;
        this.windowId = windowId;
    }

    public ComponentId getComponentId() {
        return componentId;
    }

    @Override
    public WindowId getWindowId() {
        return windowId;
    }

    @Override
    public String toString() {
        return toScriptCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractScriptElement && toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean isUndo() {
        return false;
    }

    @Override
    public IScriptElement getUndoElement() {
        return null;
    }
}
