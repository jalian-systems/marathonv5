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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import net.sourceforge.marathon.checklist.CheckList.CheckListItem;
import net.sourceforge.marathon.checklist.CheckList.CommentBox;
import net.sourceforge.marathon.checklist.CheckList.FailureNote;
import net.sourceforge.marathon.checklist.CheckList.Header;
import net.sourceforge.marathon.editor.IContentChangeListener;

public class CheckListFormNode extends VBox {

    public static final Logger LOGGER = Logger.getLogger(CheckListFormNode.class.getName());

    private CheckList checkList;
    private final Mode mode;
    private boolean dirty;
    ArrayList<IContentChangeListener> contentChangeListeners = new ArrayList<IContentChangeListener>();

    public static enum Mode {
        DISPLAY(false, false), EDIT(true, false), ENTER(false, true);
        private boolean selectable;
        private boolean editable;

        Mode(boolean selectable, boolean editable) {
            this.setSelectable(selectable);
            this.setEditable(editable);
        }

        public void setSelectable(boolean selectable) {
            this.selectable = selectable;
        }

        public boolean isSelectable() {
            return selectable;
        }

        public void setEditable(boolean editable) {
            this.editable = editable;
        }

        public boolean isEditable() {
            return editable;
        }
    }

    public CheckListFormNode(CheckList checklist, Mode mode) {
        super(5);
        this.mode = mode;
        this.checkList = checklist;
        buildVBox();
    }

    private void buildVBox() {
        ObservableList<Node> children = getChildren();
        if (mode.isSelectable()) {
            children.addAll(createNameField(), createDescriptionField());
        } else {
            if (checkList.getName().equals("")) {
                children.add(addSeparator("<No Name>"));
            } else {
                children.add(addSeparator(checkList.getName()));
            }
            GridPane gridPane = new GridPane();
            String text = checkList.getDescription();
            if (text.equals("")) {
                text = "<No Description>";
            }
            StringTokenizer tok = new StringTokenizer(text, "\n");
            int rowIndex = 0;
            while (tok.hasMoreTokens()) {
                Label label = new Label(tok.nextToken());
                gridPane.add(label, 0, rowIndex++);
            }
            children.add(gridPane);
        }
        Iterator<CheckList.CheckListItem> items = checkList.getItems();
        List<CheckListItemVBoxer> vboxers = new ArrayList<CheckListItemVBoxer>();
        while(items.hasNext()) {
            vboxers.add(getVBoxer(items.next()));
        }
        vboxers.forEach(vboxer -> {
            VBox vbox = vboxer.getVbox(mode.isSelectable(), mode.isEditable());
            HBox.setHgrow(vbox, Priority.ALWAYS);
            children.add(vbox);
            if (mode.isSelectable()) {
                VBox.setMargin(vbox, new Insets(5, 10, 0, 5));
            }
        });
    }

    private CheckListItemVBoxer getVBoxer(CheckListItem item) {
        if(item instanceof CommentBox)
            return new CommentBoxVBoxer((CommentBox) item);
        else if(item instanceof FailureNote)
            return new FailureNoteVBoxer((FailureNote) item);
        else if(item instanceof Header)
            return new HeaderVBoxer((Header) item);
        throw new RuntimeException("Unknown CheckListItem type: " + item.getClass().getName());
    }

    public void addContentChangeListener(IContentChangeListener l) {
        contentChangeListeners.add(l);
    }

    private Node addSeparator(String name) {
        Separator separator = new Separator();
        separator.setPadding(new Insets(8, 0, 0, 0));
        HBox.setHgrow(separator, Priority.ALWAYS);
        Text text = new Text(name);
        text.setTextAlignment(TextAlignment.CENTER);
        HBox hBox = new HBox(text, separator);
        HBox.setHgrow(hBox, Priority.ALWAYS);
        return hBox;
    }

    private VBox createNameField() {
        VBox nameFieldBox = new VBox();
        TextField nameField = new TextField();
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            fireContentChanged();
            checkList.setName(nameField.getText());
        });
        nameField.setEditable(mode.isSelectable());
        nameFieldBox.getChildren().addAll(new Label("Name"), nameField);
        HBox.setHgrow(nameField, Priority.ALWAYS);
        VBox.setMargin(nameFieldBox, new Insets(5, 10, 0, 5));
        nameField.setText(checkList.getName());
        HBox.setHgrow(nameFieldBox, Priority.ALWAYS);
        return nameFieldBox;
    }

    private VBox createDescriptionField() {
        VBox descriptionFieldBox = new VBox();
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(4);
        descriptionArea.textProperty().addListener((observable, oldValue, newValue) -> {
            fireContentChanged();
            checkList.setDescription(descriptionArea.getText());
        });
        descriptionArea.setEditable(mode.isSelectable());
        descriptionFieldBox.getChildren().addAll(new Label("Description"), descriptionArea);
        HBox.setHgrow(descriptionArea, Priority.ALWAYS);
        VBox.setMargin(descriptionFieldBox, new Insets(5, 10, 5, 5));
        descriptionArea.setText(checkList.getDescription());
        HBox.setHgrow(descriptionArea, Priority.ALWAYS);
        HBox.setHgrow(descriptionFieldBox, Priority.ALWAYS);
        return descriptionFieldBox;
    }

    public CheckList getCheckList() {
        return checkList;
    }

    public boolean isSelectable() {
        return mode.isSelectable();
    }

    public boolean isDirty() {
        return dirty;
    }

    private void fireContentChanged() {
        dirty = true;
        for (IContentChangeListener l : contentChangeListeners) {
            l.contentChanged();
        }
    }

    public void addHeader(String label) {
        Header header = checkList.createHeader(label);
        VBox headerBox = getVBoxer(header).getVbox(mode.isSelectable(), mode.isEditable());
        HBox.setHgrow(headerBox, Priority.ALWAYS);
        VBox.setMargin(headerBox, new Insets(5, 10, 0, 5));
        getChildren().add(headerBox);
    }

    public void addCheckListItem(String label) {
        FailureNote failureNote = checkList.createFailureNote(label);
        VBox failureNoteBox = getVBoxer(failureNote).getVbox(mode.isSelectable(), mode.isEditable());
        HBox.setHgrow(failureNoteBox, Priority.ALWAYS);
        VBox.setMargin(failureNoteBox, new Insets(5, 10, 3, 5));
        getChildren().add(failureNoteBox);
    }

    public void addTextArea(String label) {
        CommentBox commentBox = checkList.createCommentBox(label);
        VBox vBox = getVBoxer(commentBox).getVbox(mode.isSelectable(), mode.isEditable());
        HBox.setHgrow(vBox, Priority.ALWAYS);
        VBox.setMargin(vBox, new Insets(5, 10, 3, 5));
        getChildren().add(vBox);
    }

    public void moveUpSelected() {
        if (CheckListItemVBoxer.selectedItem != null) {
            checkList.moveUp(CheckListItemVBoxer.selectedItem.getItem());
        }
        rebuildBox();
    }

    public void moveDownSelected() {
        if (CheckListItemVBoxer.selectedItem != null) {
            checkList.moveDown(CheckListItemVBoxer.selectedItem.getItem());
        }
        rebuildBox();
    }

    public void deleteSelected() {
        if (CheckListItemVBoxer.selectedItem != null) {
            checkList.delete(CheckListItemVBoxer.selectedItem.getItem());
            CheckListItemVBoxer.selectedItem.deselect();
            CheckListItemVBoxer.selectedItem = null;
        }
        rebuildBox();
    }

    private void rebuildBox() {
        getChildren().clear();
        buildVBox();
    }

    public boolean isEditable() {
        return mode.isEditable();
    }
}
