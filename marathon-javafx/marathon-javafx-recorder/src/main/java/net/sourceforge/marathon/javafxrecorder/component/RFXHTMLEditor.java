package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.web.HTMLEditor;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXHTMLEditor extends RFXComponent {

    private String prevText;

    public RFXHTMLEditor(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusGained(RFXComponent prev) {
        HTMLEditor htmlEditor = (HTMLEditor) node;
        prevText = htmlEditor.getHtmlText();
    }

    @Override public void focusLost(RFXComponent next) {
        HTMLEditor htmlEditor = (HTMLEditor) node;
        String currentText = htmlEditor.getHtmlText();
        if (currentText != null && !currentText.equals(prevText))
            recorder.recordSelect(this, currentText);
    }

}
