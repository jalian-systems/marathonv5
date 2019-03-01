package net.sourceforge.marathon.javafxrecorder.component.richtextfx;

import java.util.logging.Logger;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import net.sourceforge.marathon.javafxagent.components.richtextfx.GenericStyledArea;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponent;

public class RFXGenericStyledArea extends RFXComponent {

    public static final Logger LOGGER = Logger.getLogger(RFXGenericStyledArea.class.getName());

    private String prevText = null;

    public RFXGenericStyledArea(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override
    public void focusGained(RFXComponent prev) {
        prevText = new GenericStyledArea(getComponent()).getText();
    }

    @Override
    public void focusLost(RFXComponent next) {
        String text = new GenericStyledArea(getComponent()).getText();
        if (!text.equals(prevText)) {
            recorder.recordSelect(this, text);
        }
    }

    @Override
    public String _getText() {
        return new GenericStyledArea(getComponent()).getText();
    }

    @Override
    public String getTagName() {
        return GenericStyledArea.getTagName(getComponent());
    }

}
