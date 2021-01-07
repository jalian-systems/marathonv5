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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import junit.framework.Test;
import net.sourceforge.marathon.json.JSONObject;
import net.sourceforge.marathon.junit.TestCreator;
import net.sourceforge.marathon.runtime.api.IConsole;

public class GroupGroupEntry extends GroupEntry {

    public static final Logger LOGGER = Logger.getLogger(GroupGroupEntry.class.getName());

    private Group group;

    public GroupGroupEntry(GroupEntryType entryType, String name) throws IOException {
        super(entryType, name);
        Path path = entryType.groupType().dir().toPath().resolve(name);
        group = Group.createGroup(path);
    }

    @Override
    public String getName() {
        return group.getName();
    }

    @Override
    public Path getFilePath() {
        return group.getPath();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject t = new JSONObject();
        t.put("type", getType().name());
        File suiteDirectory = getType().groupType().dir();
        String path = suiteDirectory.toPath().relativize(group.getPath()).toString();
        path.replace(File.separatorChar, '/');
        t.put("name", path);
        return t;
    }

    @Override
    public void setName(String name) {
        group.setName(name);
    }

    @Override
    public Test getTest(boolean acceptChecklist, IConsole console) throws IOException {
        return new TestCreator(acceptChecklist, console).getTest(group);
    }

    @Override
    public boolean canPlaySingle() {
        return false;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (group == null ? 0 : group.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GroupGroupEntry other = (GroupGroupEntry) obj;
        if (group == null) {
            if (other.group != null) {
                return false;
            }
        } else if (!group.equals(other.group)) {
            return false;
        }
        return true;
    }

    @Override
    public void refresh() {
        try {
            group = Group.createGroup(group.getPath());
        } catch (IOException e) {
        }
    }

    @Override
    public void rename(String text) {
        group.setName(text);
        try {
            Group.updateFile(group);
        } catch (IOException e) {
        }
    }
}
