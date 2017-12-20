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
package net.sourceforge.marathon;

import java.util.logging.Logger;

import net.sourceforge.marathon.api.IVersion;

/**
 * Marathon version information. This tmpl file is used to generated
 * Version.java. Version.java is not part of the source code control.
 */
public class Version {

    public static final Logger LOGGER = Logger.getLogger(Version.class.getName());

    private final static IVersion version;

    static {
        IVersion xversion = null;
        try {
            xversion = (IVersion) Class.forName("Version").newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        version = xversion;
    }

    /**
     * No one can create a Version object
     *
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private Version() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    }

    /**
     * @return the version given in the ant build file.
     */
    public static String id() {
        return version.id();
    }

    /**
     * @return the product name
     */
    public static String product() {
        return version.product();
    }

    /**
     * @return the timestamp of the given build.
     */
    public static String tstamp() {
        return version.tstamp();
    }

    public static String build() {
        return version.build();
    }

    public static String blurbTitle() {
        return version.blurbTitle();
    }

    public static String blurbCompany() {
        return version.blurbCompany();
    }

    public static String blurbWebsite() {
        return version.blurbWebsite();
    }

    public static String blurbCredits() {
        return version.blurbCredits();
    }
}
