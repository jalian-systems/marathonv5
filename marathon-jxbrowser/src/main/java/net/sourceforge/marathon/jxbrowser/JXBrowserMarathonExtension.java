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
package net.sourceforge.marathon.jxbrowser;

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

import javafx.scene.Node;
import net.sourceforge.marathon.javafxagent.IJavaElementFinder;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElementFactory;

public class JXBrowserMarathonExtension {

    public static final Logger logger = Logger.getLogger(JXBrowserMarathonExtension.class.getName());

    public static void premain(final String args, Instrumentation instrumentation) throws Exception {
        logger.warning("Loading extension " + JXBrowserMarathonExtension.class.getName());
        JavaFXElementFactory.add(new IJavaElementFinder() {
            @Override public Class<? extends IJavaFXElement> get(Node component) {
                if (component.getClass().getName().equals("com.teamdev.jxbrowser.chromium.javafx.BrowserView")) {
                    return JavaFXBrowserViewElement.class;
                }
                return null;
            }
        });
        if ("recording".equals(System.getProperty("marathon.mode"))) {
            instrumentation.addTransformer(new BrowserTransformer());
            RecorderInit.init();
        }
    }
}
