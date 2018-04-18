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

import java.util.logging.Logger;

public abstract class DefaultMatcher implements IPropertyAccessor {

    public static final Logger LOGGER = Logger.getLogger(DefaultMatcher.class.getName());

    @Override public boolean isMatched(String method, String name, String value) {
        String actual = getProperty(name);
        if (actual == null) {
            return false;
        }
        if (method.equals(IPropertyAccessor.METHOD_ENDS_WITH)) {
            return actual.endsWith(value);
        } else if (method.equals(IPropertyAccessor.METHOD_EQUALS)) {
            return actual.equals(value);
        } else if (method.equals(IPropertyAccessor.METHOD_EQUALS_IGNORE_CASE)) {
            return actual.equalsIgnoreCase(value);
        } else if (method.equals(IPropertyAccessor.METHOD_MATCHES)) {
            return actual.matches(value);
        } else if (method.equals(IPropertyAccessor.METHOD_STARTS_WITH)) {
            return actual.startsWith(value);
        } else if (method.equals(IPropertyAccessor.METHOD_CONTAINS)) {
            return actual.contains(value);
        }
        return false;
    }

}
