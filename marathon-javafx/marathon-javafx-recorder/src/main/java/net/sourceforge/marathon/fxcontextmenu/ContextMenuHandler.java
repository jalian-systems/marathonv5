package net.sourceforge.marathon.fxcontextmenu;

import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.PopupWindow;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponentFactory;

public class ContextMenuHandler {

	private AssertionPanel root;
	private PopupWindow popup;

	public ContextMenuHandler(IJSONRecorder recorder, RFXComponentFactory finder) {
		popup = new PopupWindow() {
		};
		root = new AssertionPanel(popup, recorder, finder);
		root.setPrefWidth(300.0);
		popup.getScene().setRoot(root);
		popup.sizeToScene();
	}

	public void showPopup(Event event) {
		if (event instanceof KeyEvent || event.getEventType().equals(MouseEvent.MOUSE_PRESSED))
			root.setContent(event);
		if (popup.isShowing())
			return;
		if (event instanceof MouseEvent) {
			popup.show((Node) event.getSource(), ((MouseEvent) event).getScreenX(), ((MouseEvent) event).getScreenY());
		} else {
			Node source;
			source = (Node) event.getSource();
			if(event.getTarget() instanceof Node)
				source = (Node) event.getTarget();
			Bounds bounds = source.getBoundsInLocal();
			bounds = source.localToScreen(bounds);
			popup.show((Node) event.getSource(), bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2);
		}
	}

	public boolean isShowing() {
		return popup.isShowing();
	}

}
