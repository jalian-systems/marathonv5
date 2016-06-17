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
package net.sourceforge.marathon.runtime.http;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;

import org.json.JSONObject;

public class Session {

    @SuppressWarnings("unused") private Level logLevel;
    private String id;
    private String start;

    public Session() {
        this.id = UUID.randomUUID().toString();
        this.start = new Date().toString();
    }

    public void log(Level info, String string) {
    }

    public String getID() {
        return id;
    }

    public Object findElement(String id) {
        return null;
    }

    public Object getWindow(String string) {
        return null;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public JSONObject getDetails() {
        return new JSONObject().put("start", start);
    }

}
