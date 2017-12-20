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
package net.sourceforge.marathon.recorder;

import java.util.logging.Logger;

import net.sourceforge.marathon.runtime.api.ComponentId;
import net.sourceforge.marathon.runtime.api.IScriptElement;
import net.sourceforge.marathon.runtime.api.Indent;
import net.sourceforge.marathon.runtime.api.ScriptModel;
import net.sourceforge.marathon.runtime.api.WindowId;

public class ShowChecklistElement implements IScriptElement {

    public static final Logger LOGGER = Logger.getLogger(ShowChecklistElement.class.getName());

    private static final long serialVersionUID = 1L;
    private String fileName;
    private final WindowId windowId;

    public ShowChecklistElement(WindowId windowId, String fileName) {
        this.windowId = windowId;
        this.fileName = fileName;
    }

    @Override public String toScriptCode() {
        return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForShowChecklist(fileName);
    }

    public ComponentId getComponentId() {
        return null;
    }

    @Override public WindowId getWindowId() {
        return windowId;
    }

    @Override public boolean isUndo() {
        return false;
    }

    @Override public IScriptElement getUndoElement() {
        return null;
    }
}
