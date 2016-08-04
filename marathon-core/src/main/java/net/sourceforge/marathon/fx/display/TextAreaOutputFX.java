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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import net.sourceforge.marathon.display.IStdOut;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fxdocking.DockKey;
import net.sourceforge.marathon.fxdocking.DockKey.TabPolicy;
import net.sourceforge.marathon.fxdocking.Dockable;

public class TextAreaOutputFX extends Dockable implements IStdOut {

    private ToolBar toolBar = new ToolBar();
    private Button clearButton = FXUIUtils.createButton("clear", "Clear Messages");
    private Button exportButton = FXUIUtils.createButton("export", "Export log to text file");
    private BorderPane content = new BorderPane();
    private TextFlow textFlow = new TextFlow();
    private ScrollPane scrollPane = new ScrollPane(textFlow);

    private List<TextNodeInfo> textNodes = new ArrayList<>();

    private static class TextNodeInfo {
        private StringBuffer text = new StringBuffer();
        private int type;

        public TextNodeInfo(String text, int type) {
            this.text.append(text);
            this.type = type;
        }

        public StringBuffer getText() {
            return text;
        }

        public int getType() {
            return type;
        }
    }

    private static final DockKey DOCK_KEY = new DockKey("Output", "Output", "Output from the scripts",
            FXUIUtils.getIcon("console_view"), TabPolicy.NotClosable, Side.BOTTOM);

    public TextAreaOutputFX() {
        initComponents();
    }

    private void initComponents() {
        toolBar.setId("toolBar");
        toolBar.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        toolBar.getItems().addAll(exportButton, clearButton);
        clearButton.setDisable(true);
        exportButton.setDisable(true);
        clearButton.setOnAction((e) -> clear());
        exportButton.setOnAction(e -> onExport());
        content.setTop(toolBar);
        content.setCenter(scrollPane);
    }

    @Override public Node getComponent() {
        return content;
    }

    @Override public synchronized void append(String text, int type) {
        addInfo(text, type);
        if (text.contains("\n")) {
            List<TextNodeInfo> writeNow = textNodes;
            textNodes = new ArrayList<>();
            Platform.runLater(() -> {
                for (TextNodeInfo textNodeInfo : writeNow) {
                    textFlow.getChildren().add(getTextNode(textNodeInfo.getText().toString(), textNodeInfo.getType()));
                }
                clearButton.setDisable(false);
                exportButton.setDisable(false);
            });
        }
    }

    private void addInfo(String text, int type) {
        if (textNodes.size() == 0) {
            textNodes.add(new TextNodeInfo(text, type));
        } else {
            TextNodeInfo lastNode = textNodes.get(textNodes.size() - 1);
            if (lastNode.getType() == type) {
                lastNode.getText().append(text);
            } else {
                textNodes.add(new TextNodeInfo(text, type));
            }
        }
    }

    @Override public void clear() {
        ObservableList<Node> children = textFlow.getChildren();
        int size = children.size();
        if (size > 0) {
            children.remove(0, size);
        }
        clearButton.setDisable(true);
        exportButton.setDisable(true);
    }

    private void onExport() {
        File file = FXUIUtils.showSaveFileChooser(null, null, null, null);
        if (file != null) {
            try {
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
                stream.write(getText().getBytes());
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Text getTextNode(final String message, final int type) {
        Text text = new Text();
        text.setText(message);
        if (type == IStdOut.STD_OUT) {
            text.setFill(Color.BLUE);
            text.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        } else if (type == IStdOut.STD_ERR) {
            text.setFill(Color.RED);
            text.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        } else if (type == IStdOut.SCRIPT_OUT) {
            text.setFill(Color.BLUE);
            text.setFont(Font.font("Verdana", FontPosture.ITALIC, 12));
        } else if (type == IStdOut.SCRIPT_ERR) {
            text.setFill(Color.RED);
            text.setFont(Font.font("Verdana", FontPosture.ITALIC, 12));
        }
        return text;
    }

    @Override public String getText() {
        ObservableList<Node> children = textFlow.getChildren();
        StringBuilder textBuilder = new StringBuilder();
        int size = children.size();
        if (size > 0) {
            for (Node node : children) {
                Text textNode = (Text) node;
                textBuilder.append(textNode.getText());
            }
        }
        return textBuilder.toString();
    }

    @Override public DockKey getDockKey() {
        return DOCK_KEY;
    }
}
