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
package net.sourceforge.marathon.javafxagent.css;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;

public class DescendentSelector implements Selector {

    public static final Logger LOGGER = Logger.getLogger(DescendentSelector.class.getName());

    private Selector parent;
    private SimpleSelector descendent;

    public DescendentSelector(Selector parent, SimpleSelector child) {
        this.parent = parent;
        this.descendent = child;
    }

    @Override public String toString() {
        return parent + " " + descendent;
    }

    @Override public List<IJavaFXElement> findElements(IJavaFXAgent driver, IJavaFXElement container, long implicitWait) {
        List<IJavaFXElement> result = new ArrayList<IJavaFXElement>();
        List<IJavaFXElement> parents = parent.findElements(driver, container, implicitWait);
        for (IJavaFXElement parent : parents) {
            List<IJavaFXElement> es = descendent.findElements(driver, parent, implicitWait);
            for (IJavaFXElement e : es) {
                if (!result.contains(e)) {
                    result.add(e);
                }
            }
        }
        return result;
    }
}
