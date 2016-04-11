package net.sourceforge.marathon.javafxagent.components;

import java.io.File;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;

import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXDirectoryChooserElement extends JavaFXElement {

    public static class DirctoryChooserNode extends Node {

        @Override protected NGNode impl_createPeer() {
            return null;
        }

        @Override public BaseBounds impl_computeGeomBounds(BaseBounds bounds, BaseTransform tx) {
            return null;
        }

        @Override protected boolean impl_computeContains(double localX, double localY) {
            return false;
        }

        @Override public Object impl_processMXNode(MXNodeAlgorithm alg, MXNodeAlgorithmContext ctx) {
            return null;
        }

    }

    public JavaFXDirectoryChooserElement(IJavaFXAgent driver, JFXWindow window) {
        super(new DirctoryChooserNode(), driver, window);
    }

    @Override public void sendKeys(CharSequence... keysToSend) {
        String value = (String) keysToSend[0];
        javafx.scene.Node m$r = ((javafx.stage.Stage) com.sun.javafx.stage.StageHelper.getStages().get(0)).getScene().getRoot();
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
