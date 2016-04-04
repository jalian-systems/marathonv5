package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXComboBox extends RFXComponent {

    private Object prevSelectedItem;

    public RFXComboBox(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusGained(RFXComponent prev) {
        prevSelectedItem = ((ComboBox<?>) node).getSelectionModel().getSelectedItem();
    }

    @Override public void focusLost(RFXComponent next) {
        ComboBox<?> comboBox = (ComboBox<?>) node;
        Object selectedItem = comboBox.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.equals(prevSelectedItem))
            return;
        if (!comboBox.isEditable()) {
            recorder.recordSelect(this, getComboBoxText(comboBox, comboBox.getSelectionModel().getSelectedIndex(), true));
        } else {
            String editorText = comboBox.getEditor().getText();
            String selectedItemText = getComboBoxText(comboBox, comboBox.getSelectionModel().getSelectedIndex(), false);
            if (editorText.equals(selectedItemText))
                recorder.recordSelect(this, getComboBoxText(comboBox, comboBox.getSelectionModel().getSelectedIndex(), true));
            else
                recorder.recordSelect(this, editorText);
        }
    }

    @Override public String[][] getContent() {
        return getContent((ComboBox<?>) node);
    }

    @Override public String _getText() {
        return getComboBoxText((ComboBox<?>) node, ((ComboBox<?>) node).getSelectionModel().getSelectedIndex(), true);
    }

}
