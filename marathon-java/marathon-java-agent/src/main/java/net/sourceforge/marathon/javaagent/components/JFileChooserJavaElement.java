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
package net.sourceforge.marathon.javaagent.components;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;

import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.JavaElement;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JFileChooserJavaElement extends JavaElement {
    private static final String homeDir;
    private static final String cwd;
    private static final String marathonDir;

    static {
        homeDir = getRealPath(System.getProperty("user.home", null));
        cwd = getRealPath(System.getProperty("user.dir", null));
        marathonDir = getRealPath(System.getProperty("marathon.project.dir", null));
    }

    public JFileChooserJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        JFileChooser fc = (JFileChooser) component;
        if (value.equals("")) {
            fc.cancelSelection();
            return true;
        }
        if (fc.isMultiSelectionEnabled()) {
            fc.setSelectedFiles(decode(value));
            fc.approveSelection();
            return true;
        }
        fc.setSelectedFile(decodeFile(value));
        fc.approveSelection();
        return true;
    }

    private static String getRealPath(String path) {
        if (path == null)
            return null;
        try {
            return new File(path).getCanonicalPath();
        } catch (IOException e) {
            return null;
        }
    }

    private File[] decode(String text) {
        ArrayList<File> files = new ArrayList<File>();
        StringTokenizer tokenizer = new StringTokenizer(text, File.pathSeparator);
        while (tokenizer.hasMoreElements()) {
            File file = decodeFile((String) tokenizer.nextElement());
            files.add(file);
        }
        return (File[]) files.toArray(new File[files.size()]);
    }

    private File decodeFile(String path) {
        String prefix = "";
        if (path.startsWith("#M")) {
            prefix = marathonDir;
            path = path.substring(2);
        } else if (path.startsWith("#C")) {
            prefix = cwd;
            path = path.substring(2);
        } else if (path.startsWith("#H")) {
            prefix = homeDir;
            path = path.substring(2);
        }

        return new File((prefix + path.replace('/', File.separatorChar)));
    }
}