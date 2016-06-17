/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
            if (event.getTarget() instanceof Node)
                source = (Node) event.getTarget();
            Bounds bounds = source.getBoundsInLocal();
            bounds = source.localToScreen(bounds);
            popup.show((Node) event.getSource(), bounds.getMinX() + bounds.getWidth() / 2,
                    bounds.getMinY() + bounds.getHeight() / 2);
        }
    }

    public boolean isShowing() {
        return popup.isShowing();
    }

}
