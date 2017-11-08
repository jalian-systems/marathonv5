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
package net.sourceforge.marathon.api;

import java.util.logging.Logger;

public class JavaVersion {

    public static final Logger LOGGER = Logger.getLogger(JavaVersion.class.getName());

    private static String version = System.getProperty("java.version");

    public static boolean atLeast(String target) {
        int[] current = makeParts(version);
        int[] expected = makeParts(target);
        for (int i = 0; i < 4; i++) {
            if (expected[i] > current[i]) {
                return false;
            } else if (expected[i] < current[i]) {
                return true;
            }
        }
        return true;
    }

    private static int[] makeParts(String v) {
        int[] r = new int[] { 0, 0, 0, 0 };
        String[] parts = v.split("\\.");
        if (parts.length > 0) {
            r[0] = Integer.parseInt(parts[0]);
        }
        if (parts.length > 1) {
            r[1] = Integer.parseInt(parts[1]);
        }
        if (parts.length > 2) {
            String[] minors = parts[2].split("_");
            if (minors.length > 0) {
                r[2] = Integer.parseInt(minors[0]);
            }
            if (minors.length > 1) {
                r[3] = Integer.parseInt(minors[1]);
            }
        }
        return r;
    }
}
