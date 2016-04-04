package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXButtonBase extends RFXComponent {

    public RFXButtonBase(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override protected void mouseButton1Pressed(MouseEvent me) {
        if (!node.isDisabled())
            recorder.recordClick(this, me);
    }

    @Override public String _getText() {
        return getButtonText((ButtonBase) node);
    }

    @Override protected void keyPressed(KeyEvent ke) {
        if (ke.getCode() == KeyCode.SPACE) {
            recorder.recordRawKeyEvent(this, ke);
        }
    }
}
