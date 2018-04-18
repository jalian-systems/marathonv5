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

import java.util.logging.Logger;

public class TestRootElement extends CompositeScriptElement {

    public static final Logger LOGGER = Logger.getLogger(TestRootElement.class.getName());

    private static final long serialVersionUID = 1L;
    private String story;

    public TestRootElement(String story) {
        this.story = story;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TestRootElement)) {
            return false;
        }
        return super.equals(obj) && ObjectComparator.compare(story, ((TestRootElement) obj).story) == 0;
    }

    @Override public int hashCode() {
        return super.hashCode();
    }

    @Override public WindowId getWindowId() {
        return null;
    }

    @Override public boolean owns(CompositeScriptElement child) {
        return true;
    }

    @Override public boolean isUndo() {
        return false;
    }

    @Override public IScriptElement getUndoElement() {
        return null;
    }
}
