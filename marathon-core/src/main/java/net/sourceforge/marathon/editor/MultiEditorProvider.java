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
package net.sourceforge.marathon.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MultiEditorProvider implements IEditorProvider {

    public static final Logger LOGGER = Logger.getLogger(MultiEditorProvider.class.getName());

    private List<IEditorProvider> providers = new ArrayList<IEditorProvider>();
    private IEditorProvider defaultProvider;

    @Override public IEditor get(boolean linenumbers, int startLineNumber, EditorType type, boolean withToolbar) {
        if (type == IEditorProvider.EditorType.OTHER) {
            return defaultProvider.get(linenumbers, startLineNumber, type, withToolbar);
        }
        return findProvider(type).get(linenumbers, startLineNumber, type, withToolbar);
    }

    private IEditorProvider findProvider(EditorType type) {
        for (IEditorProvider provider : providers) {
            if (provider.supports(type)) {
                return provider;
            }
        }
        return defaultProvider;
    }

    @Override public boolean supports(EditorType type) {
        throw new UnsupportedOperationException("Multi editor provider can't support supports");
    }

    public void add(IEditorProvider provider, boolean isDefault) {
        providers.add(provider);
        if (isDefault) {
            defaultProvider = provider;
        }
    }

}
