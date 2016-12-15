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

public enum TokenType {
    // @formatter:off
    TT_IDENTIFIER("identifier"),
    TT_TAG("tag"),
    TT_ATTRIBUTE("attribute"),
    TT_INTEGER("integer"),
    TT_STRING("string"),
    TT_BOOLEAN("true or false"),
    TT_EOF("end of file"),
    TT_CLOSE_PAREN(")"),
    TT_OPEN_BR("["),
    TT_CLOSE_BR("]"),
    TT_OPEN_PAREN("("),
    TT_SELF_SELECTOR("self(.)"),
    TT_PSEUDO_CLASS("pseudo-class"),
    TT_NUMBER("number"),
    TT_ID("id"),
    TT_PSEUDO_CLASS_ARGS("pseudo-class-with-args"),
    TT_GREATER(">"),
    TT_PLUS("+"),
    TT_TILDE("~"),
    TT_EQUALS("="),
    TT_PREFIXMATCH("^="),
    TT_SUFFIXMATCH("$="),
    TT_SUBSTRINGMATCH("*="),
    TT_COMMA(","),
    TT_UNIVERSAL_SELECTOR("*"),
    TT_PSEUDO_ELEMENT("pseudo-element"),
    TT_PSEUDO_ELEMENT_ARGS("pseudo-element-with-args"),
    TT_WHITESPACE("whitespace");
    // @formatter:on

    private String description;

    TokenType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override public String toString() {
        return description;
    }
}
