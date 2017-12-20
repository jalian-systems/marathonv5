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
package net.sourceforge.marathon.runtime.api;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * FilePath provides makes it easy to work with file paths.
 */
public class FilePath {

    public static final Logger LOGGER = Logger.getLogger(FilePath.class.getName());

    ArrayList<String> pathElements = new ArrayList<String>();

    /**
     * Construct a FilePath with the given base directory. It is expected that
     * all paths passed to FilePath are returned by
     * {@link File#getCanonicalPath()}
     *
     * @param base
     *            , path to the base directory
     * @throws Exception
     */
    public FilePath(String base) {
        StringTokenizer tok = new StringTokenizer(base, File.separator);
        while (tok.hasMoreTokens()) {
            pathElements.add(tok.nextToken());
        }
    }

    public boolean isRelative(String currentPath) {
        FilePath current = new FilePath(currentPath);
        int baseIndex = 0;
        int currentIndex = 0;
        while (baseIndex < pathElements.size() && currentIndex < current.pathElements.size()
                && pathElements.get(baseIndex).equals(current.pathElements.get(currentIndex))) {
            baseIndex++;
            currentIndex++;
        }
        if (baseIndex == 0) {
            // Files might exist on different drives on Windows
            return false;
        }
        return true;
    }

    public String getRelative(String currentPath) {
        FilePath current = new FilePath(currentPath);
        int baseIndex = 0;
        int currentIndex = 0;
        while (baseIndex < pathElements.size() && currentIndex < current.pathElements.size()
                && pathElements.get(baseIndex).equals(current.pathElements.get(currentIndex))) {
            baseIndex++;
            currentIndex++;
        }
        if (baseIndex == 0 && currentPath.charAt(0) != File.separatorChar) {
            // Files might exist on different drives on Windows
            return currentPath;
        }
        if (baseIndex == pathElements.size() && currentIndex == current.pathElements.size()) {
            return ".";
        }
        if (baseIndex < pathElements.size() && currentIndex == current.pathElements.size()) {
            StringBuilder rest = new StringBuilder();
            for (int i = baseIndex; i < pathElements.size() - 1; i++) {
                rest.append("..").append(File.separator);
            }
            rest.append("..");
            return rest.toString();
        }
        if (baseIndex == pathElements.size() && currentIndex < current.pathElements.size()) {
            StringBuilder rest = new StringBuilder();
            for (int i = currentIndex; i < current.pathElements.size() - 1; i++) {
                rest.append(current.pathElements.get(i)).append(File.separator);
            }
            rest.append(current.pathElements.get(current.pathElements.size() - 1));
            return rest.toString();
        }
        StringBuilder rest = new StringBuilder();
        for (int i = baseIndex; i < pathElements.size() - 1; i++) {
            rest.append("..").append(File.separator);
        }
        rest.append("..");
        for (int i = currentIndex; i < current.pathElements.size(); i++) {
            rest.append(File.separator).append(current.pathElements.get(i));
        }
        return rest.toString();
    }
}
