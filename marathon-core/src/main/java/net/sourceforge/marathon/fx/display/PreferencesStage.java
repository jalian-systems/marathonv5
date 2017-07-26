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

import org.json.JSONObject;

import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.projectselection.FormPane;
import net.sourceforge.marathon.junit.TestCreator;
import net.sourceforge.marathon.runtime.api.Constants;

public class PreferencesStage extends ModalDialog<MarathonPreferencesInfo> {

    public static final Logger LOGGER = Logger.getLogger(PreferencesStage.class.getName());

    private MarathonPreferencesInfo preferenceInfo;
    private IPreferenceHandler preferenceHandler;

    private TextField mouseTriggerField = new TextField();
    private Button mouseTriggerButton = new Button("Click here");
    private TextField keyTriggerField = new TextField("Ctrl+F8");
    private CheckBox doNotHideMarathonITEBlurbs = new CheckBox("");
    private Button okButton = FXUIUtils.createButton("ok", "OK", true, "OK");
    private Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
    private Button defaultsButton = FXUIUtils.createButton("loaddefaults", "Load Defaults", true, "Load Defaults");
    private ButtonBar buttonBar = new ButtonBar();
    private JSONObject prefs;

    public PreferencesStage(MarathonPreferencesInfo preferenceInfo) {
        super("Preferences", "Set marathon preferences", FXUIUtils.getIcon("preferences"));
        this.preferenceInfo = preferenceInfo;
        this.prefs = preferenceInfo.getPreferences();
        initComponents();
    }

    @Override protected Parent getContentPane() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("preferences-stage");
        root.setId("PreferencesStage");

        FormPane formPane = new FormPane("preferences-stage-form", 3);
        // @formatter:off
        formPane.addFormField("Mouse Trigger: ", mouseTriggerField, mouseTriggerButton)
                .addFormField("Keyboard Trigger: ", keyTriggerField)
                .addFormField("Hide MarathonITE options from view: ", doNotHideMarathonITEBlurbs);
        // @formatter:on

        root.setCenter(formPane);
        root.setBottom(buttonBar);
        return root;
    }

    @Override protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    private void initComponents() {

        mouseTriggerField.setEditable(false);
        mouseTriggerField.setText(FXContextMenuTriggers.getContextMenuModifiers());
        mouseTriggerButton.setMinWidth(Region.USE_PREF_SIZE);
        mouseTriggerButton.setOnMousePressed((e) -> {
            mouseTriggerField.setText(OSFXUtils.mouseEventGetModifiersExText(e));
        });
        if (FXContextMenuTriggers.getContextMenuKeyModifiers().equals("")) {
            keyTriggerField.setText(FXContextMenuTriggers.getContextMenuKeyCode());
        } else {
            keyTriggerField.setText(
                    FXContextMenuTriggers.getContextMenuKeyModifiers() + "+" + FXContextMenuTriggers.getContextMenuKeyCode());
        }
        keyTriggerField.setEditable(false);
        keyTriggerField.addEventHandler(KeyEvent.KEY_PRESSED, (pressedEvent) -> onKeyPressed(pressedEvent));

        doNotHideMarathonITEBlurbs.setTooltip(new Tooltip("Hide the MarathonITE shameless plug"));
        doNotHideMarathonITEBlurbs.setSelected(Boolean.parseBoolean(prefs.optString(Constants.PREF_ITE_BLURBS, "false")));
        doNotHideMarathonITEBlurbs.setOnAction((e) -> {
            FXUIUtils.showMessageDialog(getStage(), "Restart Marathon for this option to take effect", "Information",
                    AlertType.INFORMATION);
        });

        okButton.setOnAction((e) -> onOk());
        cancelButton.setOnAction((e) -> onCancel());
        defaultsButton.setOnAction((e) -> onDefault());

        buttonBar.setButtonMinWidth(Region.USE_PREF_SIZE);
        buttonBar.getButtons().addAll(okButton, cancelButton, defaultsButton);
    }

    protected void onKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.SHIFT || e.getCode() == KeyCode.ALT
                || e.getCode() == KeyCode.META) {
            return;
        }
        String keyText = OSFXUtils.isModifiers(e) ? OSFXUtils.ketEventGetModifiersExText(e) + "+" : "";
        keyText += OSFXUtils.keyEventGetKeyText(e.getCode());
        keyTriggerField.setText(keyText);
    }

    private void onOk() {
        preferenceInfo.setMouseTriggerText(mouseTriggerField.getText());
        preferenceInfo.setKeyTriggerText(keyTriggerField.getText());
        preferenceInfo.setHideBlurb(doNotHideMarathonITEBlurbs.isSelected());
        if (preferenceHandler != null) {
            preferenceHandler.setPreferences(preferenceInfo);
        }
        dispose();
    }

    protected void onCancel() {
        preferenceInfo.setNeedRefresh(false);
        dispose();
    }

    private void onDefault() {
        mouseTriggerField.setText(OSFXUtils.MOUSE_MENU_MASK + "+Button3");
        keyTriggerField.setText(OSFXUtils.MOUSE_MENU_MASK + "+F8");
        TestCreator.setHideFilePattern(null);
        doNotHideMarathonITEBlurbs.setSelected(false);
    }

    public void setPreferenceHandler(IPreferenceHandler preferenceHandler) {
        this.preferenceHandler = preferenceHandler;
    }

    @Override protected void setDefaultButton() {
        okButton.setDefaultButton(true);
    }
}
