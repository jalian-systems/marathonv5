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

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;

public class BrowserTransformer implements ClassFileTransformer {

    public static final Logger LOGGER = Logger.getLogger(BrowserTransformer.class.getName());
    private boolean recording;

    public BrowserTransformer(boolean recording) {
        this.recording = recording;
    }

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
            if (recording && cl.getName().equals("com.teamdev.jxbrowser.chromium.javafx.BrowserView")) {
                LOGGER.warning("Transforming class BrowserView");
                CtConstructor[] constructors = cl.getConstructors();
                for (CtConstructor ctConstructor : constructors) {
                    if (ctConstructor.getParameterTypes().length == 1) {
                        String src2 = "net.sourceforge.marathon.jxbrowser.RFXBrowserView.generateEvent(this);";
                        ctConstructor.insertAfter(src2);
                    }
                }
            }
            if (cl.getName().equals("com.teamdev.jxbrowser.chromium.Browser")) {
                JavaFXBrowserViewElement.initRemoteDebug();
            }
            return cl.toBytecode();
        } catch (Exception e) {
        } finally {
            if (cl != null) {
                cl.detach();
            }
        }
        return null;
    }
}
