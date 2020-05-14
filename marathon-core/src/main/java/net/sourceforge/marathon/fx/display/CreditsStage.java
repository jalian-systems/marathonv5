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

import java.util.logging.Logger;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.api.ButtonBarX;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;

public class CreditsStage extends ModalDialog<String> {

    public static final Logger LOGGER = Logger.getLogger(CreditsStage.class.getName());

    private WebView webView = new WebView();
    private Button okButton = FXUIUtils.createButton("ok", "OK", true, "OK");
    private ButtonBarX buttonBar = new ButtonBarX();

    public CreditsStage() {
        super("Credits", "Without the following projects, Marathon would not have been possible", FXUIUtils.getIcon("credits"));
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

    @Override
    protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    @Override
    public Parent getContentPane() {
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
                            "<tr bgcolor=\"#c3d9ff\">" + "<th>Project</th>" + "<th>Blurb</th>" + "</tr>";

        String suffix =      "</table></p>" +
                        "</body></html>";
        // @formatter:on
        StringBuffer content = new StringBuffer(prefix);
        content.append(getCredit("Selenium", "Selenium automates browsers. We added the Java application support to it.",
                "http://www.seleniumhq.org"));
        content.append(getCredit("JRuby", "The Ruby Programming Language on the JVM", "http://jruby.org"));
        content.append(getCredit("JUnit", "The original Java unit testing framework", "http://www.junit.org"));
        content.append(getCredit("Javassist",
                "Java bytecode engineering toolkit, allows us to include execute_script and execute_async_script for Java drivers.",
                "http://jboss-javassist.github.io/javassist/"));
        content.append(getCredit("JLine", "JLine is a Java library for handling console input. ScriptConsole.. enuf said.",
                "http://jline.sourceforge.net"));
        content.append(getCredit("opencsv", "Opencsv is a very simple csv (comma-separated values) parser library for Java. ",
                "http://opencsv.sourceforge.net"));
        content.append(getCredit("Gradle", "A powerful build system for the JVM.", "https://gradle.org"));
        content.append(getCredit("SnakeYAML", "SnakeYAML is a YAML processor for the Java Virtual Machine.",
                "https://bitbucket.org/asomov/snakeyaml"));
        content.append(getCredit("TestNG",
                "TestNG is a testing framework inspired from JUnit and NUnit but introducing some new functionalities that make it more powerful and easier to use,",
                "http://testng.org/doc/index.html"));
        content.append(getCredit("Allure",
                "Allure - an open-source framework designed to create test execution reports clear to everyone in the team.",
                "http://allure.qatools.ru"));
        content.append(getCredit("Java-WebSocket", "A barebones WebSocket client and server implementation written in 100% Java.",
                "https://github.com/TooTallNate/Java-WebSocket"));
        content.append(
                getCredit("NanoHttpd", "Tiny, easily embeddable HTTP server in Java.", "https://github.com/NanoHttpd/nanohttpd"));
        content.append(getCredit("Commons IO", "Commons IO is a library of utilities to assist with developing IO functionality.",
                "http://commons.apache.org/proper/commons-io/"));
        content.append(getCredit("Eclipse",
                "Great platform to work with.<br>The navigator and junit interfaces as well as most of the icons are picked up from eclipse package. Hopefully, we will have Marathon as eclipse package sometime",
                "http://eclipse.org"));
        content.append(getCredit("Guice",
                "Guice (pronounced 'juice') is a lightweight dependency injection framework for Java 6 and above, which allows us to use the same code base for both Marathon and MarathonITE",
                "https://github.com/google/guice"));
        content.append(
                getCredit("Ace", "The high performance code editor for the web, used in Marathon for displaying text content.",
                        "https://ace.c9.io/#nav=about"));

        content.append(suffix);
        return content.toString();
    }

    private String getCredit(String name, String blurb, String website) {
        String string = "<tr>" + "<td width=\"280px\" valign=\"center\" style=\"font-size:15px\" nowrap><a href=\"" + website
                + "\">" + name + "</a>" + "<td style=\"font-size:15px\" valign=\"center\" >" + blurb + "</td>" + "</tr>";
        return string;
    }

    protected void onOK() {
        dispose();
    }

    @Override
    protected void setDefaultButton() {
        okButton.setDefaultButton(true);
    }

}
