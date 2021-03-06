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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Logger;

import org.openqa.selenium.Platform;

public class ClassPathHelper {

    public static final Logger LOGGER = Logger.getLogger(ClassPathHelper.class.getName());

    public static String getClassPath(Class<?> klass) {
        String packageName = klass.getPackage().getName();
        return getClassPath(packageName, klass.getName());
    }

    private static String getClassPath(String packageName, String name) {
        name = name.replace('.', '/');
        URL url = ClassPathHelper.class.getResource("/" + name + ".class");
        if (url == null) {
            return null;
        }
        String resource = null;
        try {
            resource = URLDecoder.decode(url.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        if (resource.startsWith("jar:")) {
            resource = resource.substring(4);
        }
        if (resource.startsWith("file:")) {
            resource = resource.substring(5);
        }
        int index = resource.indexOf('!');
        if (index != -1) {
            resource = resource.substring(0, index);
        } else {
            String packagePath = packageName.replace('.', '/');
            index = resource.indexOf(packagePath);
            if (index != -1) {
                resource = resource.substring(0, index - 1);
            }
        }
        if (Platform.getCurrent().is(Platform.WINDOWS)) {
            resource = resource.substring(1);
        }
        return resource.replace('/', File.separatorChar);
    }

    public static String getClassPath(String name) {
        int lastIndexOf = name.lastIndexOf('.');
        String packageName;
        if (lastIndexOf == -1) {
            packageName = "";
        } else {
            packageName = name.substring(0, lastIndexOf);
        }
        return getClassPath(packageName, name);
    }
}
