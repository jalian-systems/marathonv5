package net.sourceforge.marathon.javafxrecorder.component;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class MenuItemTransformer implements ClassFileTransformer {

    @Override public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return transformClass(classBeingRedefined, classfileBuffer);
    }

    private byte[] transformClass(Class<?> classBeingRedefined, byte[] b) {
        ClassPool classPool = ClassPool.getDefault();
        CtClass cl = null;
        try {
            cl = classPool.makeClass(new java.io.ByteArrayInputStream(b));
            if (cl.getName().equals("javafx.scene.control.MenuItem")) {
                CtMethod method = cl.getDeclaredMethod("fire");
                String code = "{"
                        + "javafx.scene.Node m$r = ((javafx.stage.Stage)com.sun.javafx.stage.StageHelper.getStages().get(0)).getScene().getRoot() ;"
                        + "((javafx.event.EventHandler)m$r.getProperties().get(\"marathon.menu.handler\")).handle(new javafx.event.ActionEvent(this, this));"
                        + "}";
                method.insertBefore(code);
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
