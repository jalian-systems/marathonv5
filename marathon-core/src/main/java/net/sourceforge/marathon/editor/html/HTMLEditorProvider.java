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
package net.sourceforge.marathon.editor.html;

import java.util.logging.Logger;

import net.sourceforge.marathon.editor.IEditor;
import net.sourceforge.marathon.editor.IEditorProvider;

public class HTMLEditorProvider implements IEditorProvider {

    public static final Logger LOGGER = Logger.getLogger(HTMLEditorProvider.class.getName());

    @Override
    public IEditor get(boolean linenumbers, int startLineNumber, EditorType type, boolean withToolbar) {
        return new HTMLView();
    }

    @Override
    public boolean supports(EditorType type) {
        return type == EditorType.HTML;
    }

}
