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
package net.sourceforge.marathon.runtime;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.output.WriterOutputStream;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.api.ITestApplication;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IRuntimeLauncherModel;
import net.sourceforge.marathon.runtime.api.ITestLauncher;
import net.sourceforge.marathon.runtime.api.MPFUtils;
import net.sourceforge.marathon.util.LauncherModelHelper;

public class TestApplication extends Stage implements ITestApplication {

    public static final Logger LOGGER = Logger.getLogger(TestApplication.TextAreaWriter.class.getName());

    private final static class TextAreaWriter extends Writer {
        private TextArea textArea;

        public TextAreaWriter(TextArea area) {
            textArea = area;
        }

        @Override public void write(char[] cbuf, int off, int len) throws IOException {
            final String newText = new String(cbuf, off, len);
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    textArea.setText(textArea.getText() + newText);
                }
            });
        }

        @Override public void close() throws IOException {
        }

        @Override public void flush() throws IOException {
        }
    }

    private ITestLauncher launchCommand;
    private TextArea commandField = new TextArea();
    private TextArea outputArea = new TextArea();
    private TextArea errorArea = new TextArea();
    private Button closeButton = FXUIUtils.createButton("cancel", "Close", true, "Close");

    public TestApplication(Stage parent, Properties props) {
        initOwner(parent);
        MPFUtils.replaceEnviron(props);
        String model = props.getProperty(Constants.PROP_PROJECT_LAUNCHER_MODEL);
        if (model == null || model.equals("")) {
            commandField.setText("Select a launcher and set the parameters required.");
            show();
        } else {
            IRuntimeLauncherModel launcherModel = LauncherModelHelper.getLauncherModel(model);
            launchCommand = launcherModel.createLauncher(props);
        }
        initModality(Modality.APPLICATION_MODAL);
        BorderPane content = new BorderPane();
        VBox builder = new VBox();
        builder.setSpacing(5);
        outputArea.setEditable(false);
        errorArea.setEditable(false);
        VBox.setVgrow(errorArea, Priority.ALWAYS);
        builder.getChildren().addAll(new Label("Command"), commandField, new Label("Standard Output & Error"), outputArea,
                new Label("Message"), errorArea);
        closeButton.setOnAction((event) -> {
            if (launchCommand != null) {
                launchCommand.destroy();
            }
            close();
        });
        content.setCenter(builder);
        content.setBottom(new HBox(5, FXUIUtils.createFiller(), closeButton));
        setScene(new Scene(content));
    }

    @Override public void launch() throws IOException, InterruptedException {
        if (launchCommand == null) {
            commandField.setText("This launcher does not support launch in test mode.");
            showAndWait();
            return;
        }
        launchCommand.copyOutputTo(new WriterOutputStream(new TextAreaWriter(outputArea), Charset.defaultCharset()));
        launchCommand.setMessageArea(new WriterOutputStream(new TextAreaWriter(errorArea), Charset.defaultCharset()));
        if (launchCommand.start() == ITestLauncher.OK_OPTION) {
            commandField.setText(launchCommand.toString());
            showAndWait();
        }
    }

}
