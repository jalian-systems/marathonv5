/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.runtime.api;

public class Indent {
    private static String DEFAULT_INDENT;
    private static String INDENT;
    private static final String SPACES = "        ";

    static {
        INDENT = DEFAULT_INDENT = SPACES.substring(0, 4);
    }

    public static void setDefaultIndent(boolean convert, int tabSize) {
        if (convert) {
            INDENT = DEFAULT_INDENT = SPACES.substring(0, tabSize);
        } else {
            INDENT = DEFAULT_INDENT = "\t";
        }
    }

    public static String getDefaultIndent() {
        return DEFAULT_INDENT;
    }

    private static void setIndent(String iNDENT) {
        INDENT = iNDENT;
    }

    public static void incIndent() {
        setIndent(INDENT + DEFAULT_INDENT);
    }

    public static void decIndent() {
        INDENT = INDENT.replaceFirst(DEFAULT_INDENT, "");
    }

    public static String getIndent() {
        return INDENT;
    }
}
