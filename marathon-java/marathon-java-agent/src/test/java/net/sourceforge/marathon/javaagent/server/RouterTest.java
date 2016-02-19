package net.sourceforge.marathon.javaagent.server;

import org.json.JSONObject;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import fi.iki.elonen.NanoHTTPD.Method;

@Test public class RouterTest {

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
