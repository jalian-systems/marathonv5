/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.jxbrowser;

import java.util.Set;
import java.util.logging.Logger;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponent;

public class RFXHomePage extends RFXComponent {

    public static final Logger lOGGER = Logger.getLogger(RFXHomePage.class.getName());
    private int index = -1;
    private String buttonText;

    public RFXHomePage(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        index = getIndexAt((ListView<?>) node, point);
        buttonText = getButtonText((ListView<?>) node, point);
    }

    @Override protected void mousePressed(MouseEvent me) {
        if (onButton((Node) me.getTarget()) && buttonText != null && !buttonText.equals("")) {
            recorder.recordSelect2(this, buttonText, true);
        }
    }

    @Override public String getCellInfo() {
        return index + "";
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((buttonText == null) ? 0 : buttonText.hashCode());
        result = prime * result + index;
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        RFXHomePage other = (RFXHomePage) obj;
        if (buttonText == null) {
            if (other.buttonText != null)
                return false;
        } else if (!buttonText.equals(other.buttonText))
            return false;
        if (index != other.index)
            return false;
        return true;
    }

    protected boolean onButton(Node target) {
        Node parent = target;
        while (parent != null) {
            if (parent instanceof Button)
                return true;
            parent = parent.getParent();
        }
        return false;
    }

    private String getButtonText(ListView<?> listView, Point2D point) {
        point = listView.localToScene(point);
        ListCell<?> cell = getCellAt(listView, index);
        Set<Node> nodes = cell.lookupAll("*");
        for (Node node : nodes) {
            if (node instanceof Button) {
                Bounds boundsInScene = node.localToScene(node.getBoundsInLocal(), true);
                if (boundsInScene.contains(point)) {
                    return ((Button) node).getText();
                }
            }
        }
        return null;
    }

}
