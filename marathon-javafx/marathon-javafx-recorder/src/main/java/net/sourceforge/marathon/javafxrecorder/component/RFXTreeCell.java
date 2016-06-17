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
package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXTreeCell extends RFXComponent {

    public RFXTreeCell(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public String _getValue() {
        TreeCell<?> cell = (TreeCell<?>) node;
        Node graphic = cell.getGraphic();
        RFXComponent component = getFinder().findRawRComponent(graphic, null, recorder);
        if (graphic != null && component != null)
            return component._getValue();
        return super._getValue();
    }
}
