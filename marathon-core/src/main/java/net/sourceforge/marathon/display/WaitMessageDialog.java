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
package net.sourceforge.marathon.display;

import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import net.sourceforge.marathon.fx.api.FXUIUtils;

public class WaitMessageDialog {

    public static final Logger LOGGER = Logger.getLogger(WaitMessageDialog.class.getName());

    private static final String DEFAULT_MESSAGE = "This window closes once Marathon is ready for recording";
    private static MessageDialog _instance;

    private static class MessageDialog extends Stage {
        private String message = DEFAULT_MESSAGE;
        private Label messageLabel;
        private static final Integer STARTTIME = 0;
        private Timeline timeline;
        private Label timerLabel = new Label();
        private IntegerProperty timeSeconds = new SimpleIntegerProperty(STARTTIME);

        private MessageDialog() {
            setAlwaysOnTop(true);
            initStyle(StageStyle.UNDECORATED);
            initComponents();
        }

        private void initComponents() {
            messageLabel = new Label(message);
            messageLabel.setAlignment(Pos.CENTER);
            messageLabel.setStyle("-fx-background-color:#000000");
            messageLabel.setTextFill(javafx.scene.paint.Color.WHITE);
            messageLabel.setMaxWidth(Double.MAX_VALUE);

            // Bind the timerLabel text property to the timeSeconds property
            timerLabel.setStyle("-fx-font-size: 2em");
            timerLabel.textProperty().bind(timeSeconds.asString());
            timerLabel.setTextFill(Color.RED);

            VBox vbox = new VBox(FXUIUtils.getImage("wait"), messageLabel);

            StackPane.setMargin(timerLabel, new Insets(90, 0, 0, 0));
            StackPane root = new StackPane(vbox, timerLabel);
            setScene(new Scene(root));
        }

        public void setMessage(String message) {
            if (message.equals(this.message)) {
                return;
            }
            this.message = message;
            Platform.runLater(() -> messageLabel.setText(message));
        }

        public void _show() {
            timeSeconds.set(STARTTIME);
            timeline = new Timeline();
            KeyValue keyValue = new KeyValue(timeSeconds, Integer.MAX_VALUE);
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(Integer.MAX_VALUE), keyValue));
            timeline.playFromStart();
            show();
        }

        public void _hide() {
            if (timeline != null) {
                timeline.stop();
            }
            hide();
        }

    }

    public static void setVisible(boolean b, String message) {
        if (DisplayWindow.instance() == null) {
            if (b)
                System.out.println(message);
            return;
        }
        Runnable r = () -> {
            if (_instance == null) {
                _instance = new MessageDialog();
            }
            if (_instance.isShowing() != b) {
                if (b) {
                    _instance._show();
                } else {
                    _instance._hide();
                }
            }
            _instance.setMessage(message);
        };
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }

    public static void setVisible(boolean b) {
        setVisible(b, DEFAULT_MESSAGE);
    }

}
