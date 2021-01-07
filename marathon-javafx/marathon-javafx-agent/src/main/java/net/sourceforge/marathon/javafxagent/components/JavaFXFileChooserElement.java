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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import net.sourceforge.marathon.compat.JavaCompatibility;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;
import net.sourceforge.marathon.json.JSONArray;

public class JavaFXFileChooserElement extends JavaFXElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXFileChooserElement.class.getName());

    public static class FileChooserNode extends Line {

    }

    public JavaFXFileChooserElement(IJavaFXAgent driver, JFXWindow window) {
        super(new FileChooserNode(), driver, window);
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        String value = (String) keysToSend[0];
        javafx.scene.Node m$r = JavaCompatibility.getStages().get(0).getScene().getRoot();
        List<File> files = new ArrayList<>();
        if (!"".equals(value)) {
            JSONArray ja = new JSONArray(value);
            for (int i = 0; i < ja.length(); i++) {
                files.add(new File(ja.getString(i)));
            }
        }
        m$r.getProperties().put("marathon.play.selectedFiles", files);
        synchronized (FileChooser.class) {
            FileChooser.class.notifyAll();
        }
    }
}
