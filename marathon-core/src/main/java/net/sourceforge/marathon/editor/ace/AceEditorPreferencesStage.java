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
package net.sourceforge.marathon.editor.ace;

import java.util.List;
import java.util.logging.Logger;

import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ICancelHandler;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.projectselection.FormPane;
import net.sourceforge.marathon.runtime.api.Preferences;

public class AceEditorPreferencesStage extends ModalDialog<AceEditorPreferencesInfo> implements ICancelHandler {

    public static final Logger LOGGER = Logger.getLogger(AceEditorPreferencesStage.class.getName());

    private AceEditorPreferencesInfo preferenceInfo;
    private IAceEditorPreferenceHandler preferenceHandler;

    private Button okButton = FXUIUtils.createButton("ok", "OK", true, "OK");
    private Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
    private Button defaultsButton = FXUIUtils.createButton("Save as Default", "Save these settings for all editors", true,
            "Save as Default");
    private ButtonBar buttonBar = new ButtonBar();
    private ComboBox<AceEditorTheme> themesCombo;
    private ComboBox<String> kbHandlerCombo;
    private AceEditorTheme previousTheme;
    private String previousKeyboardHandler;
    private int previousTabSize;
    private boolean previousTabConversion;
    private Spinner<Integer> tabSizeSpinner;
    private Spinner<String> fontSizeSpinner;
    private CheckBox tabConversionCheckBox;
    private CheckBox showLineNumbersCheckBox;
    private boolean previousShowLineNumbers;
    private CheckBox showInvisiblesCheckBox;
    private boolean previousShowInvisibles;
    private String previousFontSize;

    public AceEditorPreferencesStage(AceEditorPreferencesInfo preferenceInfo) {
        super("Editor Preferences", "Change editor settings like themes etc.", FXUIUtils.getIcon("settings"));
        this.preferenceInfo = preferenceInfo;
        previousTheme = preferenceInfo.getSelectedTheme();
        previousKeyboardHandler = preferenceInfo.getKeyboardHandler();
        previousTabSize = preferenceInfo.getTabSize();
        previousTabConversion = preferenceInfo.getTabConversion();
        previousShowLineNumbers = preferenceInfo.getShowLineNumbers();
        previousShowInvisibles = preferenceInfo.getShowInvisibles();
        previousFontSize = preferenceInfo.getFontSize();
        setCancelHandler(this);
        initComponents();
    }

    @Override protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    @Override protected Parent getContentPane() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("ace-editor-preferences-stage");
        root.setId("PreferencesStage");

        FormPane formPane = new FormPane("preferences-stage-form", 2);
        themesCombo = new ComboBox<>();
        List<AceEditorTheme> themes = preferenceInfo.getThemes();
        themesCombo.getItems().addAll(themes);
        themesCombo.getSelectionModel().select(previousTheme);
        themesCombo.valueProperty().addListener((event, o, n) -> preferenceHandler.changeTheme(n));

        kbHandlerCombo = new ComboBox<>();
        kbHandlerCombo.getItems().addAll("ace", "emacs", "vim");
        kbHandlerCombo.getSelectionModel().select(previousKeyboardHandler);
        kbHandlerCombo.valueProperty().addListener((event, o, n) -> preferenceHandler.changeKeyboardHandler(n));

        tabSizeSpinner = new Spinner<Integer>(FXCollections.observableArrayList(2, 4, 8));
        tabSizeSpinner.getValueFactory().setValue(previousTabSize);
        tabSizeSpinner.valueProperty().addListener((event, o, n) -> preferenceHandler.changeTabSize(n));

        fontSizeSpinner = new Spinner<String>(
                FXCollections.observableArrayList("10px", "11px", "12px", "13px", "14px", "16px", "18px", "20px", "22px", "24px"));
        fontSizeSpinner.getValueFactory().setValue(previousFontSize);
        fontSizeSpinner.valueProperty().addListener((event, o, n) -> preferenceHandler.changeFontSize(n));

        tabConversionCheckBox = new CheckBox("Convert tabs to spaces");
        tabConversionCheckBox.setSelected(previousTabConversion);
        tabConversionCheckBox.selectedProperty().addListener((event, o, n) -> preferenceHandler.changeTabConversion(n));

        showLineNumbersCheckBox = new CheckBox("Show line numbers");
        showLineNumbersCheckBox.setSelected(previousShowLineNumbers);
        showLineNumbersCheckBox.selectedProperty().addListener((event, o, n) -> preferenceHandler.changeShowLineNumbers(n));

        showInvisiblesCheckBox = new CheckBox("Show Whitespace");
        showInvisiblesCheckBox.setSelected(previousShowInvisibles);
        showInvisiblesCheckBox.selectedProperty().addListener((event, o, n) -> preferenceHandler.changeShowInvisibles(n));

        // @formatter:off
        formPane.addFormField("Theme", themesCombo)
                .addFormField("Keyboard", kbHandlerCombo)
                .addFormField("Font Size", fontSizeSpinner)
                .addFormField("Tab Size", tabSizeSpinner)
                .addFormField("", tabConversionCheckBox)
                .addFormField("", showLineNumbersCheckBox)
                .addFormField("", showInvisiblesCheckBox);
        // @formatter:on

        root.setCenter(formPane);
        root.setBottom(buttonBar);
        return root;
    }

    private void initComponents() {
        okButton.setOnAction((e) -> onOk());
        cancelButton.setOnAction((e) -> onCancel());
        defaultsButton.setOnAction((e) -> onDefault());
        buttonBar = new ButtonBar() {
            @Override protected double computePrefWidth(double height) {
                return super.computePrefWidth(height) + 200;
            }
        };
        buttonBar.setButtonMinWidth(Region.USE_PREF_SIZE);
        buttonBar.getButtons().addAll(okButton, cancelButton, defaultsButton);
    }

    private void onOk() {
        dispose();
    }

    protected void onCancel() {
        preferenceHandler.changeTheme(previousTheme);
        preferenceHandler.changeKeyboardHandler(previousKeyboardHandler);
        preferenceHandler.changeTabSize(previousTabSize);
        preferenceHandler.changeFontSize(previousFontSize);
        preferenceHandler.changeTabConversion(previousTabConversion);
        preferenceHandler.changeShowLineNumbers(previousShowLineNumbers);
        preferenceHandler.changeShowInvisibles(previousShowInvisibles);
        dispose();
    }

    private void onDefault() {
        Preferences preferences = Preferences.instance();
        JSONObject editorPreferences = preferences.getSection("ace-editor");
        editorPreferences.put("theme", themesCombo.valueProperty().getValue().getTheme());
        editorPreferences.put("keyboard-handler", kbHandlerCombo.valueProperty().getValue());
        editorPreferences.put("tabSize", tabSizeSpinner.getValue());
        editorPreferences.put("tabConversion", tabConversionCheckBox.isSelected());
        editorPreferences.put("showLineNumbers", showLineNumbersCheckBox.isSelected());
        editorPreferences.put("showInvisibles", showInvisiblesCheckBox.isSelected());
        editorPreferences.put("fontSize", fontSizeSpinner.getValue());
        preferences.save("ace-editor");
        dispose();
    }

    public void setPreferenceHandler(IAceEditorPreferenceHandler preferenceHandler) {
        this.preferenceHandler = preferenceHandler;
    }

    @Override protected void setDefaultButton() {
        okButton.setDefaultButton(true);
    }

    @Override public void handleCancel() {
        onCancel();
    }
}
