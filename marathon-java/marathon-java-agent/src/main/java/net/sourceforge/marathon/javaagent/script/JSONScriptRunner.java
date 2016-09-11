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
package net.sourceforge.marathon.javaagent.script;

import java.awt.Component;
import java.lang.reflect.Array;

import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgentException;
import net.sourceforge.marathon.javaagent.server.ExecuteMode;
import net.sourceforge.marathon.javaagent.server.Session;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONScriptRunner {

    private String script;
    private Object[] args;
    private Session session;
    private ExecuteMode mode;

    public JSONScriptRunner(String script, JSONArray args, Session session, ExecuteMode mode) {
        this.script = script;
        this.session = session;
        this.mode = mode;
        convertJSONArgs(args);
    }

    private void convertJSONArgs(JSONArray jsonArgs) {
        args = new Object[jsonArgs.length()];
        for (int i = 0; i < jsonArgs.length(); i++) {
            Object jsonArg = jsonArgs.get(i);
            args[i] = convertToJava(jsonArg);
        }
    }

    private Object convertToJava(Object jsonArg) {
        if (jsonArg instanceof Boolean || jsonArg instanceof Integer || jsonArg instanceof Long || jsonArg instanceof Double
                || jsonArg instanceof String)
            return jsonArg;
        if (jsonArg instanceof JSONArray) {
            Object[] r = new Object[((JSONArray) jsonArg).length()];
            for (int i = 0; i < r.length; i++)
                r[i] = convertToJava(((JSONArray) jsonArg).get(i));
            return r;
        }
        if (jsonArg instanceof JSONObject) {
            if (((JSONObject) jsonArg).has("ELEMENT")) {
                return session.findElement(((JSONObject) jsonArg).getString("ELEMENT")).getComponent();
            }
        }
        throw new RuntimeException("Argument of type `" + jsonArg.getClass() + "` not supported: " + jsonArg);
    }

    public Object execute() {
        ScriptExecutor se = new ScriptExecutor(mode);
        try {
            // YUK!!!
            if(script.equals("return window.name"))
                return null;
            return convertToJson(se.executeScript(script, args));
        } catch (Exception e) {
            throw new JavaAgentException("Script execution failed with an exception (" + e.getMessage() + ")", e);
        }
    }

    private Object convertToJson(Object jsonArg) {
        if (jsonArg == null)
            return null;
        if (jsonArg instanceof Boolean || jsonArg instanceof Integer || jsonArg instanceof Long || jsonArg instanceof Double
                || jsonArg instanceof String)
            return jsonArg;
        if (jsonArg.getClass().isArray()) {
            JSONArray a = new JSONArray();
            for (int i = 0; i < Array.getLength(jsonArg); i++) {
                a.put(convertToJson(Array.get(jsonArg, i)));
            }
            return a;
        }
        if (jsonArg instanceof Component) {
            IJavaElement e = session.findElement((Component) jsonArg);
            return new JSONObject().put("ELEMENT", e.getId());
        }
        return jsonArg.toString();
    }

}
