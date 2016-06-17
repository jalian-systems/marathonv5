/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.display;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import net.sourceforge.marathon.editor.IMarathonFileFilter;
import net.sourceforge.marathon.runtime.api.IScriptModel;

public class MarathonFileFilter extends FileFilter implements IMarathonFileFilter {
    private String suffix;
    private final IScriptModel scriptModel;

    public MarathonFileFilter(String sourceFileSuffix, IScriptModel scriptModel) {
        suffix = sourceFileSuffix;
        this.scriptModel = scriptModel;
    }

    public boolean accept(File f) {
        if (f.isDirectory() && !f.getName().startsWith("."))
            return true;
        return !f.isDirectory() && scriptModel.isSourceFile(f);
    }

    public String getDescription() {
        return "Marathon Source Files";
    }

    public FileFilter getChooserFilter() {
        return this;
    }

    public String getSuffix() {
        return suffix;
    }
}
