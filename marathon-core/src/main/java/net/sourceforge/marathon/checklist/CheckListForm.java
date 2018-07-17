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

import java.io.File;
import java.io.FilenameFilter;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CheckListForm {

    public static final Logger LOGGER = Logger.getLogger(CheckListForm.class.getName());

    private boolean insert;
    private File checklistDir;
    private ObservableList<CheckListElement> checklistElements = FXCollections.observableArrayList();

    public static class CheckListElement {

        private File checkList;

        public CheckListElement(File checkList) {
            this.checkList = checkList;
        }

        @Override
        public String toString() {
            String name = checkList.getName();
            name = name.substring(0, name.length() - 4);
            return name;
        }

        public File getFile() {
            return checkList;
        }
    }

    public CheckListForm(File checkListDir, boolean insert) {
        this.checklistDir = checkListDir;
        this.insert = insert;
        createCheckListElements();
    }

    private void createCheckListElements() {
        File[] items = null;
        if (checklistDir != null) {
            items = checklistDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (dir.equals(checklistDir) && name.endsWith(".xml")) {
                        return true;
                    }
                    return false;
                }
            });
        }
        if (items != null) {
            for (File item : items) {
                checklistElements.add(new CheckListElement(item));
            }
        }
    }

    public String getTitle() {
        return insert ? "Select a Checklist" : "Manage Checklists";
    }

    public File getChecklistDir() {
        return checklistDir;
    }

    public ObservableList<CheckListElement> getCheckListElements() {
        return checklistElements;
    }

    public boolean isInsert() {
        return insert;
    }

    public int getIndexOf(CheckListElement element) {
        for (CheckListElement checkListElement : checklistElements) {
            if (checkListElement.getFile().getAbsolutePath().equals(element.getFile().getAbsolutePath())) {
                return checklistElements.indexOf(checkListElement);
            }
        }
        return -1;
    }

    public boolean exits(CheckListElement element) {
        for (CheckListElement checkListElement : checklistElements) {
            if (checkListElement.getFile().getAbsolutePath().equals(element.getFile().getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }
}
