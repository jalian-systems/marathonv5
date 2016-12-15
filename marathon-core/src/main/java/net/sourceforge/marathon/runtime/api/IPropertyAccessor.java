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

public interface IPropertyAccessor {

    public static final String METHOD_EQUALS_IGNORE_CASE = "equalsIgnoreCase";
    public static final String METHOD_EQUALS = "equals";
    public static final String METHOD_MATCHES = "matches";
    public static final String METHOD_STARTS_WITH = "startsWith";
    public static final String METHOD_ENDS_WITH = "endsWith";
    public static final String METHOD_CONTAINS = "contains";

    String getProperty(String name);

    boolean isMatched(String method, String name, String value);
}
