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

public abstract class CompositeScriptElement implements IScriptElement {

    public static final Logger LOGGER = Logger.getLogger(CompositeScriptElement.class.getName());

    private static final long serialVersionUID = 5400907759785664633L;
    private RecordableList list = new RecordableList();

    public void add(IScriptElement tag) {
        list.add(tag);
    }

    public void addFirst(IScriptElement tag) {
        list.addFirst(tag);
    }

    @Override
    public String toScriptCode() {
        return list.toScriptCode();
    }

    public RecordableList getChildren() {
        return list;
    }

    /**
     * this is different than for action - action just compares text - we don't
     * want to compare our children's text, so unless this is the same instance,
     * containers are not the same objects
     */
    @Override
    public boolean equals(Object that) {
        return this == that;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public abstract boolean owns(CompositeScriptElement child);

    public ComponentId getComponentId() {
        return null;
    }

    public boolean canOverride(IScriptElement other) {
        return false;
    }
}
