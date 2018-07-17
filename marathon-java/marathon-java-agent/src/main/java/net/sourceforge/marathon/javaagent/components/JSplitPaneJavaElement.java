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
import java.util.logging.Logger;

import javax.swing.JSplitPane;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JSplitPaneJavaElement extends AbstractJavaElement {

    public static final Logger LOGGER = Logger.getLogger(JSplitPaneJavaElement.class.getName());

    public JSplitPaneJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override
    public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        JSplitPane pane = (JSplitPane) component;
        if (selector.equals("left") || selector.equals("top")) {
            return Arrays.asList(JavaElementFactory.createElement(pane.getTopComponent(), getDriver(), getWindow()));
        } else if (selector.equals("right") || selector.equals("bottom")) {
            return Arrays.asList(JavaElementFactory.createElement(pane.getBottomComponent(), getDriver(), getWindow()));
        }
        return super.getByPseudoElement(selector, params);
    }

    @Override
    public boolean marathon_select(String value) {
        ((JSplitPane) component).setDividerLocation(Integer.parseInt(value));
        return true;
    }
}
