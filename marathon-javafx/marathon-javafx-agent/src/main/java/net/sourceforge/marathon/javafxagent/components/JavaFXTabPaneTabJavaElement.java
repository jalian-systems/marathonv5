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
package net.sourceforge.marathon.javafxagent.components;

import java.util.logging.Logger;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import net.sourceforge.marathon.javafxagent.JavaFXElement;

public class JavaFXTabPaneTabJavaElement extends JavaFXElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXTabPaneTabJavaElement.class.getName());

    private JavaFXElement parent;
    private int tabIndex;

    public JavaFXTabPaneTabJavaElement(JavaFXElement parent, int tabIndex) {
        super(parent);
        this.parent = parent;
        this.tabIndex = tabIndex;
    }

    public JavaFXElement getParent() {
        return parent;
    }

    @Override public String _getText() {
        return getTextForTab((TabPane) parent.getComponent(), ((TabPane) parent.getComponent()).getTabs().get(tabIndex));
    }

    @Override public Point2D _getMidpoint() {
        StackPane tabRegion = getTabRegion();
        Bounds boundsInParent = tabRegion.getBoundsInParent();
        double x = boundsInParent.getWidth() / 2;
        double y = boundsInParent.getHeight() / 2;
        return tabRegion.localToParent(x, y);
    }

    private StackPane getTabRegion() {
        TabPane n = (TabPane) parent.getComponent();
        StackPane node = (StackPane) n.lookup(".headers-region");
        return (StackPane) node.getChildren().get(tabIndex);
    }

}
