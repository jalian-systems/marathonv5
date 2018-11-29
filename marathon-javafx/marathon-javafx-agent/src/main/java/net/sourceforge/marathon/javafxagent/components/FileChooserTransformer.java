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
                // @formatter:off"
                String codeSingle =
                        "    {" +
                        "        if (Boolean.getBoolean(\"marathon.recording.paused\") || Boolean.getBoolean(\"marathon.recording.insertscript\") || \"playing\".equals(System.getProperty(\"marathon.mode\"))) {" +
                        "            synchronized(javafx.stage.FileChooser.class) {" +
                        "                try {" +
                        "                    javafx.stage.FileChooser.class.wait(5000L);" +
                        "                } catch (InterruptedException e) {" +
                        "                    e.printStackTrace();" +
                        "                }" +
                        "            }" +
                        JavaCompatibility.getRootAccessCode() +
                        "            java.util.List files = (java.util.List) m$r.getProperties().get(\"marathon.play.selectedFiles\");" +
                        "            return files.size() > 0 ? (java.io.File) files.get(0) : null;" +
                        "        }" +
                        "    }" +
                        "";
                // @formatter:on
                CtMethod showOpenDialog = cl.getDeclaredMethod("showOpenDialog");
                showOpenDialog.insertBefore(codeSingle);
                CtMethod showSaveDialog = cl.getDeclaredMethod("showSaveDialog");
                showSaveDialog.insertBefore(codeSingle);
                // @formatter:off"
                String codeMultiple =
                        "    {" +
                        "        if (Boolean.getBoolean(\"marathon.recording.paused\") || Boolean.getBoolean(\"marathon.recording.insertscript\") || \"playing\".equals(System.getProperty(\"marathon.mode\"))) {" +
                        "            synchronized(javafx.stage.FileChooser.class) {" +
                        "                try {" +
                        "                    javafx.stage.FileChooser.class.wait(5000L);" +
                        "                } catch (InterruptedException e) {" +
                        "                    e.printStackTrace();" +
                        "                }" +
                        "            }" +
                        JavaCompatibility.getRootAccessCode() +
                        "            java.util.List files = (java.util.List) m$r.getProperties().get(\"marathon.play.selectedFiles\");" +
                        "            return files.size() > 0 ? files : null;" +
                        "        }" +
                        "    }" +
                        "";
                // @formatter:on
                CtMethod showOpenMultipleDialog = cl.getDeclaredMethod("showOpenMultipleDialog");
                showOpenMultipleDialog.insertBefore(codeMultiple);
            } else if (cl.getName().equals("javafx.stage.DirectoryChooser")) {
                // @formatter:off"
                String codeSingle =
                        "    {" +
                        "        if (Boolean.getBoolean(\"marathon.recording.paused\") || Boolean.getBoolean(\"marathon.recording.insertscript\") || \"playing\".equals(System.getProperty(\"marathon.mode\"))) {" +
                        "            synchronized(javafx.stage.DirectoryChooser.class) {" +
                        "                try {" +
                        "                    javafx.stage.DirectoryChooser.class.wait(5000L);" +
                        "                } catch (InterruptedException e) {" +
                        "                    e.printStackTrace();" +
                        "                }" +
                        "            }" +
                        JavaCompatibility.getRootAccessCode() +
                        "            return (java.io.File) m$r.getProperties().get(\"marathon.play.selectedFolder\");" +
                        "        }" +
                        "    }" +
                        "";
                // @formatter:on
                CtMethod showDialog = cl.getDeclaredMethod("showDialog");
                showDialog.insertBefore(codeSingle);
            }
            b = cl.toBytecode();
        } catch (Exception e) {
        } finally {
            if (cl != null) {
                cl.detach();
            }
        }
        return b;
    }

    /*
    java.io.File f()
    // @formatter:off
    {
        if ("playing".equals(System.getProperty("marathon.mode"))) {
            synchronized(javafx.stage.FileChooser.class) {
                try {
                    javafx.stage.FileChooser.class.wait(5000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            javafx.scene.Node m$r = net.sourceforge.marathon.compat.JavaCompatibility.getStages().get(0).getScene().getRoot();
            java.util.List files = m$r.getProperties().get("marathon.selected.files");
            return files.get(0);
        }
    }
    // @formatter:on
     */
}
