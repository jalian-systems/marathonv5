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
package net.sourceforge.marathon.javaagent.server;

import org.json.JSONObject;

public class Route {
    private final java.lang.reflect.Method proc;
    private final JSONObject params;
    private final RouteMap map;

    public Route(java.lang.reflect.Method proc, JSONObject params, RouteMap map) {
        this.proc = proc;
        this.params = params;
        this.map = map;
    }

    public java.lang.reflect.Method getProc() {
        return proc;
    }

    public JSONObject getParams() {
        return params;
    }

    public RouteMap getMap() {
        return map;
    }

    @Override public String toString() {
        return "proc = " + (proc != null ? proc.getName() : "") + " params = " + params + " map = " + map;
    }
}