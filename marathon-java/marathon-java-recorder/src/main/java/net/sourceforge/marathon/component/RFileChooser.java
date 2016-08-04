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
package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RFileChooser extends RComponent {
    private static final String homeDir;
    private static final String cwd;
    private static final String marathonDir;

    static {
        homeDir = getRealPath(System.getProperty("user.home", null));
        cwd = getRealPath(System.getProperty("user.dir", null));
        marathonDir = getRealPath(System.getProperty("marathon.project.dir", null));
    }

    public RFileChooser(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void actionPerformed(ActionEvent e) {
        if (e.getSource() != component) {
            return;
        }
        JFileChooser fc = (JFileChooser) component;
        String cmd = e.getActionCommand();
        if (cmd.equals("ApproveSelection")) {
            recordApproveSelection(fc);
        } else {
            recorder.recordSelect(this, "");
        }
    }

    private void recordApproveSelection(JFileChooser fc) {
        if (fc.isMultiSelectionEnabled()) {
            File[] fs = fc.getSelectedFiles();
            recorder.recordSelect(this, encode(fs));
        } else {
            File file = fc.getSelectedFile();
            recorder.recordSelect(this, encode(file));
        }
    }

    private static String getRealPath(String path) {
        if (path == null) {
            return null;
        }
        try {
            return new File(path).getCanonicalPath();
        } catch (IOException e) {
            return null;
        }
    }

    public static String encode(File[] selectedfiles) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < selectedfiles.length; i++) {
            String encode = encode(selectedfiles[i]);
            if (encode != null) {
                buffer.append(encode);
            }
            if (i < selectedfiles.length - 1) {
                buffer.append(File.pathSeparator);
            }
        }
        return buffer.toString();
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
