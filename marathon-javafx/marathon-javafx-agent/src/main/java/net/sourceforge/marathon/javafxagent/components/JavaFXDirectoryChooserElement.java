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
package net.sourceforge.marathon.javafxagent.components;

import java.io.File;
import java.util.logging.Logger;

import javafx.scene.shape.Line;
import javafx.stage.DirectoryChooser;
import net.sourceforge.marathon.compat.JavaCompatibility;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXDirectoryChooserElement extends JavaFXElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXDirectoryChooserElement.DirctoryChooserNode.class.getName());

    public static class DirctoryChooserNode extends Line {
    }

    public JavaFXDirectoryChooserElement(IJavaFXAgent driver, JFXWindow window) {
        super(new DirctoryChooserNode(), driver, window);
    }

    @Override public void sendKeys(CharSequence... keysToSend) {
        String value = (String) keysToSend[0];
        javafx.scene.Node m$r = JavaCompatibility.getStages().get(0).getScene().getRoot();
        File folder = null;
        if (!"".equals(value)) {
            folder = new File(value);
        }
        m$r.getProperties().put("marathon.play.selectedFolder", folder);
        synchronized (DirectoryChooser.class) {
            DirectoryChooser.class.notifyAll();
        }
    }
}
