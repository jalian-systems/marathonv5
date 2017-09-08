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
package net.sourceforge.marathon.javaagent;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.util.List;
import java.util.logging.Logger;

public class WindowTitle {

    public static final Logger LOGGER = Logger.getLogger(WindowTitle.class.getName());

    private Window window;

    private List<List<String>> containerNP;

    public WindowTitle(Window window) {
        this.window = window;
    }

    public String getTitle() {
        String title = getTitleFromNP(window);
        Window[] windows = Window.getWindows();
        String original = title;
        int index = 1;
        for (Window w : windows) {
            if (w == window) {
                return title;
            }
            if (!w.isVisible()) {
                continue;
            }
            String wTitle = getTitleFromNP(w);
            if (original.equals(wTitle)) {
                title = original + "(" + index++ + ")";
            }
        }
        return title;
    }

    private String getTitleFromNP(Window w1) {
        String title = null;
        if (containerNP != null) {
            for (List<String> list : containerNP) {
                title = getWindowName(new JavaElementPropertyAccessor(w1), list);
                if (title != null)
                    break;
            }
        }
        if (title == null) {
            title = getTitle(w1);
        }
        return title;
    }

    private String getWindowName(JavaElementPropertyAccessor w, List<String> properties) {
        StringBuilder sb = new StringBuilder();
        for (String property : properties) {
            String v = w.getAttribute(property);
            if (v == null)
                return null;
            sb.append(v).append(':');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString().trim();
    }

    private String getTitle(Component component) {
        String title = null;
        if (component instanceof Dialog) {
            title = ((Dialog) component).getTitle();
        } else if (component instanceof Frame) {
            title = ((Frame) component).getTitle();
        }
        return title == null ? "<NoTitle>" : title;
    }

    public void setContainerNamingProperties(List<List<String>> containerNP) {
        this.containerNP = containerNP;
    }

}
