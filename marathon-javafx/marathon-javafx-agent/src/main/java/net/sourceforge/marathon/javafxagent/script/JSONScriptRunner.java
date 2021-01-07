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
package net.sourceforge.marathon.javafxagent.script;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.logging.Logger;

import javafx.scene.Node;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaAgentException;
import net.sourceforge.marathon.javafxagent.server.ExecuteMode;
import net.sourceforge.marathon.javafxagent.server.Session;
import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.json.JSONObject;

public class JSONScriptRunner {

    public static final Logger LOGGER = Logger.getLogger(JSONScriptRunner.class.getName());

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
                || jsonArg instanceof String) {
            return jsonArg;
        }
        if (jsonArg instanceof JSONArray) {
            Object[] r = new Object[((JSONArray) jsonArg).length()];
            for (int i = 0; i < r.length; i++) {
                r[i] = convertToJava(((JSONArray) jsonArg).get(i));
            }
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
            if (script.equals("return window.name")) {
                return null;
            }
            return convertToJson(se.executeScript(script, args));
        } catch (Exception e) {
            throw new JavaAgentException("Script execution failed with an exception (" + script + Arrays.asList(args) + ")", e);
        }
    }

    private Object convertToJson(Object jsonArg) {
        if (jsonArg == null) {
            return null;
        }
        if (jsonArg instanceof Boolean || jsonArg instanceof Integer || jsonArg instanceof Long || jsonArg instanceof Double
                || jsonArg instanceof String) {
            return jsonArg;
        }
        if (jsonArg.getClass().isArray()) {
            JSONArray a = new JSONArray();
            for (int i = 0; i < Array.getLength(jsonArg); i++) {
                a.put(convertToJson(Array.get(jsonArg, i)));
            }
            return a;
        }
        if (jsonArg instanceof Node) {
            IJavaFXElement e = session.findElement((Node) jsonArg);
            return new JSONObject().put("ELEMENT", e.getElementId());
        }
        return jsonArg.toString();
    }

}
