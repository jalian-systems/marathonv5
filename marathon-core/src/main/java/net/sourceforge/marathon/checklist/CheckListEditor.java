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
package net.sourceforge.marathon.checklist;

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.control.Button;
import net.sourceforge.marathon.checklist.CheckListFormNode.Mode;
import net.sourceforge.marathon.editor.FileBasedEditor;
import net.sourceforge.marathon.editor.IContentChangeListener;
import net.sourceforge.marathon.editor.IStatusBar;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fxdocking.ToolBarContainer;
import net.sourceforge.marathon.fxdocking.ToolBarContainer.Orientation;
import net.sourceforge.marathon.fxdocking.ToolBarPanel;
import net.sourceforge.marathon.fxdocking.VLToolBar;

public class CheckListEditor extends FileBasedEditor implements IContentChangeListener {

    public static final Logger LOGGER = Logger.getLogger(CheckListEditor.class.getName());

    private HashMap<String, Object> dataMap = new HashMap<String, Object>();
    private ToolBarContainer container = ToolBarContainer.createDefaultContainer(Orientation.RIGHT);
    private CheckListView checkListView;
    private CheckList checklist;
    @SuppressWarnings("unused")
    private IStatusBar statusBar;
    private boolean dirty;

    public CheckListEditor() {
        checkListView = new CheckListView(true);
        checkListView.addContentChangeListener(this);
        initToolBar();
        container.setContent(checkListView);
    }

    private void initToolBar() {
        ToolBarPanel toolBarPanel = container.getToolBarPanel();
        Button headerButton = FXUIUtils.createButton("header", "Create Header", true, "Header");
        headerButton.setOnAction((e) -> checkListView.onHeader());

        Button checklistButton = FXUIUtils.createButton("checklist", "Create checklist", true, "Checklist");
        checklistButton.setOnAction((e) -> checkListView.onChecklist());

        Button textAreaButton = FXUIUtils.createButton("textbox", "Create Textbox", true, "Textbox");
        textAreaButton.setOnAction((e) -> checkListView.onTextArea());

        VLToolBar toolBar = new VLToolBar();
        toolBar.add(headerButton);
        toolBar.add(checklistButton);
        toolBar.add(textAreaButton);
        toolBarPanel.add(toolBar);
    }

    @Override
    public void setStatusBar(IStatusBar statusBar) {
        this.statusBar = statusBar;
    }

    @Override
    public void addContentChangeListener(IContentChangeListener l) {
        checkListView.addContentChangeListener(l);
    }

    @Override
    public Node getNode() {
        return container;
    }

    @Override
    public Object getData(String key) {
        return dataMap.get(key);
    }

    @Override
    public void setData(String key, Object value) {
        dataMap.put(key, value);
    }

    @Override
    public void setText(String code) {
        ByteArrayInputStream stream = new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8));
        XMLDecoder decoder = new XMLDecoder(stream);
        checklist = (CheckList) decoder.readObject();
        decoder.close();
        checkListView.setCheckListNode(new CheckListFormNode(checklist, Mode.EDIT));
    }

    @Override
    public String getText() {
        String string = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        checklist.save(out);
        string = out.toString();
        return string;
    }

    @Override
    public void setDirty(boolean b) {
        dirty = b;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void contentChanged() {
        setDirty(true);
    }

    @Override
    public void createNewResource(String script, File directory) {
        super.createNewResource(script, directory);
        setDirty(true);
    }

    @Override
    public boolean canSaveAs() {
        return true;
    }
}
