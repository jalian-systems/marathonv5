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

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import net.sourceforge.marathon.runtime.api.UIUtils;

@SuppressWarnings("serial") public class LineNumberDialog extends MarathonInputDialog {

    private int maxLine;

    public LineNumberDialog(JFrame parent, String title) {
        super(parent, title);
        JTextField lineNumberField = getInputField();
        lineNumberField.setPreferredSize(new Dimension(100, 20));
        lineNumberField.setMaximumSize(lineNumberField.getPreferredSize());
    }

    @Override protected String getFieldLabel() {
        return "&Line: ";
    }

    @Override protected JButton createOKButton() {
        return UIUtils.createGotoButton();
    }

    @Override protected JButton createCancelButton() {
        return UIUtils.createCancelButton();
    }

    @Override protected String validateInput(String inputText) {
        try {
            int line = Integer.parseInt(inputText);
            if (line < 1 || line > maxLine)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            return "Enter a valid number between 1 & " + maxLine;
        }
        return null;
    }

    public void setMaxLineNumber(int maxLine) {
        this.maxLine = maxLine;
    }

    public void setLine(int line) {
        setValue(line + "");
    }

    public int getLineNumber() {
        if (isOk()) {
            return Integer.parseInt(getValue());
        }
        return -1;
    }

}
