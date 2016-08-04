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

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;

public class CreditsStage extends ModalDialog<String> {

    private WebView webView = new WebView();
    private Button okButton = FXUIUtils.createButton("ok", "OK", true, "OK");
    private ButtonBar buttonBar = new ButtonBar();

    public CreditsStage() {
        super("Credits");
        initComponents();
    }

    private void initComponents() {
        webView.setId("webView");
        WebEngine engine = webView.getEngine();
        engine.loadContent(getWebViewContent(), "text/html");
        VBox.setVgrow(webView, Priority.ALWAYS);

        okButton.setOnAction((e) -> onOK());
        buttonBar.setId("buttonBar");
        buttonBar.getButtons().add(okButton);
    }

    @Override protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    @Override protected Parent getContentPane() {
        VBox content = new VBox();
        content.getStyleClass().add("credits-stage");
        content.setId("creditsStage");
        content.getChildren().addAll(webView, buttonBar);
        return content;
    }

    private String getWebViewContent() {
        // @formatter:off
        String prefix = "<html><body>" +
                            "<center><h1>Credits</h1></center>" +
                            "<p><table width=\"100%\" height=\"60%\" border=\"1\" align=\"center\"  cellspacing=\"0\">" +
                            "<tr bgcolor=\"#c3d9ff\">" + "<th>Package</th>" + "<th>Blurb</th>" + "<th>Web Site</th>" + "</tr>";

        String suffix =      "</table></p>" +
                        "</body></html>";
        // @formatter:on
        StringBuffer content = new StringBuffer(prefix);
        content.append(getCredit("Eclipse",
                "Great platform to work with.<br>The navigator and junit interfaces as well as most of the icons are picked up from eclipse package. Hopefully, we will have Marathon as eclipse package sometime",
                "http://eclipse.org"));
        content.append(getCredit("JRuby",
                "JRuby is an 100% pure-Java implementation of the Ruby programming language.<br>Marathon Ruby scripting model uses JRuby.",
                "http://jruby.codehaus.org"));
        content.append(getCredit("jEdit",
                "Programmer's text editor.<br>Marathon uses jEdit's excellent text area component to provide a comprehensive editing environment.",
                "http://www.jedit.org"));
        content.append(getCredit("Looks and Forms", "The good looks of Marathon are derived from these two packages from jgoodies.",
                "http://www.jgoodies.com"));
        content.append(getCredit("JUnit", "The original Java unit testing framework", "http://www.junit.org"));
        content.append(getCredit("VL Docking",
                "VLDocking Framework is a set of Java Swing Components providing a simple and coherent API to bring docking capabilities to any Swing application. "
                        + "<br>VLDocking Framework (www.vlsolutions.com), is the property of VLSolutions, and is distributed under the terms of its commercial license. It can only be used in the context of the marathon project."
                        + "<br>For any other usage, please refer to the open source (GPL compatible) version available from VLSolutions web site.",
                "http://www.vlsolutions.com"));
        content.append(suffix);
        return content.toString();
    }

    private String getCredit(String name, String blurb, String website) {
        String string = "<tr>" + "<td valign=\"center\" style=\"font-size:15px\" nowrap >" + name + "</td>"
                + "<td width=\"280px\" style=\"font-size:15px\" valign=\"center\" nowrap pre>" + blurb + "</td>"
                + "<td valign=\"center\" style=\"font-size:15px\" nowrap><a href=\"" + website + "\">" + website + "</a>" + "</tr>";
        return string;
    }

    protected void onOK() {
        dispose();
    }

    @Override protected void setDefaultButton() {
        okButton.setDefaultButton(true);
    }

}
