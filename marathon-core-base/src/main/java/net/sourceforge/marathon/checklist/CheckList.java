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
    private String extCaptureHTML;

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

    // @formatter:off
    private static final String HTML = 
            "<!DOCTYPE xhtml PUBLIC \"-//W3C//DTD HTML 4.01//EN\">\n" +
                    "<html lang=\"en\">\n" +
                    "\n" +
                    "<head>\n" +
                    "    <style type=\"text/css\">\n" +
                    "        *,\n" +
                    "        ::after,\n" +
                    "        ::before {\n" +
                    "            box-sizing: border-box;\n" +
                    "        }\n" +
                    "\n" +
                    "        body {\n" +
                    "            padding-left: 10px;\n" +
                    "        }\n" +
                    "\n" +
                    "        table {\n" +
                    "            width: 980px;\n" +
                    "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                    "            font-size: 16px;\n" +
                    "            text-align: left;\n" +
                    "            border-collapse: collapse;\n" +
                    "        }\n" +
                    "\n" +
                    "        tr:hover td {\n" +
                    "            background: #d0dafd;\n" +
                    "            color: #339;\n" +
                    "\n" +
                    "        }\n" +
                    "\n" +
                    "        th {\n" +
                    "            /* font-size: 13px; */\n" +
                    "            font-weight: normal;\n" +
                    "            background: #b9c9fe;\n" +
                    "            border-top: 4px solid #aabcfe;\n" +
                    "            border: 1px solid #fff;\n" +
                    "            color: #039;\n" +
                    "            padding: 8px;\n" +
                    "        }\n" +
                    "\n" +
                    "        td {\n" +
                    "            background: #e8edff;\n" +
                    "            border: 1px solid #fff;\n" +
                    "            color: #669;\n" +
                    "            border-top: 1px solid transparent;\n" +
                    "            padding: 8px;\n" +
                    "            vertical-align: top;\n" +
                    "        }\n" +
                    "\n" +
                    "        #checklist_title {\n" +
                    "            text-align: left;\n" +
                    "            font-weight: bold;\n" +
                    "        }\n" +
                    "\n" +
                    "        #checklist_description {\n" +
                    "            text-align: left;\n" +
                    "            color: grey;\n" +
                    "        }\n" +
                    "\n" +
                    "        table#checklist_table {\n" +
                    "            border-collapse: collapse;\n" +
                    "        }\n" +
                    "\n" +
                    "        .h_type {\n" +
                    "            width: 5%;\n" +
                    "        }\n" +
                    "\n" +
                    "        .h_desc {\n" +
                    "            width: 40%;\n" +
                    "        }\n" +
                    "\n" +
                    "        .h_notes {\n" +
                    "            width: 45%;\n" +
                    "        }\n" +
                    "\n" +
                    "        .h_status {\n" +
                    "            width: 10%;\n" +
                    "        }\n" +
                    "\n" +
                    "        .header {\n" +
                    "            font-size: large;\n" +
                    "        }\n" +
                    "\n" +
                    "        .n_type {\n" +
                    "            background-image: url('data:image/gif;base64,R0lGODlhEAAQANUAAGh2km96jm56jitbmSlvxCltwSluwSpsvSprvClqtypquClnsipnsipkrCpjqyphpSpeoCpcmipZlCtZlCtYkStYkCtWjXd/iPv9/7vd+sbk/dDp/oG+7YfB74/G8JnL86TS9a/X99ju//j8//r9//f8//P7/+z5/+/6/9Xz/+v5/+76//j9/+n5/+z6/+v6/+/7//P8//f9//v+//r+/4KGgYKFgI6Neo2MepiTcqCZbKecaP///wAAAAAAAAAAACH5BAEAADwALAAAAAAQABAAAAZ8QJ5wSCwaj0YLRRKBPBwLxaFAIBI4Hc8HFMpoNqLq0FKZDJwNRgJhEAt3qbh8viPCW/h8vj7UpfSAOkQ5KSovJy4uKiqJOUQ3KSgwkyiVKzA4RDYpMSaenTGdNUQXKSWnqCMsMhdEAikzJBgzGLU0GAFEALu8vbtIwMFHQQA7') !important;\n" +
                    "            background-repeat: no-repeat !important;\n" +
                    "            background-position: 4px 12px !important;\n" +
                    "        }\n" +
                    "\n" +
                    "        .c_type {\n" +
                    "            background-image: url('data:image/gif;base64,R0lGODlhEAAQANUAAPHz+vj5/PX3/Ozw+YeUrYyWquft+fD0/PP2/PX3+/r7/fj5+152o22ErV9zlmBzlnGCoN/o+OLq+ICUtIGUtJutzIKRqpenwJinwI6bsK+6zO7z+666zPX3+pKbpvv8/fr7/D+m/6vB1ACK+ABVlgBGe5mfoaGknKmpltDQxLCtkbeyjbaxjbu0itCseNSwe+HLqsWaYc6pdseicsqldNOuetvEpMGbbMSeb76Ya62BUryWarWQZ+TWxtTBr////yH5BAEAAD8ALAAAAAAQABAAAAaVwJ9wKMyIRBmiUmjhcDQcy/LXi8UgmMsFA9HFesTX60GhTMoUhzjcWrjf7taLWGsvPqHPu1Ujulh3ISQfDAwLKy5EMioKgiEgHR0JKjJCIT80KY4BFYUAKDQ/jjCOAgOnpyczliUjJSEIBg2FDAYmOEOuIwcSvb4SHjdEIxsRxsfGBTlENgTOz9A7RD481dbXPlPaP0EAOw==') !important;\n" +
                    "            background-repeat: no-repeat !important;\n" +
                    "            background-position: 4px 12px !important;\n" +
                    "        }\n" +
                    "\n" +
                    "        div.spacer {\n" +
                    "            height: 10px;\n" +
                    "        }\n" +
                    "        .success {\n" +
                    "            background-image: url('data:image/gif;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAAwFBMVEX4+fzv8/vz9vz5+vzm7frt8vrl7fnp8Prr8frx9fvn7/r2+fzc6+bP49uMv6XE3tECnktNqHlbsINjuIt+uJmbx7Cpz7u318Zgrn1irn5lsYFxt4suh005klk9j1pFlWFNn2pRpG9VonBSnG1QmGpbqnhfrHtcpHZZmXBosoNttIdHnWR7dE+fmn+5taJ+dU6HekyDd02Rf0iMfUqdhUWXgkawjz6sjECnikKiiEO7qHbLvpy0kD3///////8AAADUu3FjAAAAP3RSTlP//////////////////////////////////////////////////////////////////////////////////wCOJnwXAAAAmUlEQVR4nF3NZw+CMBSF4YKgjIJtHbhRce8JONr//7O89hJifD6+yckhCrx/EB0qJQwp6VOCUh0yf0N9lOmQmx36ZVIz1+HpJvNFt7d2E/ehw8uYGIUbUQwFwA7sO4QGqLPteLp0Rs4FQyRZDV0hCCFkS0+8wDtBkFFbCjaMZytrYJ0hNKUMOauiIwQeSs7ZYb/TyltVIOrPB9fEIFKKel5fAAAAAElFTkSuQmCC') !important;\n" +
                    "            background-repeat: no-repeat !important;\n" +
                    "            background-position: 4px 12px !important;\n" +
                    "            padding-left: 24px !important;\n" +
                    "        }\n" +
                    "        .fail {\n" +
                    "            background-image: url('data:image/gif;base64,R0lGODlhEAAQAPf/AMczNfRxdPRzdPNydPNzddgqL+AsNN8sM8cpMOY2PuU2PsUgK+UwOfJVYPRja/NjavNja/Nka8UYJ8YZKMUZJ8YgLPJUYMUTJfE/UvA/UfJIWPFIWNRldN+cqMpdSc5uXspXRspYRslYRtWIfMlQQ9ymoMlHPslHP8hHP8c9OeBhW/WBfcc9OuNST/WAfvSAfuPExP///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAEAADEALAAAAAAQABAAAAiVAGMIHEiwoMGDBmF4GFFwhAcYBGGEaBGCocARIlqAgCiwBAkXK0h8KPGBxIuQJQiOQEFAgAoTKgYEOGGRIAcWESA8cPAgBQeEABQ0aGAhAQCEMRAw2KBBAwMECDlUwECV6oKfBTlMoFpAQgEMGShgFdjhAtgLHDpwMJvhQoeBMC4YQDtw7YELHAXGHStwbV6kgAMLDAgAOw==') !important;\n" +
                    "            background-repeat: no-repeat !important;\n" +
                    "            background-position: 4px 12px !important;\n" +
                    "            padding-left: 24px !important;\n" +
                    "        }\n" +
                    "        .notes {\n" +
                    "            background-image: url('data:image/gif;base64,R0lGODlhEAAQAPf/AIGUtPX3+/Hz+uft+Y6bsICUua66zPX3+oWXuYOVuYyWqreyjZKbppmfoamplqGknLCtka+6zIyewHGCoICUtI6gwIKRqpChwezw+bu0iuLq+N/o+IeUrfj5+////wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAEAAB4ALAAAAAAQABAAAAh3AD0IHEiwoMGDCAcSWEggocAJBgxEMGABYYYMAABQyAjgosEMHUKKDJnh48iRJQsuEHkBgcsOCwxCCEAzwIGbASAYdCCgZ4UEQAU4MPgAg9GjRh8YbDCgqYQCUAc0MMhAg9WrVhkYVLChq9euCgxyGEu2rEOEAQEAOw==') !important;\n" +
                    "            background-repeat: no-repeat !important;\n" +
                    "            background-position: 4px 12px !important;\n" +
                    "            padding-left: 24px !important;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "\n" +
                    "    <title>Checklist: %checklist_name%</title>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "    <h1 id=\"checklist_title\">Checklist: %checklist_name% (%checklist_status%)</h1>\n" +
                    "    <h2 id=\"checklist_description\">\n" +
                    "       %checklist_description%\n" +
                    "    </h2>\n" +
                    "\n" +
                    "    <div class=\"spacer\">&nbsp;</div>\n" +
                    "\n" +
                    "    <table id=\"checklist_table\">\n" +
                    "        <tr>\n" +
                    "            <th class=\"h_type\"></th>\n" +
                    "            <th class=\"h_desc\">Description</th>\n" +
                    "            <th class=\"h_notes\">Notes</th>\n" +
                    "            <th class=\"h_status\">Status</th>\n" +
                    "        </tr>\n" +
                    "       %itemContents%\n" +
                    "    </table>\n" +
                    "    %extCaptureHTML%\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>\n" +
                    "\n";
    // @formatter:on

    public void saveHTML(OutputStream baos) {
        PrintWriter printWriter = new PrintWriter(baos);

        StringBuffer itemContents = new StringBuffer();
        for (CheckListItem item : checkListItems) {
            itemContents.append("<tr>\n");
            if (item.getType().equals("header")) {
                itemContents.append("<th class=\"header\" colspan=\"4\">").append(quoteCharacters(item.getLabel())).append("</th>");
            } else if (item.getType().equals("checklist")) {
                itemContents.append("<td class=\"c_type\"></td>\n");
                itemContents.append("<td class=\"c_desc\">").append(quoteCharacters(item.getLabel())).append("</td>\n");
                itemContents.append("<td class=\"c_data\">").append(quoteCharacters(item.getText())).append("</td>\n");
                itemContents.append("<td class=\"c_status " + getStatusClass(item.getSelected()) + "\">")
                        .append(getStatus(item.getSelected())).append("</td>\n");
            } else {
                itemContents.append("<td class=\"n_type\"></td>\n");
                itemContents.append("<td class=\"n_desc\">").append(quoteCharacters(item.getLabel())).append("</td>\n");
                itemContents.append("<td class=\"n_data\">").append(quoteCharacters(item.getText())).append("</td>\n");
                itemContents.append("<td class=\"n_status " + getStatusClass(item.getSelected()) + "\">")
                        .append(getStatus(item.getSelected())).append("</td>\n");
            }
            itemContents.append("</tr>\n");
        }

        printWriter.println(HTML.replace("%checklist_name%", quoteCharacters(getName())).replace("%checklist_status%", getStatus())
                .replace("%checklist_description%", quoteCharacters(getDescription()))
                .replace("%itemContents%", itemContents.toString())
                .replace("%extCaptureHTML%", extCaptureHTML == null ? "" : extCaptureHTML));
        printWriter.close();
    }

    private String getStatus(int selected) {
        if (selected == 0) {
            return "";
        }
        if (selected == 1) {
            return "OK";
        }
        if (selected == 2) {
            return "Notes";
        } else {
            return "Failure";
        }
    }

    private String getStatusClass(int selected) {
        if (selected == 0) {
            return "";
        }
        if (selected == 1) {
            return "success";
        }
        if (selected == 2) {
            return "notes";
        } else {
            return "fail";
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

    public String getExtCaptureHTML() {
        return extCaptureHTML;
    }

    public void setExtCaptureHtml(String htmlDoc) {
        this.extCaptureHTML = htmlDoc;
    }

}
