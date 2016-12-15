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
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.screencapture.ImagePanel;
import net.sourceforge.marathon.screencapture.ImagePanel.Annotation;

public class CheckList {

    public static abstract class CheckListItem {
        private transient VBox vbox;
        private String label;
        private static CheckListItem selectedItem;

        public CheckListItem(String label) {
            setLabel(label);
        }

        public CheckListItem() {
            this(null);
        }

        public void setLabel(String label) {
            this.label = label;
        }

        protected abstract VBox createVBox(boolean selectable, boolean editable);

        public VBox getVbox(boolean selectable, boolean editable) {
            if (vbox == null) {
                vbox = createVBox(selectable, editable);
            }
            if (selectable) {
                setMouseListener(vbox);
            }
            return vbox;
        }

        protected void setMouseListener(Node node) {
            node.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
                e.consume();
                if (selectedItem != null) {
                    selectedItem.deselect();
                }
                selectedItem = CheckListItem.this;
                CheckListItem.this.select();
            });
        }

        public void select() {
            vbox.setStyle("-fx-border-color: blue;" + "-fx-border-width: 3");
        }

        public void deselect() {
            vbox.setStyle("-fx-border-color: white;");
        }

        public String getLabel() {
            return label;
        }

        public abstract String getType();

        public int getSelected() {
            return 0;
        }

        public String getText() {
            return null;
        }

        public void setSelected(int selected) {
        }

        public void setText(String text) {
        }
    }

    public static class Header extends CheckListItem {
        private static final String TYPE = "header";

        public Header(String label) {
            super(label);
        }

        public Header() {
        }

        @Override protected VBox createVBox(boolean selectable, boolean editable) {
            VBox headerBox = new VBox();
            HBox hBox = new HBox();
            Separator separator = new Separator();
            HBox.setHgrow(separator, Priority.ALWAYS);
            separator.setStyle("-fx-padding: 8 0 0 3;");
            hBox.getChildren().addAll(new Label(getLabel()), separator);
            headerBox.getChildren().add(hBox);
            return headerBox;
        }

        @Override public String getType() {
            return TYPE;
        }

        @Override public String toString() {
            return "<header label = \"" + getLabel() + "\" />";
        }
    }

    public static class FailureNote extends CheckListItem {
        private static final String TYPE = "checklist";
        private RadioButton success;
        private RadioButton fail;
        private RadioButton notes;
        private TextArea textArea = new TextArea();
        private Label label;
        private String text = "";
        private int selected = 0;

        public FailureNote(String label) {
            super(label);
        }

        public FailureNote() {
        }

        @Override protected VBox createVBox(boolean selectable, boolean editable) {
            VBox checkListBox = new VBox();
            checkListBox.getChildren().addAll(createButtonBar(selectable, editable), createTextArea(selectable, editable));
            if (selectable) {
                setMouseListener(checkListBox);
                setMouseListener(success);
                setMouseListener(fail);
                setMouseListener(notes);
                setMouseListener(textArea);
                setMouseListener(label);
            }
            return checkListBox;
        }

        private ToolBar createButtonBar(boolean selectable, boolean editable) {
            ToolBar toolBar = new ToolBar();
            label = new Label(getLabel());
            ToggleGroup toggleGroup = new ToggleGroup();
            success = new RadioButton("Success");
            fail = new RadioButton("Fail");
            notes = new RadioButton("Notes");
            success.setDisable(!editable);
            fail.setDisable(!editable);
            notes.setDisable(!editable);
            success.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (success.isSelected()) {
                    selected = 1;
                }
                textArea.setDisable(success.isSelected());
            });
            fail.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (fail.isSelected()) {
                    selected = 3;
                }
                textArea.setDisable(success.isSelected());
            });
            notes.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (notes.isSelected()) {
                    selected = 2;
                }
                textArea.setDisable(success.isSelected());
            });
            if (selected == 1) {
                success.setSelected(true);
            } else if (selected == 3) {
                fail.setSelected(true);
            } else if (selected == 2) {
                notes.setSelected(true);
            } else {
                success.setSelected(editable);
            }
            toggleGroup.getToggles().addAll(success, fail, notes);
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);
            toolBar.getItems().addAll(label, region, success, fail, notes);
            return toolBar;
        }

        private Node createTextArea(boolean selectable, boolean editable) {
            textArea.setPrefRowCount(4);
            textArea.setEditable(editable);
            textArea.textProperty().addListener((observable, oldValue, newValue) -> {
                text = textArea.getText();
            });
            textArea.setText(text);
            ScrollPane scrollPane = new ScrollPane(textArea);
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
            HBox.setHgrow(scrollPane, Priority.ALWAYS);
            return scrollPane;
        }

        @Override public String getType() {
            return TYPE;
        }

        @Override public String getText() {
            return text;
        }

        @Override public void setSelected(int selected) {
            this.selected = selected;
        }

        @Override public int getSelected() {
            return selected;
        }

        @Override public void setText(String text) {
            this.text = text;
        }

        @Override public String toString() {
            return "<failureNote label = \"" + getLabel() + "\" />";
        }
    }

    public static class CommentBox extends CheckListItem {
        private static final String TYPE = "comments";
        private TextArea textArea;
        private String text = "";

        public CommentBox(String label) {
            super(label);
        }

        public CommentBox() {
        }

        @Override protected VBox createVBox(boolean selectable, boolean editable) {
            VBox textAreaBox = new VBox();
            Label label = new Label(getLabel());
            textAreaBox.getChildren().addAll(label, createTextArea(selectable, editable));
            if (selectable) {
                setMouseListener(label);
                setMouseListener(textArea);
            }
            HBox.setHgrow(textAreaBox, Priority.ALWAYS);
            return textAreaBox;
        }

        private Node createTextArea(boolean selectable, boolean editable) {
            textArea = new TextArea();
            textArea.setPrefRowCount(4);
            textArea.setEditable(editable);
            textArea.textProperty().addListener((observable, oldValue, newValue) -> {
                text = textArea.getText();
            });
            textArea.setText(text);
            ScrollPane scrollPane = new ScrollPane(textArea);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
            HBox.setHgrow(scrollPane, Priority.ALWAYS);
            return scrollPane;
        }

        @Override public String getType() {
            return TYPE;
        }

        @Override public String getText() {
            return text;
        }

        @Override public void setText(String text) {
            this.text = text;
        }

        @Override public String toString() {
            return "<commentBox label = \"" + getLabel() + "\" />";
        }
    }

    private ArrayList<CheckList.CheckListItem> checkListItems;
    private String name;
    private String description;
    private String captureFile;
    private File dataFile;

    public CheckList() {
        checkListItems = new ArrayList<CheckList.CheckListItem>();
    }

    public Iterator<CheckListItem> getItems() {
        return checkListItems.iterator();
    }

    private void add(CheckListItem item) {
        checkListItems.add(item);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setCheckListItems(ArrayList<CheckList.CheckListItem> checkListItems) {
        this.checkListItems = checkListItems;
    }

    public ArrayList<CheckList.CheckListItem> getCheckListItems() {
        return checkListItems;
    }

    public Header createHeader(String label) {
        Header header = new Header(label);
        add(header);
        return header;
    }

    public FailureNote createFailureNote(String label) {
        FailureNote failureNote = new FailureNote(label);
        add(failureNote);
        return failureNote;
    }

    public CommentBox createCommentBox(String label) {
        CommentBox commentBox = new CommentBox(label);
        add(commentBox);
        return commentBox;
    }

    public void moveUpSelected() {
        if (CheckListItem.selectedItem != null) {
            int index = checkListItems.indexOf(CheckListItem.selectedItem);
            if (index == -1 || index == 0) {
                return;
            }
            checkListItems.remove(index);
            checkListItems.add(index - 1, CheckListItem.selectedItem);
        }
    }

    public void moveDownSelected() {
        if (CheckListItem.selectedItem != null) {
            int index = checkListItems.indexOf(CheckListItem.selectedItem);
            if (index == -1 || index == checkListItems.size() - 1) {
                return;
            }
            checkListItems.remove(index);
            checkListItems.add(index + 1, CheckListItem.selectedItem);
        }
    }

    public void deleteSelected() {
        if (CheckListItem.selectedItem != null) {
            CheckListItem.selectedItem.deselect();
            checkListItems.remove(CheckListItem.selectedItem);
            CheckListItem.selectedItem = null;
        }
    }

    public static CheckList read(File file) throws Exception {
        XMLDecoder decoder = new XMLDecoder(new FileInputStream(file));
        try {
            return (CheckList) decoder.readObject();
        } finally {
            decoder.close();
        }
    }

    public void save(OutputStream out) {
        XMLEncoder encoder1;
        encoder1 = new XMLEncoder(out);
        encoder1.writeObject(this);
        encoder1.close();
    }

    public void setCaptureFile(String file) {
        this.captureFile = file;
    }

    public void xsetDataFile(File file) {
        this.dataFile = file;
    }

    public File xgetDataFile() {
        return dataFile;
    }

    public String getStatus() {
        int status = 0;
        for (CheckListItem item : checkListItems) {
            if (status < item.getSelected()) {
                status = item.getSelected();
            }
        }
        if (status == 3) {
            return "Fail";
        } else if (status == 2) {
            return "Notes";
        }
        return "OK";
    }

    public void saveXML(String indent, ByteArrayOutputStream baos, int index) {
        PrintWriter printWriter = new PrintWriter(baos);
        indent += "  ";
        printWriter.print(indent + "<checklist ");
        printWriter.print("name=\"" + quoteCharacters(getName()) + "\" ");
        printWriter.print("index=\"" + index + "\" ");
        printWriter.print("description=\"" + quoteCharacters(getDescription()) + "\" ");
        if (captureFile != null) {
            printWriter.print("capture=\"" + captureFile + "\" ");
        }
        printWriter.print("status=\"" + getStatus() + "\" ");
        printWriter.println(">");

        for (CheckListItem item : checkListItems) {
            printWriter.print("<checkitem type=\"" + item.getType() + "\" ");
            printWriter.print("label=\"" + quoteCharacters(item.getLabel()) + "\" ");
            int selected = item.getSelected();
            if (selected != 0) {
                printWriter.print("selected=\"" + selected + "\" ");
            }
            String text = item.getText();
            if (text == null) {
                printWriter.println("/>");
            } else {
                printWriter.println(" text=\"");
                printWriter.print(quoteCharacters(text));
                printWriter.println("\" />");
            }
        }
        // TODO: Annotate screen capture.
        if (captureFile != null) {
            File file = new File(System.getProperty(Constants.PROP_IMAGE_CAPTURE_DIR), captureFile);
            try {
                ImagePanel imagePanel = new ImagePanel(file, false);
                List<Annotation> annotations = imagePanel.getAnnotations();
                printWriter.println(indent + "  " + "<annotations>");
                for (Annotation a : annotations) {
                    printWriter.println(indent + "    " + "<annotation x=\"" + a.getX() + "\" y=\"" + a.getY() + "\" w=\""
                            + a.getWidth() + "\" h=\"" + a.getHeight() + "\" text=\"" + quoteCharacters(a.getText()) + "\"/>");
                }
                printWriter.println(indent + "  " + "</annotations>");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        printWriter.println(indent + "</checklist>");
        printWriter.close();
    }

    private static String quoteCharacters(String s) {
        StringBuffer result = null;
        for (int i = 0, max = s.length(), delta = 0; i < max; i++) {
            char c = s.charAt(i);
            String replacement = null;

            if (c == '&') {
                replacement = "&amp;";
            } else if (c == '<') {
                replacement = "&lt;";
            } else if (c == '\r') {
                replacement = "&#13;";
            } else if (c == '>') {
                replacement = "&gt;";
            } else if (c == '"') {
                replacement = "&quot;";
            } else if (c == '\'') {
                replacement = "&apos;";
            }

            if (replacement != null) {
                if (result == null) {
                    result = new StringBuffer(s);
                }
                result.replace(i + delta, i + delta + 1, replacement);
                delta += replacement.length() - 1;
            }
        }
        if (result == null) {
            return s;
        }
        return result.toString();
    }

    public String getCaptureFile() {
        return captureFile;
    }

    public void saveHTML(OutputStream baos) {
        PrintWriter printWriter = new PrintWriter(baos);
        printWriter.println("<html><head><title>Checklist: " + quoteCharacters(getName()) + "</title></head><body>");
        printWriter.println("<h1>Checklist: " + quoteCharacters(getName()) + "(" + getStatus() + ")</h1>");
        printWriter.println("<h2>" + quoteCharacters(getDescription()) + "</h2>");
        for (CheckListItem item : checkListItems) {
            String status = getStatus(item.getSelected());
            printWriter.println("<h3>" + quoteCharacters(item.getLabel()) + status + "<h3><hr/>");
            String text = item.getText();
            if (text != null) {
                printWriter.println("<p>" + quoteCharacters(text) + "</p>");
            }
        }
        printWriter.println("</body></html>");

        printWriter.close();
    }

    private String getStatus(int selected) {
        if (selected == 0) {
            return "";
        }
        if (selected == 1) {
            return "(OK)";
        }
        if (selected == 2) {
            return "(Notes)";
        } else {
            return "(Failure)";
        }
    }
}
