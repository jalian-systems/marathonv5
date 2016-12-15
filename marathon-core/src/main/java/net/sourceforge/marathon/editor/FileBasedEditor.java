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

import java.io.File;
import java.io.IOException;

import net.sourceforge.marathon.display.MarathonFileFilter;
import net.sourceforge.marathon.editor.IEditorProvider.EditorType;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.util.FileHandler;
import net.sourceforge.marathon.util.INameValidateChecker;
import net.sourceforge.marathon.util.IResourceHandler;

public abstract class FileBasedEditor extends AbstractEditor {

    protected FileHandler fileHandler;

    /**
     * Title for the new files created.
     */
    public static final String NEW_FILE = "Untitled";

    private static class NewFileNameGenerator {
        public int newFileCount = 0;

        public String getNewFileName() {
            if (newFileCount == 0) {
                newFileCount++;
                return NEW_FILE;
            }
            return NEW_FILE + newFileCount++;
        }

    }

    private static NewFileNameGenerator newFileNameGenerator = new NewFileNameGenerator();

    @Override public IResourceHandler createResourceHandler(EditorType type, INameValidateChecker nameChecker) throws IOException {
        if (type == EditorType.CHECKLIST) {
            MarathonFileFilter marathonFileFilter = new MarathonFileFilter() {
                @Override public String getSuffix() {
                    return ".xml";
                }
            };
            fileHandler = new FileHandler(marathonFileFilter, nameChecker);
            return fileHandler;
        }
        fileHandler = new FileHandler(new MarathonFileFilter(), nameChecker);
        return fileHandler;
    }

    @Override public void refreshResource() {
        File currentFile = fileHandler.getCurrentFile();
        try {
            String script = fileHandler.readFile(currentFile);
            setText(script);
            setDirty(false);
        } catch (IOException e1) {
        }
    }

    @Override public String getDockKey() {
        File currentFile = fileHandler.getCurrentFile();
        if (currentFile != null) {
            return currentFile.getAbsolutePath();
        }
        return (String) getData("filename");
    }

    @Override public String getName() {
        File currentFile = fileHandler.getCurrentFile();
        if (currentFile != null) {
            return currentFile.getName();
        }
        return (String) getData("filename");
    }

    @Override public String getResourcePath() {
        if (fileHandler.getCurrentFile() == null) {
            return (String) getData("filename");
        }
        try {
            return fileHandler.getCurrentFile().getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override public String getDisplayName() {
        File currentFile = fileHandler.getCurrentFile();
        if (currentFile == null) {
            return (String) getData("displayname");
        }
        try {
            String projectPath = Constants.getMarathonProjectDirectory().getCanonicalPath();
            String filePath = currentFile.getCanonicalPath();
            if (filePath.startsWith(projectPath)) {
                filePath = filePath.substring(projectPath.length() + 1);
            }
            return filePath;
        } catch (IOException e) {
        }
        return currentFile.getName();
    }

    @Override public void readResource(File to) throws IOException {
        String script = fileHandler.readFile(to);
        setText(script);
        setDirty(false);
        setMode(fileHandler.getMode(getName()));
    }

    @Override public boolean isProjectFile() {
        return fileHandler.isProjectFile();
    }

    @Override public boolean isTestFile() {
        return fileHandler.isTestFile();
    }

    @Override public File saveAs() throws IOException {
        return fileHandler.saveAs(getText(), getNode().getScene().getWindow(), getName());
    }

    @Override public File save() throws IOException {
        return fileHandler.save(getText(), getNode().getScene().getWindow(), getName());
    }

    @Override public boolean isModuleFile() {
        return fileHandler.isModuleFile();
    }

    @Override public void saveTo(File file) throws IOException {
        fileHandler.saveTo(file, getText());
    }

    @Override public void createNewResource(String script, File directory) {
        fileHandler.setCurrentDirectory(directory);
        fileHandler.clearCurrentFile();
        String newFileName = newFileNameGenerator.getNewFileName();
        setText(script);
        setMode(fileHandler.getMode(newFileName));
        setData("filename", newFileName);
        setData("displayname", newFileName);
        setDirty(false);
        setFocus();
    }

    @Override public boolean isEditingResource(File file) {
        return fileHandler.getCurrentFile() != null && file.equals(fileHandler.getCurrentFile());
    }

    @Override public boolean isFileBased() {
        return true;
    }

    @Override public void changeResource(File file) {
        setData("filename", file.getName());
        setData("displayname", file.getName());
        fileHandler.setCurrentFile(file);
    }

    @Override public boolean isNewFile() {
        return fileHandler.isNewFile();
    }
}
