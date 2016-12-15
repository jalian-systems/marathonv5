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

import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import net.sourceforge.marathon.util.AbstractSimpleAction;

public abstract class AbstractEditor implements IEditor {

    @Override public void setStatusBar(IStatusBar statusBar) {
    }

    @Override public void startInserting() {
    }

    @Override public void stopInserting() {
    }

    @Override public void insertScript(String script) {
    }

    @Override public void addKeyBinding(String keyBinding, AbstractSimpleAction action) {
    }

    @Override public void highlightLine(int line) {
    }

    @Override public boolean isEditable() {
        return false;
    }

    @Override public int getSelectionStart() {
        return 0;
    }

    @Override public int getSelectionEnd() {
        return 0;
    }

    @Override public void setDirty(boolean b) {
    }

    @Override public boolean isDirty() {
        return false;
    }

    @Override public void addCaretListener(CaretListener listener) {
    }

    @Override public void refresh() {
    }

    @Override public void addContentChangeListener(IContentChangeListener l) {
    }

    @Override public int getCaretLine() {
        return 0;
    }

    @Override public void setCaretLine(int line) {
    }

    @Override public Node getNode() {
        return null;
    }

    @Override public void addGutterListener(IGutterListener provider) {
    }

    @Override public Object getData(String key) {
        return null;
    }

    @Override public void setData(String key, Object value) {
    }

    @Override public void setCaretPosition(int position) {
    }

    @Override public int getCaretPosition() {
        return 0;
    }

    @Override public String getText() {
        return null;
    }

    @Override public void setText(String text) {
    }

    @Override public void setMode(String mode) {
    }

    @Override public int getLineOfOffset(int selectionStart) {
        return 0;
    }

    @Override public int getLineStartOffset(int startLine) {
        return 0;
    }

    @Override public int getLineEndOffset(int endLine) {
        return 0;
    }

    @Override public void setFocus() {
    }

    @Override public void setMenuItems(MenuItem[] menuItems) {
    }

    @Override public void toggleInsertMode() {
    }

    @Override public void setEditable(boolean b) {
    }

    @Override public void runWhenReady(Runnable r) {
        r.run();
    }

    @Override public void runWhenContentLoaded(Runnable r) {
        r.run();
    }

    @Override public Font getFont() {
        return null;
    }

    @Override public void refreshResource() {
    }

    @Override public String getResourcePath() {
        return null;
    }

    @Override public boolean isProjectFile() {
        return false;
    }

    @Override public boolean isTestFile() {
        return false;
    }

    @Override public File saveAs() throws IOException {
        throw new RuntimeException("SaveAs not supported by editor");
    }

    @Override public boolean isModuleFile() {
        return false;
    }

    @Override public void saveTo(File file) throws IOException {
        throw new RuntimeException("SaveAs not supported by editor");
    }

    @Override public void createNewResource(String script, File directory) {
        throw new RuntimeException("createNewResource not supported by editor");
    }

    @Override public boolean isEditingResource(File file) {
        return false;
    }

    @Override public boolean isFileBased() {
        return false;
    }

    @Override public boolean canSaveAs() {
        return false;
    }

    @Override public void changeResource(File file) {
    }

    @Override public boolean isNewFile() {
        return false;
    }
}
