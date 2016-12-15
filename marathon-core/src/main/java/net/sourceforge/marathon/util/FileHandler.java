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
package net.sourceforge.marathon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Optional;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;
import net.sourceforge.marathon.display.MarathonFileChooserInfo;
import net.sourceforge.marathon.display.MarathonFileFilter;
import net.sourceforge.marathon.editor.IMarathonFileFilter;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.runtime.api.Constants;

public class FileHandler implements IResourceHandler {
    private static final String NL = System.getProperty("line.separator");
    private File currentFile;
    private IMarathonFileFilter filter;
    private File fixtureDirectory;
    private File[] moduleDirectories;
    private File rootDirectory;
    private File testDirectory;
    private INameValidateChecker nameValidateChecker;
    private MarathonFileChooserInfo fileChooserInfo;
    private boolean isNewFile = false;

    public FileHandler(IMarathonFileFilter filter, INameValidateChecker nameValidateChecker) throws IOException {
        this.filter = filter;
        this.testDirectory = Constants.getMarathonDirectory(Constants.PROP_TEST_DIR);
        this.fixtureDirectory = Constants.getMarathonDirectory(Constants.PROP_FIXTURE_DIR);
        this.moduleDirectories = Constants.getMarathonDirectories(Constants.PROP_MODULE_DIRS);
        this.nameValidateChecker = nameValidateChecker;
        rootDirectory = new File("");
    }

    public FileHandler(INameValidateChecker nameChecker) throws IOException {
        this(new MarathonFileFilter(), nameChecker);
    }

    @Override public void clearCurrentFile() {
        setCurrentFile(null);
    }

    @Override public File getCurrentFile() {
        return currentFile;
    }

    public File getFile(String fileName) {
        try {
            File file;
            if (fileName.contains(testDirectory.getCanonicalPath())) {
                String relativeFileName = fileName.substring(testDirectory.getCanonicalPath().length() + 1, fileName.length());
                file = new File(testDirectory, relativeFileName);
                if (file.exists()) {
                    if (file.isFile()) {
                        return file;
                    }
                }
            } else {
                for (File moduleDirectorie : moduleDirectories) {
                    if (fileName.contains(moduleDirectorie.getCanonicalPath())) {
                        String relativeFileName = fileName.substring(moduleDirectorie.getCanonicalPath().length() + 1,
                                fileName.length());
                        file = new File(moduleDirectorie, relativeFileName);
                    } else {
                        file = new File(moduleDirectorie, fileName);
                    }
                    if (file.exists()) {
                        if (file.isFile()) {
                            return file;
                        }
                    }
                }
            }
            file = new File(fixtureDirectory, fileName);
            if (file.exists()) {
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override public boolean isModuleFile() {
        for (File moduleDirectorie : moduleDirectories) {
            if (rootDirectory.equals(moduleDirectorie)) {
                return true;
            }
        }
        return false;
    }

    @Override public boolean isProjectFile() {
        if (rootDirectory.equals(testDirectory) || rootDirectory.equals(fixtureDirectory)) {
            return true;
        }
        for (File moduleDirectorie : moduleDirectories) {
            if (rootDirectory.equals(moduleDirectorie)) {
                return true;
            }
        }
        return false;
    }

    @Override public boolean isTestFile() {
        return rootDirectory.equals(testDirectory);
    }

    @Override public String readFile(File file) throws IOException {
        setCurrentFile(file.getCanonicalFile());
        return readFile();
    }

    @Override public File save(String script, Window parent, String filename) throws IOException {
        if (currentFile != null) {
            saveToFile(currentFile, script);
            return currentFile;
        } else {
            this.isNewFile = true;
            return saveAs(script, parent, filename);
        }
    }

    @Override public File saveAs(String script, Window parent, String filename) throws IOException {
        boolean saved = false;
        while (!saved) {
            File file = askForFile(parent, filename);
            if (file == null) {
                return null;
            }
            ButtonType option = ButtonType.YES;
            if (file.exists()) {
                if (nameValidateChecker != null && !nameValidateChecker.okToOverwrite(file)) {
                    return null;
                }
                Optional<ButtonType> result = FXUIUtils.showConfirmDialog(parent,
                        "File " + file.getName() + " already exists. Do you want to overwrite?", "File exists",
                        AlertType.CONFIRMATION, ButtonType.YES, ButtonType.NO);
                option = result.get();
            }
            if (option == ButtonType.YES) {
                setCurrentFile(file);
                saveToFile(currentFile, script);
                return file;
            }
            if (option == ButtonType.CANCEL) {
                return null;
            }
        }
        return null;
    }

    @Override public File saveTo(File file, String script) throws IOException {
        if (file != null) {
            setCurrentFile(file);
            saveToFile(currentFile, script);
        }
        return file;
    }

    @Override public void setCurrentDirectory(File directory) {
        try {
            rootDirectory = directory.getCanonicalFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File askForFile(Window parent, String filename) {
        fileChooserInfo = new MarathonFileChooserInfo("Save File", filename, rootDirectory, isTestFile());
        File selectedFile = FXUIUtils.showMarathonSaveFileChooser(fileChooserInfo, "Saving '" + filename + "'", FXUIUtils.getIcon("saveAs"));
        if (selectedFile != null) {
            String suffix = filter.getSuffix();
            if (suffix == null) {
                throw new RuntimeException("Could not find suffix needed for the script");
            }
            if (selectedFile.getName().indexOf('.') == -1 && !selectedFile.getName().endsWith(suffix)) {
                selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + suffix);
            }
            return selectedFile;
        } else {
            return null;
        }
    }

    private File getRootDir(File file) {
        try {
            String filePath = file.getCanonicalPath();
            if (filePath.startsWith(testDirectory.getCanonicalPath())) {
                return testDirectory;
            }
            for (File moduleDirectorie : moduleDirectories) {
                if (filePath.startsWith(moduleDirectorie.getCanonicalPath())) {
                    return moduleDirectorie;
                }
            }
            if (filePath.startsWith(fixtureDirectory.getCanonicalPath())) {
                return fixtureDirectory;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return new File("");
    }

    private String readFile() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(currentFile), Charset.defaultCharset()));
        try {
            StringBuffer buffer = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + NL);
            }
            reader.close();
            String s = buffer.toString();
            return s;
        } finally {
            reader.close();
        }
    }

    private void saveToFile(File file, String script) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), Charset.defaultCharset());
        try {
            out.write(script);
        } finally {
            out.close();
        }
        if (fileChooserInfo != null) {
            fileChooserInfo.fileCreated(file);
        }
    }

    public void setCurrentFile(File file) {
        currentFile = file;
        if (file != null) {
            rootDirectory = getRootDir(file);
        }
    }

    @Override public String getMode(String fileName) {
        if (fileName == null) {
            return "text";
        }
        String ext = "";
        if (fileName.startsWith("Untitled")) {
            ext = filter.getSuffix().substring(1);
        } else {
            int lastIndexOf = fileName.lastIndexOf('.');
            if (lastIndexOf == -1 || lastIndexOf == fileName.length() - 1) {
                return "text";
            }
            ext = fileName.substring(lastIndexOf + 1);
        }
        if (ext.equals("rb")) {
            return "ruby";
        }
        if (ext.equals("suite") || ext.equals("story") || ext.equals("feature") || ext.equals("issue")) {
            return "json";
        }
        return ext;
    }

    public boolean isFixtureFile() {
        return rootDirectory.equals(fixtureDirectory);
    }

    public String getFixture() {
        if (!isFixtureFile()) {
            throw new RuntimeException("Current file is not a fixture file");
        }
        try {
            String rootPath = rootDirectory.getCanonicalPath();
            String filePath = currentFile.getCanonicalPath();
            if (!filePath.startsWith(rootPath)) {
                throw new RuntimeException("Fixture is not in fixture directory?");
            }
            String fixtureFileName = filePath.substring(rootPath.length() + 1);
            int indexOfDot = fixtureFileName.lastIndexOf('.');
            return fixtureFileName.substring(0, indexOfDot);
        } catch (IOException e) {
            throw new RuntimeException("getFixture" + e.getMessage());
        }
    }

    public boolean isNewFile() {
        return isNewFile;
    }

}
