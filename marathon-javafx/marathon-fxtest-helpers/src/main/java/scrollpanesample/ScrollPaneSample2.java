/*
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package scrollpanesample;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ScrollPaneSample2 extends Application {

    final ScrollPane sp = new ScrollPane();
    final ImageView[] pics = new ImageView[5];
    final HBox hb = new HBox();
    final Button fileName = new Button("Click Me");
    Rectangle[] rectangles = new Rectangle[10];

    @Override
    public void start(Stage stage) {
        VBox box = new VBox();
        Scene scene = new Scene(box, 180, 180);
        stage.setScene(scene);
        stage.setTitle("ScrollPaneSample");
        box.getChildren().addAll(sp, fileName);
        VBox.setVgrow(sp, Priority.ALWAYS);

        fileName.setLayoutX(30);
        fileName.setLayoutY(160);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Color[] colors = new Color[] { Color.RED, Color.ALICEBLUE, Color.AQUA, Color.BEIGE, Color.BLANCHEDALMOND };
        for (int i = 0; i < 10; i++) {
            int cindex = (i + 5) % 5;
            Rectangle rectangle = new Rectangle(100, 50, colors[cindex]);
            rectangles[i] = rectangle;
            hb.getChildren().add(rectangle);
        }

        sp.setPrefSize(115, 150);
        sp.setContent(hb);
        fileName.setOnMouseClicked((e) -> {
            scrollTo(rectangles[4]);
        });
        stage.show();
    }

    private void scrollTo(Node target) {
        ScrollPane scrollPane = getParentScrollPane(target);
        if (scrollPane == null)
            return;
        Node content = scrollPane.getContent();
        Bounds contentBounds = content.localToScene(content.getBoundsInLocal());
        Bounds viewportBounds = scrollPane.getViewportBounds();
        Bounds nodeBounds = target.localToScene(target.getBoundsInLocal());
        double toVScroll = (nodeBounds.getMinY() - contentBounds.getMinY())
                * ((scrollPane.getVmax() - scrollPane.getVmin()) / (contentBounds.getHeight() - viewportBounds.getHeight()));
        if (toVScroll >= scrollPane.getVmin() && toVScroll < scrollPane.getVmax())
            scrollPane.setVvalue(toVScroll);
        double toHScroll = (nodeBounds.getMinX() - contentBounds.getMinX())
                * ((scrollPane.getHmax() - scrollPane.getHmin()) / (contentBounds.getWidth() - viewportBounds.getWidth()));
        if (toHScroll >= scrollPane.getHmin() && toHScroll < scrollPane.getHmax())
            scrollPane.setHvalue(toHScroll);
    }

    private ScrollPane getParentScrollPane(Node target) {
        Parent p = target.getParent();
        while (p != null && !(p instanceof ScrollPane))
            p = p.getParent();
        return (ScrollPane) p;
    }

    public static void main(String[] args) {
        launch(args);
    }
}