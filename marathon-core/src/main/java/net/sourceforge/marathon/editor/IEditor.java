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
import java.util.EventListener;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import net.sourceforge.marathon.editor.IEditorProvider.EditorType;
import net.sourceforge.marathon.util.AbstractSimpleAction;
import net.sourceforge.marathon.util.INameValidateChecker;
import net.sourceforge.marathon.util.IResourceHandler;

public interface IEditor {

    public interface CaretListener extends EventListener {

        void caretUpdate();

    }

    public interface IGutterListener extends EventListener {
        public boolean hasBreakpointAtLine(int line);

        public void gutterDoubleClickedAt(int line);
    }

    public static final int FIND_NEXT = 1;
    public static final int FIND_PREV = 2;

    public static final int FIND_FAILED = 1;
    public static final int FIND_WRAPPED = 2;
    public static final int FIND_SUCCESS = 3;

    public void setStatusBar(IStatusBar statusBar);

    public void startInserting();

    public void stopInserting();

    public void insertScript(String script);

    public void addKeyBinding(String keyBinding, AbstractSimpleAction action);

    public void highlightLine(int line);

    public boolean isEditable();

    public int getSelectionStart();

    public int getSelectionEnd();

    public void setDirty(boolean b);

    public boolean isDirty();

    public void addCaretListener(CaretListener listener);

    public void refresh();

    public void addContentChangeListener(IContentChangeListener l);

    public int getCaretLine();

    public void setCaretLine(int line);

    public Node getNode();

    public void addGutterListener(IGutterListener provider);

    public Object getData(String key);

    public void setData(String key, Object value);

    public void setCaretPosition(int position);

    public int getCaretPosition();

    public String getText();

    public void setText(String code);

    public void setMode(String mode);

    public int getLineOfOffset(int selectionStart);

    public int getLineStartOffset(int startLine);

    public int getLineEndOffset(int endLine);

    public void setFocus();

    public void setMenuItems(MenuItem[] menuItems);

    public void toggleInsertMode();

    public void setEditable(boolean b);

    public void runWhenReady(Runnable r);

    public Font getFont();

    public IResourceHandler createResourceHandler(EditorType type, INameValidateChecker nameChecker) throws IOException;

    public void refreshResource();

    public String getDockKey();

    public String getName();

    public String getResourcePath();

    public String getDisplayName();

    public void readResource(File to) throws IOException;

    public boolean isProjectFile();

    public boolean isTestFile();

    public File saveAs() throws IOException;

    public File save() throws IOException;

    public boolean isModuleFile();

    public void saveTo(File file) throws IOException;

    public void createNewResource(String script, File directory);

    public boolean isEditingResource(File file);

    public boolean isFileBased();

    public boolean canSaveAs();

    public void changeResource(File file);

    void runWhenContentLoaded(Runnable r);

    public boolean isNewFile();
};
