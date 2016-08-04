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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.marathon.javaagent.IJavaElement;

public class AttributeFilter implements SelectorFilter {

    private String name;
    private Argument arg;
    private String op;

    public AttributeFilter(String name, Argument arg, String op) {
        this.name = name;
        this.arg = arg;
        this.op = op;
    }

    @Override public String toString() {
        if (op == null) {
            return "[" + name + "]";
        }
        return "[" + name + " " + op + " " + arg + "]";
    }

    @Override public List<IJavaElement> match(IJavaElement je) {
        if (doesMatch(je)) {
            return Arrays.asList(je);
        }
        return new ArrayList<IJavaElement>();
    }

    public boolean doesMatch(IJavaElement je) {
        if (arg == null) {
            return je.hasAttribue(name);
        }
        String expected = je.getAttribute(name);
        if (expected == null) {
            return false;
        }
        if (op.equals("startsWith")) {
            return expected.startsWith(arg.getStringValue());
        } else if (op.equals("endsWith")) {
            return expected.endsWith(arg.getStringValue());
        } else if (op.equals("contains")) {
            return expected.contains(arg.getStringValue());
        } else {
            return expected.equals(arg.getStringValue());
        }
    }
}
