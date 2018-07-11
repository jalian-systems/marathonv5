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
import java.util.logging.Logger;

import org.json.JSONObject;

import javafx.scene.Node;
import junit.framework.Test;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.model.Group.GroupType;
import net.sourceforge.marathon.runtime.api.IConsole;

public abstract class GroupEntry {

    public static final Logger LOGGER = Logger.getLogger(GroupEntry.GroupEntryType.class.getName());

    public enum GroupEntryType {
        SUITE(GroupType.SUITE, "tsuite"), FOLDER(null, "fldr_obj"), TEST(null, "test"), FEATURE(GroupType.FEATURE,
                "tfeature"), STORY(GroupType.STORY, "tstory"), ISSUE(GroupType.ISSUE, "tissue");

        private GroupType groupType;
        private String icon;

        GroupEntryType(GroupType groupType, String icon) {
            this.groupType = groupType;
            this.icon = icon;
        }

        public GroupType groupType() {
            return groupType;
        }

        public Node icon() {
            return FXUIUtils.getIcon(icon);
        }
    }

    private GroupEntryType type;
    protected String name;

    public GroupEntry(GroupEntryType type, String name) {
        this.type = type;
        this.name = name;
    }

    public GroupEntryType getType() {
        return type;
    }

    public abstract String getName();

    public abstract Path getFilePath();

    public JSONObject toJSON() {
        JSONObject t = new JSONObject();
        t.put("type", getType().name());
        t.put("name", name);
        return t;
    }

    public abstract void setName(String name);

    public abstract Test getTest(boolean acceptChecklist, IConsole console) throws IOException;

    public abstract boolean canPlaySingle();

    @Override
    public abstract String toString();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    public abstract void refresh();

    public abstract void rename(String text);

    public String getRawName() {
        return name;
    }

    public Node getIcon() {
        return getType().icon();
    }

}
