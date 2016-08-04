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
package net.sourceforge.marathon.javaagent.components;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JSpinner;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgentException;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JSpinnerJavaElement extends AbstractJavaElement {

    public JSpinnerJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        JComponent spinnerEditor = ((JSpinner) component).getEditor();
        if (spinnerEditor == null) {
            throw new JavaAgentException("Null value returned by getEditor() on spinner", null);
        }
        IJavaElement ele = JavaElementFactory.createElement(spinnerEditor, driver, window);
        ele.marathon_select(value);
        try {
            ((JSpinner) component).commitEdit();
        } catch (Throwable t) {
        }
        return true;
    }

    @Override public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("editor")) {
            return Arrays.asList(JavaElementFactory.createElement(getEditor(), getDriver(), getWindow()));
        }
        return super.getByPseudoElement(selector, params);
    }

    @Override public String _getText() {
        IJavaElement editor = JavaElementFactory.createElement(getEditor(), getDriver(), getWindow());
        return editor.getText();
    }

    private Component getEditor() {
        JComponent editorComponent = ((JSpinner) component).getEditor();
        if (editorComponent == null) {
            throw new JavaAgentException("Null value returned by getEditor() on spinner", null);
        }
        if (editorComponent instanceof JSpinner.DefaultEditor) {
            editorComponent = ((JSpinner.DefaultEditor) editorComponent).getTextField();
        }
        return editorComponent;
    }
}
