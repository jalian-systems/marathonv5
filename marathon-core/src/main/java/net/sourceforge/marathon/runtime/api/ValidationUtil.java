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

import java.util.StringTokenizer;

public class ValidationUtil {

    public static boolean isValidClassName(String className) {
        if (className.contains("..")) {
            return false;
        }
        StringTokenizer tok = new StringTokenizer(className, ".");
        while (tok.hasMoreTokens()) {
            if (!ValidationUtil.isValidIdentifier(tok.nextToken())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidIdentifier(String part) {
        char[] cs = part.toCharArray();
        if (cs.length == 0 || !Character.isJavaIdentifierStart(cs[0])) {
            return false;
        }
        for (int i = 1; i < cs.length; i++) {
            if (!Character.isJavaIdentifierPart(cs[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidMethodName(String text) {
        return isValidIdentifier(text);
    }

}
