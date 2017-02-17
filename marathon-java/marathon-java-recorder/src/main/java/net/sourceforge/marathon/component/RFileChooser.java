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

import javax.swing.JFileChooser;

import net.sourceforge.marathon.javaagent.ChooserHelper;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RFileChooser extends RComponent {

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
            recorder.recordSelect(this, ChooserHelper.encode(fs));
        } else {
            File file = fc.getSelectedFile();
            recorder.recordSelect(this, ChooserHelper.encode(file));
        }
    }
}
