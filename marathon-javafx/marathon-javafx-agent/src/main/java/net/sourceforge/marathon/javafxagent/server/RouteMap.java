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
package net.sourceforge.marathon.javafxagent.server;

import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD.Method;

public class RouteMap {

    public static final Logger LOGGER = Logger.getLogger(RouteMap.class.getName());

    private final Method method;
    private final String uri;
    private final java.lang.reflect.Method proc;

    public RouteMap(Method method, String uri) {
        this(method, uri, null);
    }

    public RouteMap(Method method, String uri, java.lang.reflect.Method proc) {
        this.method = method;
        this.uri = uri;
        this.proc = proc;
    }

    public Method getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public java.lang.reflect.Method getProc() {
        return proc;
    }

    @Override public String toString() {
        return " method = " + method + "\nuri = " + uri;
    }
}
