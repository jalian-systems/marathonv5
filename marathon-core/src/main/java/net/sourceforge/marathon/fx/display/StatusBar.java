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
package net.sourceforge.marathon.fx.display;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import net.sourceforge.marathon.editor.IStatusBar;
import net.sourceforge.marathon.fx.api.ModalDialog;

public class StatusBar extends HBox implements IStatusBar {

    private Label rowLabel;
    private Label columnLabel;
    private Label insertLabel;
    private Label msgLabel;
    private Label fixtureLabel;
    private Label extraLabel;

    public StatusBar() {
        setId("status-bar");
        msgLabel = createLabel("");
        extraLabel = createLabel("               ");
        extraLabel.setFont(Font.font("System", FontPosture.ITALIC, 12.0));
        fixtureLabel = createLabel("       ");
        fixtureLabel.setFont(Font.font("System", FontWeight.BOLD, 12.0));
        rowLabel = createLabel("       ");
        columnLabel = createLabel("       ");
        insertLabel = createLabel("               ");
        Region region = new Region();
        getChildren().addAll(msgLabel, region, createSeparator(), extraLabel, createSeparator(), fixtureLabel, createSeparator(),
                rowLabel, createSeparator(), columnLabel, createSeparator(), insertLabel, createSeparator());
        HBox.setHgrow(region, Priority.ALWAYS);
        getStylesheets().add(ModalDialog.class.getClassLoader().getResource("net/sourceforge/marathon/fx/api/css/marathon.css")
                .toExternalForm());
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setMinWidth(Region.USE_PREF_SIZE);
        label.setPadding(new Insets(3, 15, 3, 3));
        label.setAlignment(Pos.CENTER);
        return label;
    }

    private Separator createSeparator() {
        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setPadding(new Insets(3, 0, 3, 0));
        return separator;
    }

    @Override public void setCaretLocation(Object row, Object col) {
        if (row == null && col == null) {
            this.rowLabel.setText("");
            this.columnLabel.setText("");
        } else {
            this.rowLabel.setText(" " + row.toString());
            this.columnLabel.setText(" " + col);
        }
        rowLabel.setMinWidth(Region.USE_PREF_SIZE);
        columnLabel.setMinWidth(Region.USE_PREF_SIZE);
    }

    @Override public void setIsOverwriteEnabled(boolean isOverwriteEnabled) {
        if (isOverwriteEnabled) {
            this.insertLabel.setText("Overwrite");
        } else {
            this.insertLabel.setText("Insert    ");
        }
        insertLabel.setMinWidth(Region.USE_PREF_SIZE);
    }

    public void setApplicationState(String state) {
        msgLabel.setText(state);
        msgLabel.setMinWidth(Region.USE_PREF_SIZE);
    }

    public void setFixture(String fixture) {
        fixtureLabel.setText(fixture);
        fixtureLabel.setMinWidth(Region.USE_PREF_SIZE);
    }

    public Node getFixtureLabel() {
        return fixtureLabel;
    }

    public Node getRowLabel() {
        return rowLabel;
    }

    public Node getInsertLabel() {
        return insertLabel;
    }

}
