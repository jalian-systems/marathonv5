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

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.ButtonBarBuilder;

public class ButtonBarFactory {

    public static JPanel buildOKCancelBar(JButton okButton, JButton cancelButton) {
        return buildRightAlignedBar(okButton, cancelButton);
    }

    public static JPanel buildOKCancelApplyBar(JButton okButton, JButton cancelButton, JButton testButton) {
        return buildRightAlignedBar(okButton, cancelButton, testButton);
    }

    public static JPanel buildRightAlignedBar(JButton... buttons) {
        return ButtonBarBuilder.create().addGlue().addButton(buttons).getPanel();
    }

    public static JPanel buildOKBar(JButton okButton) {
        return buildRightAlignedBar(okButton);
    }

    public static Component buildOKCancelHelpBar(JButton ok, JButton cancel, JButton loadDefaults) {
        return buildRightAlignedBar(ok, cancel, loadDefaults);
    }

}
