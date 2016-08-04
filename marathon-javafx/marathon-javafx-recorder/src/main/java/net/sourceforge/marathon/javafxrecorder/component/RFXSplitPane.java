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
package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXSplitPane extends RFXComponent {

    private String prevLocations;

    public RFXSplitPane(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override protected void mouseButton1Pressed(MouseEvent me) {
        SplitPane sp = (SplitPane) node;
        prevLocations = getDividerLocations(sp);
    }

    @Override protected void mouseReleased(MouseEvent me) {
        SplitPane splitPane = (SplitPane) node;
        String currentDividerLoctions = getDividerLocations(splitPane);
        if (!currentDividerLoctions.equals(prevLocations)) {
            recorder.recordSelect(this, currentDividerLoctions);
        }
    }

    @Override public String _getText() {
        return getDividerLocations((SplitPane) node);
    }
}
