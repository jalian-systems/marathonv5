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
package net.sourceforge.marathon.fx.display;

import java.util.logging.Logger;

import net.sourceforge.marathon.fx.api.FXUIUtils;

public class LineNumberStage extends MarathonInputStage {

    public static final Logger LOGGER = Logger.getLogger(LineNumberStage.class.getName());

    private int maxLine;

    public LineNumberStage() {
        super("Goto", "Goto a line", FXUIUtils.getIcon("goto"));
    }

    @Override protected String validateInput(String inputText) {
        try {
            int line = Integer.parseInt(inputText);
            if (line < 1 || line > maxLine) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            return "Enter a valid number between 1 & " + maxLine;
        }
        return null;
    }

    @Override protected String getInputFiledLabelText() {
        return "Line: ";
    }

    public void setMaxLineNumber(int maxLine) {
        this.maxLine = maxLine;
    }

    public void setLine(int line) {
        setValue(line + "");
    }

    @Override protected void setDefaultButton() {
        okButton.setDefaultButton(true);
    }
}
