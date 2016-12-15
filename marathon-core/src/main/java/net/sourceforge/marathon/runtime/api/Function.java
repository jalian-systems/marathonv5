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
package net.sourceforge.marathon.runtime.api;

import java.io.Serializable;
import java.util.List;

public class Function implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private List<Argument> arguments;
    private final String doc;
    private String window;
    private Module parent;

    public Function(String fname, List<Argument> arguments, String doc, Module parent) {
        name = fname;
        this.arguments = arguments;
        this.doc = doc;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    @Override public String toString() {
        return name;
    }

    public String getDocumentation() {
        if (doc != null) {
            return doc;
        }
        return "";
    }

    public void setWindow(String window) {
        this.window = window;
    }

    public String getWindow() {
        return window;
    }

    public Module getParent() {
        return parent;
    }

    public String[][] getArgumentArray() {
        String[] argArray = new String[arguments.size()];
        int defaultStart = -1;
        for (int i = 0; i < arguments.size(); i++) {
            argArray[i] = arguments.get(i).getName();
            if (arguments.get(i).getDefault() != null && defaultStart == -1) {
                defaultStart = i;
            }
        }
        String[] defaults = new String[0];
        if (defaultStart != -1) {
            defaults = new String[arguments.size() - defaultStart];
            for (int i = defaultStart; i < arguments.size(); i++) {
                defaults[i - defaultStart] = arguments.get(i).getDefault();
            }
        }
        return new String[][] { argArray, defaults };
    }
}
