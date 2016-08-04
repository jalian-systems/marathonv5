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

/**
 * Information of plug ins such as ScriptModel, Launcher.
 */
public class PlugInModelInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public String name;
    public String className;

    public PlugInModelInfo(String description, String className) {
        this.name = description;
        this.className = className;
    }

    @Override public String toString() {
        return name;
    }
}
