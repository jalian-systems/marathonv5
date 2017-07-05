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
package net.sourceforge.marathon.fx.display;

import java.util.logging.Logger;

public class VersionInfo {

    public static final Logger LOGGER = Logger.getLogger(VersionInfo.class.getName());

    private String version;
    private String blurbTitle;
    private String blurbCompany;
    private String blurbWebsite;
    private String blurbCredits;

    public VersionInfo(String version, String blurbTitle, String blurbCompany, String blurbWebsite, String blurbCredits) {
        this.version = version;
        this.blurbTitle = blurbTitle;
        this.blurbCompany = blurbCompany;
        this.blurbWebsite = blurbWebsite;
        this.blurbCredits = blurbCredits;
    }

    public String getVersion() {
        return version;
    }

    public String getBlurbTitle() {
        return blurbTitle;
    }

    public String getBlurbCompany() {
        return blurbCompany;
    }

    public String getBlurbWebsite() {
        return blurbWebsite;
    }

    public String getBlurbCredits() {
        return blurbCredits;
    }
}
