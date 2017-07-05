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
package net.sourceforge.marathon.resource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import junit.framework.Test;
import net.sourceforge.marathon.resource.ResourceView.Operation;
import net.sourceforge.marathon.runtime.api.IConsole;

public abstract class Resource extends TreeItem<Resource> {

    public static final Logger LOGGER = Logger.getLogger(Resource.class.getName());

    public Resource() {
        setValue(this);
    }

    @Override public String toString() {
        return getName();
    }

    public abstract Test getTest(boolean acceptChecklist, IConsole console) throws IOException;

    public abstract String getName();

    public abstract Resource rename(String text);

    public abstract boolean canRename();

    public abstract boolean canRun();

    public abstract boolean canOpen();

    public abstract boolean copy(Map<DataFormat, Object> content);

    public abstract void paste(Clipboard clipboard, Operation operation);

    public abstract void pasteInto(Clipboard clipboard, Operation operation);

    public abstract Optional<ButtonType> delete(Optional<ButtonType> option);

    public abstract boolean canDelete();

    public abstract void refresh();

    public abstract Path getFilePath();

    public abstract boolean canPlaySingle();

    public abstract boolean canHide();

    public abstract void hide();

    public abstract MenuItem[] getUnhideMenuItem();

    public abstract List<Resource> findNodes(Resource resource, List<Resource> found);

    public abstract void deleted();

    public void updated(Resource resource) {
    }

    public void moved() {
    }

    public boolean isTestFile() {
        return false;
    }

    public boolean isSuiteFile() {
        return false;
    }

    public boolean isFeatureFile() {
        return false;
    }

    public boolean isIssueFile() {
        return false;
    }

    public boolean isStoryFile() {
        return false;
    }

    public boolean droppable(Dragboard dragboard) {
        return true;
    }

    public boolean hasProperties() {
        return false;
    }

    public String getDescription() {
        return null;
    }
}
