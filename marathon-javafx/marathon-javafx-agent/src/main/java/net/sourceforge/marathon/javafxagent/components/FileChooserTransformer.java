package net.sourceforge.marathon.javafxagent.components;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class FileChooserTransformer implements ClassFileTransformer {

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		return transformClass(classBeingRedefined, classfileBuffer);
	}

	// @formatter:off
	// @formatter:on
	
	private byte[] transformClass(Class<?> classBeingRedefined, byte[] b) {
		ClassPool classPool = ClassPool.getDefault();
		CtClass cl = null;
		try {
			cl = classPool.makeClass(new java.io.ByteArrayInputStream(b));
			if (cl.getName().equals("com.sun.glass.ui.CommonDialogs")) {
				CtMethod showFileChooser = cl.getDeclaredMethod("showFileChooser");
				String code = 
						"{" +
						"  if(System.getProperty(\"marathon.mode\").equals(\"playing\")) {" +
						"    synchronized (javafx.stage.FileChooser.class) {" +
						"      try {" +
						"          javafx.stage.FileChooser.class.wait(5000L);" +
						"      } catch (InterruptedException e) {" +
						"          e.printStackTrace();" +
						"      }" +
						"    }" +
						"    javafx.scene.Node m$r = ((javafx.stage.Stage)com.sun.javafx.stage.StageHelper.getStages().get(0)).getScene().getRoot();" +
						"    com.sun.glass.ui.CommonDialogs.FileChooserResult fileChooserResult = (com.sun.glass.ui.CommonDialogs.FileChooserResult) m$r.getProperties().get(\"marathon.play.selectedFiles\");" +
						"    return fileChooserResult;" +
						"  }" +
						"}" ;
				showFileChooser.insertBefore(code);
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
