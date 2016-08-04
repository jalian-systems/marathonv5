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

public class Argument {

    private Boolean b;
    private Integer i;
    private Double d;
    private String s;

    public Argument(Boolean b) {
        this.b = b;
    }

    public Argument(int i) {
        this.i = Integer.valueOf(i);
    }

    public Argument(double d) {
        this.d = Double.valueOf(d);
    }

    public Argument(String s) {
        this.s = s;
    }

    @Override public String toString() {
        if (b != null) {
            return b.toString();
        } else if (i != null) {
            return i.toString();
        } else if (d != null) {
            return d.toString();
        } else {
            return "\"" + s + "\"";
        }
    }

    public String getStringValue() {
        if (b != null) {
            return b.toString();
        } else if (i != null) {
            return i.toString();
        } else if (d != null) {
            return d.toString();
        } else {
            return s;
        }
    }

    public Object getValue() {
        if (b != null) {
            return b;
        } else if (i != null) {
            return i;
        } else if (d != null) {
            return d;
        } else {
            return s;
        }
    }
}
