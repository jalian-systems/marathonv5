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
package net.sourceforge.marathon.ruby;

import java.io.File;
import java.util.Optional;
import java.util.Properties;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.runtime.fx.api.ISubPropertiesLayout;
import net.sourceforge.marathon.runtime.fx.api.ListLayout;

public class RubyPathLayout extends ListLayout implements ISubPropertiesLayout {

    public static final String PROP_APPLICATION_RUBYPATH = "marathon.application.rubypath";
    public static final String PROP_APPLICATION_RUBYHOME = "marathon.application.rubyhome";
    private TextField rubyHomeField;

    public RubyPathLayout(ModalDialog<?> parent) {
        super(parent);
    }

    @Override public Node getContent() {
        Node content = super.getContent();
        listViewBox.getChildren().add(createRubyHomeField());
        return content;
    }

    private HBox createRubyHomeField() {
        HBox rubyHomeBox = new HBox(5);
        rubyHomeField = new TextField();
        rubyHomeField.setId("RubyHomeField");
        rubyHomeField.setPromptText("(Bundled JRuby)");
        Label label = createLabel("Ruby Home: ");
        label.setId("RubyLabel");
        label.setMinWidth(Region.USE_PREF_SIZE);
        rubyHomeBox.getChildren().addAll(label, rubyHomeField);
        HBox.setHgrow(rubyHomeField, Priority.ALWAYS);
        return rubyHomeBox;
    }

    private Label createLabel(String labelText) {
        Label label = new Label(labelText);
        return label;
    }

    @Override public String getName() {
        return "Ruby Path";
    }

    @Override public Node getIcon() {
        return FXUIUtils.getIcon("cp_obj");
    }

    @Override public boolean isValidInput(boolean showAlert) {
        if (rubyHomeField.getText().equals("")) {
            return true;
        }
        File lib = new File(rubyHomeField.getText(), "lib");
        File jar;
        if (lib.exists()) {
            jar = new File(lib, "jruby.jar");
            if (jar.exists()) {
                return true;
            }
        }
        if (showAlert) {
            Optional<ButtonType> r = FXUIUtils.showConfirmDialog(parent.getStage(),
                    "Could not find jruby.jar in give Home/lib directory. Do you want to continue?", "JRuby Home",
                    AlertType.CONFIRMATION, ButtonType.YES, ButtonType.NO);
            if (r.get() != ButtonType.YES) {
                Platform.runLater(() -> rubyHomeField.requestFocus());
                return false;
            }
        }
        return true;
    }

    @Override public boolean isAddArchivesNeeded() {
        return false;
    }

    @Override public boolean isAddFoldersNeeded() {
        return true;
    }

    @Override public boolean isAddClassesNeeded() {
        return false;
    }

    @Override public boolean isSingleSelection() {
        return true;
    }

    @Override public String getPropertyKey() {
        return PROP_APPLICATION_RUBYPATH;
    }

    @Override public void setProperties(Properties props) {
        super.setProperties(props);
        rubyHomeField.setText(props.getProperty(PROP_APPLICATION_RUBYHOME, ""));
    }

    @Override public void getProperties(Properties props) {
        super.getProperties(props);
        props.setProperty(PROP_APPLICATION_RUBYHOME, rubyHomeField.getText());
    }
}
