package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXChoiceBox extends RFXComponent {

    private Object prevSelectedItem;

    public RFXChoiceBox(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusGained(RFXComponent prev) {
        prevSelectedItem = ((ChoiceBox<?>) node).getSelectionModel().getSelectedItem();
    }

    @Override public void focusLost(RFXComponent next) {
        ChoiceBox<?> choiceBox = (ChoiceBox<?>) node;
        Object selectedItem = choiceBox.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.equals(prevSelectedItem))
            return;
        String text = getChoiceBoxText(choiceBox, choiceBox.getSelectionModel().getSelectedIndex());
        if (text != null)
            recorder.recordSelect(this, text);
    }

    @Override public String _getText() {
        return getChoiceBoxText((ChoiceBox<?>) node, ((ChoiceBox<?>) node).getSelectionModel().getSelectedIndex());
    }

    @Override public String[][] getContent() {
        return getContent((ChoiceBox<?>) node);
    }
}
