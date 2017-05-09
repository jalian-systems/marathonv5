package net.sourceforge.marathon.component;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class FileDialogTransformer implements ClassFileTransformer {

    public static final Logger LOGGER = Logger.getLogger(FileDialogTransformer.class.getName());

    @Override public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return transformClass(classBeingRedefined, classfileBuffer);
    }

    private byte[] transformClass(Class<?> classBeingRedefined, byte[] b) {
        ClassPool classPool = ClassPool.getDefault();
        CtClass cl = null;
        try {
            cl = classPool.makeClass(new java.io.ByteArrayInputStream(b));
            if (cl.getName().equals("java.awt.FileDialog")) {
                CtMethod showFileChooser = cl.getDeclaredMethod("getFile");
                String code = "{"
                        + "dispatchEvent(new java.awt.event.ActionEvent(this, 200, $_));"
                        + "}";
                showFileChooser.insertAfter(code);
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
