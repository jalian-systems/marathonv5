/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.runtime.api;

import java.io.Serializable;

public class WindowId implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String title;
    private WindowId parent;
	private String containerType;

    protected WindowId() {
    }

    public WindowId(String title, WindowId parent, String containerType) {
        this.title = title;
        this.parent = parent;
		this.containerType = containerType;
    }

    public String getTitle() {
        return title;
    }

    public String getParentTitle() {
        if (parent == null)
            return null;
        return parent.getTitle();
    }

    public String toString() {
        if (parent != null)
            return parent.toString() + ">>" + getTitle();
        return getTitle();
    }

    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((containerType == null) ? 0 : containerType.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WindowId other = (WindowId) obj;
		if (containerType == null) {
			if (other.containerType != null)
				return false;
		} else if (!containerType.equals(other.containerType))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	public String getContainerType() {
		return containerType;
	}
	
    public WindowId getParent() {
        return parent;
    }

    public void addToTagInserter(TagInserter tagInserter, IScriptElement recordable) {
        if (parent != null)
            parent.addToTagInserter(tagInserter, null);
        tagInserter.add(new WindowElement(this), recordable);
    }

}
