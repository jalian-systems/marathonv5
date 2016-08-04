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

public class WindowElement extends CompositeScriptElement {
    private static final long serialVersionUID = 1L;
    private WindowId windowId;

    public WindowElement(WindowId windowId) {
        this.windowId = windowId;
    }

    @Override public WindowId getWindowId() {
        return windowId;
    }

    public String getTitle() {
        return windowId.getTitle();
    }

    @Override public boolean owns(CompositeScriptElement child) {
        if (!(child instanceof WindowElement) || getTitle().equals(((WindowElement) child).windowId.getParentTitle())) {
            return true;
        }
        return false;
    }

    @Override public String toScriptCode() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(Indent.getIndent() + ScriptModel.getModel().getScriptCodeForWindow(windowId));
        Indent.incIndent();
        buffer.append(super.toScriptCode());
        Indent.decIndent();
        buffer.append(Indent.getIndent() + ScriptModel.getModel().getScriptCodeForWindowClose(windowId));
        return buffer.toString();
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof WindowElement) {
            WindowElement that = (WindowElement) obj;
            new ObjectComparator();
            return ObjectComparator.compare(that.windowId, this.windowId) == 0;
        }
        return false;
    }

    @Override public int hashCode() {
        return windowId.hashCode();
    }

    @Override public boolean isUndo() {
        return false;
    }

    @Override public IScriptElement getUndoElement() {
        return null;
    }
}
