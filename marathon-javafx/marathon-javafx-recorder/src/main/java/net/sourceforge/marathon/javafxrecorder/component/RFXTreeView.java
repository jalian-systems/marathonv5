package net.sourceforge.marathon.javafxrecorder.component;

import org.json.JSONArray;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXTreeView extends RFXComponent {

	public RFXTreeView(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
		super(source, omapConfig, point, recorder);
	}

	@Override
	public void focusLost(RFXComponent next) {
		TreeView<?> treeView = (TreeView<?>) getComponent();
		ObservableList<?> selectedItems = treeView.getSelectionModel().getSelectedItems();
		JSONArray pa = new JSONArray();
		for (Object object : selectedItems) {
			pa.put(getTextForNode(treeView, (TreeItem<?>) object));
		}
		recorder.recordSelect(this, pa.toString());
	}
}
