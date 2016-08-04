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
package net.sourceforge.marathon.javaagent.css;

import java.util.List;

import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;

public class FindByCssSelector {

    private IJavaElement container;
    private long implicitWait;
    private IJavaAgent driver;

    public FindByCssSelector(IJavaElement container, IJavaAgent driver2, long implicitWait) {
        this.container = container;
        this.driver = driver2;
        this.implicitWait = implicitWait;
    }

    public List<IJavaElement> findElements(String using) {
        Selector selector = new SelectorParser(using).parse();
        return selector.findElements(driver, container, implicitWait);
    }

}
