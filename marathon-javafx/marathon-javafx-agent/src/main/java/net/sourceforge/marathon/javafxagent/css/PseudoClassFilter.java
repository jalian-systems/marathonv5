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

public class PseudoClassFilter implements SelectorFilter {

    public static final Logger LOGGER = Logger.getLogger(PseudoClassFilter.class.getName());

    private String function;
    private Argument[] args;

    public PseudoClassFilter(String function) {
        this(function, new Argument[0]);
    }

    public PseudoClassFilter(String function, Argument[] args) {
        this.function = function;
        this.args = args;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(":" + function);
        if (args.length > 0) {
            sb.append("(");
            for (Argument arg : args) {
                sb.append(arg.toString());
                sb.append(", ");
            }
            sb.setLength(sb.length() - 2);
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public List<IJavaFXElement> match(IJavaFXElement je) {
        if (doesMatch(je)) {
            return Arrays.asList(je);
        }
        return new ArrayList<IJavaFXElement>();
    }

    public boolean doesMatch(IJavaFXElement je) {
        if (args == null || args.length == 0) {
            return je.filterByPseudoClass(function);
        }
        Object[] params = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            params[i] = args[i].getValue();
        }
        return je.filterByPseudoClass(function, params);
    }

    public boolean isNth() {
        return function.equals("nth");
    }

    public int getNthIndex() {
        return (Integer) args[0].getValue();
    }
}
