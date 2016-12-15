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
package net.sourceforge.marathon.ruby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.Indent;

public class FixtureGenerator {
    // @formatter:off
    private static final String comment_fixture_properties =
        "Launcher uses the properties specified here to launch the application";

    private static final String comment_teardown =
        "Marathon executes this method at the end of test script.";

    private static final String comment_setup =
        "Marathon executes this method before the test script.";

    private static final String comment_test_setup =
        "Marathon executes this method after the first window of the application is displayed.\n" +
        "You can add any Marathon script elements here.";

    private static final String comment_final =
            "Any code you add below this comment is executed before the application is started.\n" +
            "You can use any ruby script here and not selenium and marathon script elements.";

    // @formatter:on

    public void printFixture(Properties props, PrintStream ps, String launcher, List<String> keys) {
        printComments(ps, comment_fixture_properties, "");
        ps.println("#{{{ Fixture Properties");
        ps.println("fixture_properties = {");

        printKeyValue(Constants.PROP_PROJECT_LAUNCHER_MODEL, launcher, ps, false);
        keys = new ArrayList<String>(keys);
        int size = keys.size();
        for (int i = 0; i < size; i++) {
            printProperty(props, keys.get(i), ps, false);
        }

        Enumeration<Object> allKeys = props.keys();
        while (allKeys.hasMoreElements()) {
            String key = (String) allKeys.nextElement();
            if (key.startsWith(Constants.PROP_PROPPREFIX)) {
                printProperty(props, key, ps, false);
            }
        }
        printProperty(props, Constants.FIXTURE_REUSE, ps, true);
        ps.print(Indent.getDefaultIndent());
        ps.println("}");
        ps.println("#}}} Fixture Properties");

        ps.println();
        String d = props.getProperty(Constants.FIXTURE_DESCRIPTION);
        if (!"".equals(d)) {
            printComments(ps, d, "");
        }
        ps.println("class Fixture");

        ps.println();
        printComments(ps, comment_teardown, Indent.getDefaultIndent());
        ps.print(Indent.getDefaultIndent());
        ps.println("def teardown");
        ps.print(Indent.getDefaultIndent());
        ps.print(Indent.getDefaultIndent());
        ps.println();
        ps.print(Indent.getDefaultIndent());
        ps.println("end");
        ps.println();
        printComments(ps, comment_setup, Indent.getDefaultIndent());
        ps.print(Indent.getDefaultIndent());
        ps.println("def setup");

        ps.print(Indent.getDefaultIndent());
        ps.println("end");
        ps.println();
        printComments(ps, comment_test_setup, Indent.getDefaultIndent());
        ps.print(Indent.getDefaultIndent());
        ps.println("def test_setup");
        ps.print(Indent.getDefaultIndent());
        ps.print(Indent.getDefaultIndent());
        ps.println();
        ps.print(Indent.getDefaultIndent());
        ps.println("end");
        ps.println();
        ps.println("end");
        ps.println();
        ps.println("$fixture = Fixture.new");
        ps.println();
        printComments(ps, comment_final, "");
        ps.close();
    }

    private void printProperty(Properties props, String key, PrintStream ps, boolean last) {
        printKeyValue(key, props.getProperty(key), ps, last);
    }

    private void printKeyValue(String key, String value, PrintStream ps, boolean last) {
        ps.print(Indent.getDefaultIndent());
        ps.print(Indent.getDefaultIndent());
        ps.print("'" + key + "' => ");
        ps.print(RubyScriptModel.encode(value));
        if (last) {
            ps.println();
        } else {
            ps.println(",");
        }
    }

    private void printComments(PrintStream ps, String d, String indent) {
        ps.println("=begin");
        BufferedReader reader = new BufferedReader(new StringReader(d));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if ("=end".equals(line)) {
                    line = "\\=end";
                }
                ps.print(indent);
                ps.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ps.println("=end");
        ps.println();
    }

}
