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
package net.sourceforge.marathon.runtime.api;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Logger;

import org.json.JSONObject;

public class JSONObjectPropertyAccessor extends DefaultMatcher implements Serializable {
    
    public static final Logger LOGGER = Logger.getLogger(JSONObjectPropertyAccessor.class.getName());

    private static final long serialVersionUID = 1L;

    private transient JSONObject o;

    public JSONObjectPropertyAccessor() {
    }

    public JSONObjectPropertyAccessor(JSONObject o) {
        this.o = o;
    }

    @Override public String getProperty(String name) {
        if (o.has(name) && o.get(name) instanceof String) {
            return o.getString(name);
        }
        return null;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(o.toString());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        o = new JSONObject((String) s.readObject());
    }

    @Override public String toString() {
        return o.toString();
    }
}
