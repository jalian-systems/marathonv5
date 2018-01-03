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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Argument implements Serializable {

    public static final Logger LOGGER = Logger.getLogger(Argument.class.getName());

    private static final long serialVersionUID = 1L;

    public enum Type {
        STRING, REGEX, NUMBER, NONE, BOOLEAN
    }

    private final String name;
    private final String defaultValue;
    private final List<String> defaultList;
    private final Type type;
    private static final List<String> trueList = new ArrayList<String>();
    private static final List<String> falseList = new ArrayList<String>();

    static {
        trueList.add("true");
        trueList.add("false");
        falseList.add("false");
        falseList.add("true");
    }

    public Argument(String name) {
        this.name = name;
        this.defaultValue = null;
        this.type = Type.NONE;
        this.defaultList = null;
    }

    public Argument(String name, String defaultValue, Type type) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.type = type;
        this.defaultList = null;
    }

    public Argument(String name, List<String> defaultList, Type type) {
        this.name = name;
        this.defaultList = defaultList;
        this.type = type;
        this.defaultValue = null;
    }

    public String getName() {
        return name;
    }

    public String getDefault() {
        if (defaultValue == null || type == Type.BOOLEAN) {
            return null;
        }
        return defaultValue;
    }

    public List<String> getDefaultList() {
        if (type == Type.BOOLEAN) {
            if (defaultValue.equals("true")) {
                return trueList;
            } else {
                return falseList;
            }
        }
        return defaultList;
    }

    @Override public String toString() {
        if (defaultValue == null) {
            return name;
        }
        return name + "(= " + defaultValue + ")";
    }

    public Type getType() {
        return type;
    }
}
