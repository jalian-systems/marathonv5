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
package net.sourceforge.marathon.javaagent.components;

import java.util.Properties;

import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.json.JSONObject;

public class JavaElementTest {

    protected IJavaElement marathon_select_by_properties(IJavaElement e, String select, boolean isEdit) {
        Properties p = new Properties();
        p.setProperty("select", select);
        JSONObject o = new JSONObject(p);
        String encodedState = "select-by-properties('" + o.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'")
                + "')";
        return e.findElementByCssSelector(".::" + encodedState + (isEdit ? "::editor" : ""));
    }

    protected IJavaElement marathon_select_by_properties(IJavaElement e, Properties p, boolean isEdit) {
        JSONObject o = new JSONObject(p);
        String encodedState = "select-by-properties('" + o.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'")
                + "')";
        return e.findElementByCssSelector(".::" + encodedState + (isEdit ? "::editor" : ""));
    }

    protected void marathon_select(IJavaElement e, String state) {
        String encodedState = state.replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'");
        e.findElementByCssSelector(".::call-select('" + encodedState + "')");
    }

    public void siw(Runnable doRun) {
        try {
            SwingUtilities.invokeAndWait(doRun);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
