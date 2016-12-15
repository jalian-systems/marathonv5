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

import net.sourceforge.marathon.action.AbstractScriptElement;
import net.sourceforge.marathon.runtime.api.Indent;
import net.sourceforge.marathon.runtime.api.ScriptModel;
import net.sourceforge.marathon.runtime.api.WindowId;

public class InsertScriptElement extends AbstractScriptElement {
    private static final long serialVersionUID = 1L;
    private String function;
    private String pkg = null;

    public InsertScriptElement(WindowId windowId, String function) {
        super(null, windowId);
        this.function = ScriptModel.getModel().getFunctionFromInsertDialog(function);
        this.pkg = ScriptModel.getModel().getPackageFromInsertDialog(function);
    }

    @Override public String toScriptCode() {
        StringBuffer sb = new StringBuffer();
        sb.append(Indent.getIndent()).append(function).append("\n");
        return sb.toString();
    }

    public String getImportStatement() {
        return ScriptModel.getModel().getScriptCodeForImportAction(pkg, function);
    }

}
