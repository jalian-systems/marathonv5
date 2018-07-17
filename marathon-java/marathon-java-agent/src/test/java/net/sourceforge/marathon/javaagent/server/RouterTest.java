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
package net.sourceforge.marathon.javaagent.server;

import org.json.JSONObject;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import fi.iki.elonen.NanoHTTPD.Method;

@Test
public class RouterTest {

    public void findRoute() throws Throwable {
        JavaServer router = new JavaServer(1020);
        RouteMap routeMap;
        JSONObject params;
        Route route;
        route = router.findRoute(Method.GET, "/status");
        routeMap = route.getMap();
        params = route.getParams();
        AssertJUnit.assertEquals(Method.GET, routeMap.getMethod());
        AssertJUnit.assertEquals("/status", routeMap.getUri());
        route = router.findRoute(Method.GET, "/session/SESSION_ID/element/ELEMENT_ID/size");
        routeMap = route.getMap();
        params = route.getParams();
        AssertJUnit.assertEquals(Method.GET, routeMap.getMethod());
        AssertJUnit.assertEquals("/session/:sessionId/element/:id/size", routeMap.getUri());
        AssertJUnit.assertEquals("SESSION_ID", params.get("sessionId"));
        route = router.findRoute(Method.GET, "/session/SESSION-ID/element/ELEMENT-ID/css/PROPERTY-NAME");
        routeMap = route.getMap();
        params = route.getParams();
        AssertJUnit.assertEquals(Method.GET, routeMap.getMethod());
        AssertJUnit.assertEquals("/session/:sessionId/element/:id/css/:propertyName", routeMap.getUri());
        AssertJUnit.assertEquals("SESSION-ID", params.get("sessionId"));
        AssertJUnit.assertEquals("ELEMENT-ID", params.get("id"));
        AssertJUnit.assertEquals("PROPERTY-NAME", params.get("propertyName"));
        route = router.findRoute(Method.GET, "/session/SESSION-ID/element/ELEMENT-ID");
        routeMap = route.getMap();
        params = route.getParams();
        AssertJUnit.assertEquals(Method.GET, routeMap.getMethod());
        AssertJUnit.assertEquals("/session/:sessionId/element/:id", routeMap.getUri());
        AssertJUnit.assertEquals("SESSION-ID", params.get("sessionId"));
        AssertJUnit.assertEquals("ELEMENT-ID", params.get("id"));
        route = router.findRoute(Method.POST, "/session/SESSION-ID/element/active");
        routeMap = route.getMap();
        params = route.getParams();
        AssertJUnit.assertEquals(Method.POST, routeMap.getMethod());
        AssertJUnit.assertEquals("/session/:sessionId/element/active", routeMap.getUri());
        AssertJUnit.assertEquals("SESSION-ID", params.get("sessionId"));
    }
}
// /session/:sessionId/element/:id
