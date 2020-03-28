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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.api.ButtonBarX;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;

public class BrowserConfigurationStage extends ModalDialog<List<Browser>> {

    public static final Logger LOGGER = Logger.getLogger(BrowserConfigurationStage.class.getName());

    private ButtonBarX buttonBar = new ButtonBarX();
    private Button okButton = FXUIUtils.createButton("ok", "OK", true, "OK");
    private Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");

    private List<Browser> browsers;

    private TabPane browserTabs;

    public BrowserConfigurationStage(List<Browser> browsers) throws FileNotFoundException, IOException {
        super("Configure Browsers", "Set the capabilities and options for WebDrivers", FXUIUtils.getIcon("web"));
        this.browsers = browsers;
        initComponents();
    }

    private void initComponents() {
        buttonBar.getButtons().addAll(okButton, cancelButton);
        okButton.setOnAction((e) -> onSave());
        cancelButton.setOnAction((e) -> dispose());
    }

    private void onSave() {
        browserTabs.getTabs().stream().forEach((tab) -> ((BrowserTab) tab).save());
        dispose();
    }

    @Override
    protected Parent getContentPane() {
        BorderPane borderPane = new BorderPane();
        browserTabs = new TabPane();
        browserTabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        for (Browser browser : browsers) {
            IWebBrowserProxy proxy = getProxyInstance(browser);
            browserTabs.getTabs().add((Tab) proxy.getTab(browser.getBrowserName()));
        }
        borderPane.setCenter(browserTabs);
        borderPane.setBottom(buttonBar);
        return borderPane;

    }

    public IWebBrowserProxy getProxyInstance(Browser browser) {
        try {
            return (IWebBrowserProxy) Class.forName(browser.getProxy()).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    protected void initialize(Stage stage) {
        super.initialize(stage);
    }

    @Override
    protected void setDefaultButton() {
    }

}
