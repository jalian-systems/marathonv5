package net.sourceforge.marathon.javafxrecorder.component;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class FileChooserTransformer implements ClassFileTransformer {

    @Override public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return transformClass(classBeingRedefined, classfileBuffer);
    }

    private byte[] transformClass(Class<?> classBeingRedefined, byte[] b) {
        ClassPool classPool = ClassPool.getDefault();
        CtClass cl = null;
        try {
            cl = classPool.makeClass(new java.io.ByteArrayInputStream(b));
            if (cl.getName().equals("com.sun.glass.ui.CommonDialogs")) {
                CtMethod showFileChooser = cl.getDeclaredMethod("showFileChooser");
                showFileChooser.insertAfter(
                        "((javafx.stage.Stage)com.sun.javafx.stage.StageHelper.getStages().get(0)).getScene().getRoot().getProperties().put(\"selctedFiles\", $_);");
                showFileChooser.insertAfter(
                        "((javafx.stage.Stage)com.sun.javafx.stage.StageHelper.getStages().get(0)).getScene().getRoot().fireEvent(new javafx.event.Event(new javafx.event.EventType(\"filechooser\")));");
                CtMethod showFolderChooser = cl.getDeclaredMethod("showFolderChooser");
                showFolderChooser.insertAfter(
                        "((javafx.stage.Stage)com.sun.javafx.stage.StageHelper.getStages().get(0)).getScene().getRoot().getProperties().put(\"selctedFolder\", $_);");
                showFolderChooser.insertAfter(
                        "((javafx.stage.Stage)com.sun.javafx.stage.StageHelper.getStages().get(0)).getScene().getRoot().fireEvent(new javafx.event.Event(new javafx.event.EventType(\"folderchooser\")));");
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
