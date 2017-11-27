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
package net.sourceforge.marathon.util;

import java.net.URL;
import java.util.logging.Logger;

public class BlurbInfo {

    public static final Logger LOGGER = Logger.getLogger(BlurbInfo.class.getName());

    private URL url;
    private String title;
    private boolean cancelNeeded;
    private String html;

    public BlurbInfo(URL url, String title, boolean cancelNeeded) {
        this.url = url;
        this.title = title;
        this.cancelNeeded = cancelNeeded;
    }

    public BlurbInfo(String html, String title, boolean cancelNeeded) {
        this.html = html;
        this.title = title;
        this.cancelNeeded = cancelNeeded;
    }

    public String getTitle() {
        return title;
    }

    public URL getURL() {
        return url;
    }

    public boolean isCancelNeeded() {
        return cancelNeeded;
    }
    
    public String getHtml() {
        return html;
    }
}
