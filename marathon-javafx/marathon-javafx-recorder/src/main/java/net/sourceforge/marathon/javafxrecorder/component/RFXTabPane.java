package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXTabPane extends RFXComponent {

    public RFXTabPane(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override protected void mouseClicked(MouseEvent me) {
        TabPane tp = ((TabPane) node);
        Tab selectedTab = tp.getSelectionModel().getSelectedItem();
        if (selectedTab != null)
            recorder.recordSelect(this, getTextForTab(tp, selectedTab));
    }

    @Override public String[][] getContent() {
        return getContent((TabPane) node);
    }

}
