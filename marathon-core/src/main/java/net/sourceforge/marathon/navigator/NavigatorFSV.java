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
package net.sourceforge.marathon.navigator;

import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileSystemView;

final class NavigatorFSV extends FileSystemView {
    private File[] rootDirectory;

    public NavigatorFSV(File[] rootDirectory) {
        this.rootDirectory = new File[rootDirectory.length];
        for (int i = 0; i < rootDirectory.length; i++) {
            try {
                this.rootDirectory[i] = rootDirectory[i].getCanonicalFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public File createNewFolder(File containingDir) throws IOException {
        if (containingDir == null)
            throw new IOException("Parent Directory is null");
        File newDirectory = new File(containingDir, "NewFolder");
        int i = 1;
        while (newDirectory.exists() && i < 100) {
            newDirectory = new File(containingDir, "NewFolder" + i);
            i++;
        }
        if (newDirectory.exists())
            throw new IOException("Directory exists");
        if (!newDirectory.mkdir())
            throw new IOException("Unable to create folder: " + newDirectory);
        return newDirectory;
    }

    public File getDefaultDirectory() {
        return rootDirectory[0];
    }

    public File getHomeDirectory() {
        return rootDirectory[0];
    }

    public File[] getRoots() {
        return rootDirectory;
    }

    public boolean isRoot(File f) {
        for (int i = 0; i < rootDirectory.length; i++) {
            if (f.equals(rootDirectory[i]))
                return true;
        }
        return false;
    }
}
