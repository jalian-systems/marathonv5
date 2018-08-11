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
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

public class CheckList {

    public static final Logger LOGGER = Logger.getLogger(CheckList.class.getName());

    public static abstract class CheckListItem {
        private String label;

        public CheckListItem(String label) {
            setLabel(label);
        }

        public CheckListItem() {
            this(null);
        }

        public void setLabel(String label) {
            this.label = label;
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

        @Override
        public String getType() {
            return TYPE;
        }

        @Override
        public String toString() {
            return "<header label = \"" + getLabel() + "\" />";
        }
    }

    public static class FailureNote extends CheckListItem {
        private static final String TYPE = "checklist";
        private String text = "";
        private int selected = 0;

        public FailureNote(String label) {
            super(label);
        }

        public FailureNote() {
        }

        @Override
        public String getType() {
            return TYPE;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void setSelected(int selected) {
            this.selected = selected;
        }

        @Override
        public int getSelected() {
            return selected;
        }

        @Override
        public void setText(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "<failureNote label = \"" + getLabel() + "\" />";
        }
    }

    public static class CommentBox extends CheckListItem {
        private static final String TYPE = "comments";
        private String text = "";

        public CommentBox(String label) {
            super(label);
        }

        public CommentBox() {
        }

        @Override
        public String getType() {
            return TYPE;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void setText(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "<commentBox label = \"" + getLabel() + "\" />";
        }
    }

    private ArrayList<CheckList.CheckListItem> checkListItems;
    private String name;
    private String description;
    private String captureFile;
    private File dataFile;
    private File extCaptureFile;

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

    public void moveUp(CheckListItem item) {
        int index = checkListItems.indexOf(item);
        if (index == -1 || index == 0) {
            return;
        }
        checkListItems.remove(index);
        checkListItems.add(index - 1, item);
    }

    public void moveDown(CheckListItem item) {
        int index = checkListItems.indexOf(item);
        if (index == -1 || index == checkListItems.size() - 1) {
            return;
        }
        checkListItems.remove(index);
        checkListItems.add(index + 1, item);
    }

    public void delete(CheckListItem item) {
        checkListItems.remove(item);
    }

    public File getExtCaptureFile() {
        return extCaptureFile;
    }

    public void setExtCaptureFile(File extCaptureFile) {
        this.extCaptureFile = extCaptureFile;
    }
}
