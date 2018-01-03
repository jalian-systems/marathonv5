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
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import junit.framework.Test;
import net.sourceforge.marathon.resource.ResourceView.Operation;
import net.sourceforge.marathon.runtime.api.IConsole;

public class DummyResource extends Resource {

    public static final Logger LOGGER = Logger.getLogger(DummyResource.class.getName());

    @Override public Test getTest(boolean acceptChecklist, IConsole console) throws IOException {
        return null;
    }

    @Override public String getName() {
        return null;
    }

    @Override public Resource rename(String text) {
        return null;
    }

    @Override public boolean canRename() {
        return false;
    }

    @Override public boolean canRun() {
        return false;
    }

    @Override public boolean canOpen() {
        return false;
    }

    @Override public boolean copy(Map<DataFormat, Object> content) {
        return false;
    }

    @Override public void paste(Clipboard clipboard, Operation operation) {
    }

    @Override public void pasteInto(Clipboard clipboard, Operation operation) {
    }

    @Override public Optional<ButtonType> delete(Optional<ButtonType> option) {
        return null;
    }

    @Override public boolean canDelete() {
        return false;
    }

    @Override public void refresh() {
    }

    @Override public Path getFilePath() {
        return null;
    }

    @Override public boolean canPlaySingle() {
        return false;
    }

    @Override public boolean canHide() {
        return false;
    }

    @Override public void hide() {
    }

    @Override public MenuItem[] getUnhideMenuItem() {
        return null;
    }

    @Override public List<Resource> findNodes(Resource resource, List<Resource> found) {
        return null;
    }

    @Override public void deleted() {
    }

}
