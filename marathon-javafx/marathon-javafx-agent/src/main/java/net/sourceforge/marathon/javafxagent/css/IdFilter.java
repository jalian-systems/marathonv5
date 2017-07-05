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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import net.sourceforge.marathon.javafxagent.IJavaFXElement;

public class IdFilter implements SelectorFilter {

    public static final Logger LOGGER = Logger.getLogger(IdFilter.class.getName());

    private String id;

    public IdFilter(String id) {
        this.id = id;
    }

    @Override public String toString() {
        char[] cs = id.toCharArray();
        boolean needQuotes = false;
        for (char c : cs) {
            needQuotes = needQuotes || !Character.isJavaIdentifierPart(c);
        }
        if (needQuotes) {
            return "#\"" + id + "\"";
        }
        return "#" + id;
    }

    @Override public List<IJavaFXElement> match(IJavaFXElement je) {
        if (id.equals(je.getAttribute("name"))) {
            return Arrays.asList(je);
        }
        return new ArrayList<IJavaFXElement>();
    }
}
