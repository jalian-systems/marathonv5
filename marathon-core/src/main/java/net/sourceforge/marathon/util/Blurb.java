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
package net.sourceforge.marathon.util;

import java.net.URL;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.api.ButtonBarX;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;

public abstract class Blurb {

    public static final Logger LOGGER = Logger.getLogger(Blurb.class.getName());

    private URL url;
    private String title;
    private ButtonType selection;
    private boolean cancel;

    private String html;

    public Blurb(StringBuilder html, String title, boolean cancel) {
        this.title = title;
        this.cancel = cancel;
        this.html = html.toString();
        selection = showMessage();
    }

    public Blurb(StringBuilder html, String title) {
        this(html, title, false);
    }

    public Blurb(String marker, String title, boolean cancel) {
        this.url = getClass().getResource(marker + ".html");
        this.title = title;
        this.cancel = cancel;
        selection = showMessage();
    }

    public Blurb(String marker, String title) {
        this(marker, title, false);
    }

    protected ButtonType showDialog() {
        BlurbInfo blurbInfo;
        if (url == null)
            blurbInfo = new BlurbInfo(html, title, cancel);
        else
            blurbInfo = new BlurbInfo(url, title, cancel);
        BlurbStage blurbStage = new BlurbStage(blurbInfo);
        blurbStage.getStage().showAndWait();
        return blurbStage.getSelection();
    }

    public ButtonType getSelection() {
        return selection;
    }

    public ButtonType showMessage() {
        if (Platform.isFxApplicationThread()) {
            return showDialog();
        } else {
            Object[] ret = new Object[1];
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ret[0] = showDialog();
                }
            });
            return (ButtonType) ret[0];
        }
    }

    public static class BlurbStage extends ModalDialog<BlurbInfo> {

        private BlurbInfo blurbInfo;
        private ButtonType selection;
        private WebView webView = new WebView();
        private Button okButton = FXUIUtils.createButton("ok", "OK", true, "OK");
        private Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
        private ButtonBarX buttonBar = new ButtonBarX();

        public BlurbStage(BlurbInfo blurbInfo) {
            super(blurbInfo.getTitle(), null, null);
            this.blurbInfo = blurbInfo;
            initComponents();
        }

        @Override
        protected void initialize(Stage stage) {
            super.initialize(stage);
            stage.initModality(Modality.APPLICATION_MODAL);
        }

        @Override
        protected Parent getContentPane() {
            VBox content = new VBox();
            content.getStyleClass().add("blurb-stage");
            content.setId("blurbStage");
            content.getChildren().addAll(webView, buttonBar);
            return content;
        }

        private void initComponents() {
            webView.setId("webView");
            webView.getEngine().getLoadWorker().stateProperty().addListener(new HyperlinkRedirectListener(webView));
            VBox.setVgrow(webView, Priority.ALWAYS);
            WebEngine engine = webView.getEngine();
            if (blurbInfo.getURL() != null)
                engine.load(blurbInfo.getURL().toExternalForm());
            else
                engine.loadContent(blurbInfo.getHtml());

            buttonBar.setId("buttonBar");
            buttonBar.setButtonMinWidth(Region.USE_PREF_SIZE);
            buttonBar.getButtons().add(okButton);
            if (blurbInfo.isCancelNeeded()) {
                buttonBar.getButtons().add(cancelButton);
            }
            okButton.setOnAction((e) -> onOk());
            cancelButton.setOnAction((e) -> onCancel());
        }

        private void onOk() {
            selection = ButtonType.OK;
            dispose();
        }

        private void onCancel() {
            selection = ButtonType.CANCEL;
            dispose();
        }

        @Override
        protected void setDefaultButton() {
            okButton.setDefaultButton(true);
        }

        public ButtonType getSelection() {
            return selection;
        }
    }
}
