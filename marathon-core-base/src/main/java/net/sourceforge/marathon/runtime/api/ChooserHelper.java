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
import java.io.IOException;
import java.util.logging.Logger;

import org.json.JSONArray;

public class ChooserHelper {

    public static final Logger LOGGER = Logger.getLogger(ChooserHelper.class.getName());

    private static final String homeDir;
    private static final String cwd;
    private static final String marathonDir;

    static {
        homeDir = getRealPath(System.getProperty("user.home", null));
        cwd = getRealPath(System.getProperty("user.dir", null));
        marathonDir = getRealPath(System.getProperty("marathon.project.dir", null));
    }

    private static String getRealPath(String path) {
        if (path == null) {
            return "";
        }
        try {
            return new File(path).getCanonicalPath();
        } catch (IOException e) {
            return "";
        }
    }

    public static String decode(String s) {
        if (s.length() == 0) {
            return "";
        }
        JSONArray ja = new JSONArray(s);
        JSONArray r = new JSONArray();
        for (int i = 0; i < ja.length(); i++) {
            String file = ja.getString(i);
            r.put(decodeFile(file));
        }
        return r.toString();
    }

    public static String decodeFile(String file) {
        if (file.startsWith("#M")) {
            return file.replace("#M", marathonDir);
        } else if (file.startsWith("#C")) {
            return file.replace("#C", cwd);
        } else if (file.startsWith("#H")) {
            return file.replace("#H", homeDir);
        }
        return file;
    }

    public static String encode(File file) {
        String path;
        try {
            path = file.getCanonicalPath();

            String prefix = "";
            if (marathonDir != null && path.startsWith(marathonDir)) {
                prefix = "#M";
                path = path.substring(marathonDir.length());
            } else if (cwd != null && path.startsWith(cwd)) {
                prefix = "#C";
                path = path.substring(cwd.length());
            } else if (homeDir != null && path.startsWith(homeDir)) {
                prefix = "#H";
                path = path.substring(homeDir.length());
            }
            return (prefix + path).replace(File.separatorChar, '/');
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
