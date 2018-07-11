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
package net.sourceforge.marathon.javafxrecorder.component;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import net.sourceforge.marathon.compat.JavaCompatibility;

public class FileChooserTransformer implements ClassFileTransformer {

    public static final Logger LOGGER = Logger.getLogger(FileChooserTransformer.class.getName());

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
            byte[] classfileBuffer) throws IllegalClassFormatException {
        return transformClass(classBeingRedefined, classfileBuffer);
    }

    private byte[] transformClass(Class<?> classBeingRedefined, byte[] b) {
        ClassPool classPool = ClassPool.getDefault();
        CtClass cl = null;
        try {
            cl = classPool.makeClass(new java.io.ByteArrayInputStream(b));
            if (cl.getName().equals("javafx.stage.FileChooser")) {
                // @formatter: off
                String codeSingle = "{" + JavaCompatibility.getRootAccessCode()
                        + "m$r.getProperties().put(\"marathon.selectedFiles\", $_ == null ? null : java.util.Arrays.asList(new Object[] { $_ }));"
                        + "m$r.fireEvent(new javafx.event.Event((javafx.event.EventType)m$r.getProperties().get(\"marathon.fileChooser.eventType\")));"
                        + "}";
                // @formatter: on
                CtMethod showOpenDialog = cl.getDeclaredMethod("showOpenDialog");
                showOpenDialog.insertAfter(codeSingle);
                CtMethod showSaveDialog = cl.getDeclaredMethod("showSaveDialog");
                showSaveDialog.insertAfter(codeSingle);
                String codeMultiple = "{" + JavaCompatibility.getRootAccessCode()
                        + "m$r.getProperties().put(\"marathon.selectedFiles\", $_ );"
                        + "m$r.fireEvent(new javafx.event.Event((javafx.event.EventType)m$r.getProperties().get(\"marathon.fileChooser.eventType\")));"
                        + "}";
                CtMethod showOpenMultipleDialog = cl.getDeclaredMethod("showOpenMultipleDialog");
                showOpenMultipleDialog.insertAfter(codeMultiple);
            } else if (cl.getName().equals("javafx.stage.DirectoryChooser")) {
                // @formatter: off
                String codeSingle = "{" + JavaCompatibility.getRootAccessCode()
                        + "m$r.getProperties().put(\"marathon.selectedFolder\", $_);"
                        + "m$r.fireEvent(new javafx.event.Event((javafx.event.EventType)m$r.getProperties().get(\"marathon.folderChooser.eventType\")));"
                        + "}";
                // @formatter: on
                CtMethod showDialog = cl.getDeclaredMethod("showDialog");
                showDialog.insertAfter(codeSingle);
            }
            b = cl.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cl != null) {
                cl.detach();
            }
        }
        return b;
    }

}
