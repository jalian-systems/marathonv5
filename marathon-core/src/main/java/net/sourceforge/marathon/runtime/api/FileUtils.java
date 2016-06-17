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
package net.sourceforge.marathon.runtime.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Logger;

public class FileUtils {

    private static final Logger logger = Logger.getLogger(FileUtils.class.getName());

    public static void copyFiles(File src, File dest, FilenameFilter filter) {
        File[] files = src.listFiles(filter);
        if (files == null) {
            logger.warning("copyFiles: No files in src directory " + src);
            return;
        }
        for (int i = 0; i < files.length; i++) {
            File srcFile = files[i];
            File destFile = new File(dest, srcFile.getName());
            try {
                copyFile(srcFile, destFile);
            } catch (IOException e) {
                logger.warning("Copy file failed: src = " + srcFile + " dest = " + destFile);
            }
        }
    }

    public static void copyFile(File srcFile, File destFile) throws IOException {
        FileInputStream is = new FileInputStream(srcFile);
        FileOutputStream os = new FileOutputStream(destFile);
        int n;
        byte[] b = new byte[1024];
        while ((n = is.read(b)) != -1) {
            os.write(b, 0, n);
        }
        os.close();
        is.close();
    }

    public static File findFile(String home, String filename) {
        return findFile(new File(home), filename);
    }

    private static File findFile(File file, String filename) {
        if (file.getName().equals(filename))
            return file;
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (File file2 : listFiles) {
                File found = findFile(file2, filename);
                if (found != null)
                    return found;
            }
        }
        return null;
    }

}
