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
package net.sourceforge.marathon.checklist;

import java.io.File;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.sourceforge.marathon.checklist.CheckListFormNode.Mode;

public class CheckListTest extends Application {

    public static final Logger LOGGER = Logger.getLogger(CheckListTest.class.getName());

    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(getContent());
        primaryStage.show();
    }

    private Scene getContent() {
        VBox node = new VBox();
        Node node2 = null;
        try {
            node2 = new CheckListView(new CheckListFormNode(
                    CheckList.read(new File("/Users/adityakarra/Projects/SwingTest/OmapProject.mpd/Checklists/Checklist.xml")),
                    Mode.EDIT));
        } catch (Exception e) {
        }
        node.getChildren().add(node2);
        return new Scene(node);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
