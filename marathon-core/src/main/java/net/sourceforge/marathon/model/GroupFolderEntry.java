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
package net.sourceforge.marathon.model;

import java.io.IOException;
import java.nio.file.Path;

import junit.framework.Test;
import net.sourceforge.marathon.junit.TestCreator;
import net.sourceforge.marathon.runtime.api.IConsole;

public class GroupFolderEntry extends GroupEntry {

    private Path path;

    public GroupFolderEntry(String name) throws IOException {
        super(GroupEntryType.FOLDER, name);
        path = new TestCreator(false, null).getFile(name).toPath();
    }

    @Override public String getName() {
        return super.getRawName();
    }

    @Override public Path getFilePath() {
        return path;
    }

    @Override public void setName(String name) {
    }

    @Override public Test getTest(boolean acceptChecklist, IConsole console) throws IOException {
        return new TestCreator(acceptChecklist, console).getTest(super.getRawName());
    }

    @Override public boolean canPlaySingle() {
        return false;
    }

    @Override public String toString() {
        return super.getRawName();
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (path == null ? 0 : path.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GroupFolderEntry other = (GroupFolderEntry) obj;
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        return true;
    }

    @Override public void refresh() {
    }

    @Override public void rename(String text) {
    }
}
