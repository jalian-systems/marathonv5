package net.sourceforge.marathon.javafxrecorder.component;

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
				String code = "{"
						+ "javafx.scene.Node m$r = ((javafx.stage.Stage)com.sun.javafx.stage.StageHelper.getStages().get(0)).getScene().getRoot() ;"
						+ "m$r.getProperties().put(\"marathon.selectedFiles\", $_);"
						+ "m$r.fireEvent(new javafx.event.Event((javafx.event.EventType)m$r.getProperties().get(\"marathon.fileChooser.eventType\")));"
						+ "}";
				showFileChooser.insertAfter(code);
				CtMethod showFolderChooser = cl.getDeclaredMethod("showFolderChooser");
				code = "{"
						+ "javafx.scene.Node m$r = ((javafx.stage.Stage)com.sun.javafx.stage.StageHelper.getStages().get(0)).getScene().getRoot() ;"
						+ "m$r.getProperties().put(\"marathon.selectedFolder\", $_);"
						+ "m$r.fireEvent(new javafx.event.Event((javafx.event.EventType)m$r.getProperties().get(\"marathon.folderChooser.eventType\")));"
						+ "}";
				showFolderChooser.insertAfter(code);
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
