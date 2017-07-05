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
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MPFUtils {
    
    public static final Logger LOGGER = Logger.getLogger(MPFUtils.class.getName());

    /**
     * Replaces java properties in the properties of the form %&lt;java
     * property&gt; with the java property value.
     *
     * @param mpfProps
     *            , Properties where the replacement takes place
     */
    public static void replaceEnviron(Properties mpfProps) {
        Enumeration<Object> enumeration = mpfProps.keys();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String value = mpfProps.getProperty(key);
            String updatedValue = getUpdatedValue(value, mpfProps);
            if (updatedValue == null) {
                updatedValue = "";
            }
            mpfProps.setProperty(key, updatedValue);
        }
    }

    /**
     * Get the value for a given MPF property.
     *
     * @param value
     *            , the original value
     * @param mpfProps
     *            , properties from which the replacements are taken
     * @return the modified value
     */
    private static String getUpdatedValue(String value, Properties mpfProps) {
        if (value == null) {
            return null;
        }
        Pattern p = Pattern.compile("[^%]*(%[^%]*%).*");
        Matcher m = p.matcher(value);
        while (m.matches()) {
            String var = m.group(1);
            String varValue = getUpdatedValue(mpfProps.getProperty(var.substring(1, var.length() - 1)), mpfProps);
            if (varValue == null) {
                varValue = System.getProperty(var.substring(1, var.length() - 1), null);
                if (varValue == null) {
                    varValue = System.getenv(var.substring(1, var.length() - 1));
                    if (varValue == null) {
                        varValue = "";
                    }
                }
            }
            value = value.replaceAll(var, escape(varValue));
            m = p.matcher(value);
        }
        return value;
    }

    /**
     * Escape the backslash characters.
     *
     * @param value
     *            , original value
     * @return new value
     */
    private static String escape(String value) {
        return value.replaceAll("\\\\", "\\\\\\\\");
    }

    /**
     * MPF stores all paths with ';' pathSeparator and files with '/'. This
     * function replaces this with system specific pathSeparator character.
     *
     * @param mpfProps
     *            , properties for which the pathSeparator need to be replaced.
     */
    public static void convertPathChar(Properties mpfProps) {
        String value;
        value = mpfProps.getProperty(Constants.PROP_APPLICATION_PATH);
        if (value == null) {
            return;
        }
        mpfProps.setProperty(Constants.PROP_APPLICATION_PATH, convertPathChar(value));
    }

    public static String convertPathChar(String value) {
        value = value.replace(';', File.pathSeparatorChar);
        value = value.replace('/', File.separatorChar);
        return value;
    }

    public static String getUpdatedValue(String value) {
        if (value == null) {
            return null;
        }
        Pattern p = Pattern.compile("[^%]*(%[^%]*%).*");
        Matcher m = p.matcher(value);
        while (m.matches()) {
            String var = m.group(1);
            String varValue = getUpdatedValue(System.getProperty(var.substring(1, var.length() - 1)));
            if (varValue == null) {
                varValue = System.getProperty(var.substring(1, var.length() - 1), null);
                if (varValue == null) {
                    varValue = System.getenv(var.substring(1, var.length() - 1));
                    if (varValue == null) {
                        varValue = "";
                    }
                }
            }
            value = value.replaceAll(var, escape(varValue));
            m = p.matcher(value);
        }
        return value;
    }

    public static String encodeProjectDir(File file, Properties props) {
        String currentFilePath;
        FilePath projectBaseDir;
        try {
            String path;
            File projectDir = new File(props.getProperty(Constants.PROP_PROJECT_DIR));
            if (!projectDir.exists()) {
                return file.getCanonicalPath().replace(File.separatorChar, '/');
            }
            path = projectDir.getCanonicalPath();
            projectBaseDir = new FilePath(path);
            if (file.isFile()) {
                currentFilePath = file.getParentFile().getCanonicalPath();
            } else {
                currentFilePath = file.getCanonicalPath();
            }
            if (!projectBaseDir.isRelative(currentFilePath)) {
                return file.getCanonicalPath().replace(File.separatorChar, '/');
            }
        } catch (Exception e) {
            e.printStackTrace();
            return file.toString().replace(File.separatorChar, '/');
        }
        String relativePath = projectBaseDir.getRelative(currentFilePath);
        if (relativePath.equals(".")) {
            relativePath = "";
        } else if (file.isFile()) {
            relativePath += File.separator;
        }
        return ("%" + Constants.PROP_PROJECT_DIR + "%" + File.separator + relativePath + (file.isFile() ? file.getName() : ""))
                .replace(File.separatorChar, '/');
    }

    public static String decodeProjectDir(String fileName, Properties props) {
        if (props.getProperty(Constants.PROP_PROJECT_DIR) == null) {
            return "";
        }
        fileName = fileName.replaceAll("%" + Constants.PROP_PROJECT_DIR + "%",
                props.getProperty(Constants.PROP_PROJECT_DIR).replace(File.separatorChar, '/'));
        fileName = fileName.replace('/', File.separatorChar);
        try {
            return new File(fileName).getCanonicalPath().replace(File.separatorChar, '/');
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

}
