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

import java.io.File;
import java.io.IOException;

import javafx.stage.Window;

public interface IResourceHandler {

    String readFile(File file) throws IOException;

    boolean isTestFile();

    File getCurrentFile();

    boolean isProjectFile();

    void setCurrentDirectory(File directory);

    void clearCurrentFile();

    String getMode(String newFileName);

    File saveAs(String text, Window parent, String data) throws IOException;

    File save(String text, Window parent, String data) throws IOException;

    boolean isModuleFile();

    File saveTo(File file, String text) throws IOException;

}
