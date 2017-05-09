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

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.sourceforge.marathon.api.GuiceInjector;
import net.sourceforge.marathon.display.GlobalResourceChangeListener;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.projectselection.FormPane;
import net.sourceforge.marathon.resource.IResourceActionSource;
import net.sourceforge.marathon.resource.navigator.FileResource;
import net.sourceforge.marathon.runtime.api.Constants;
import ru.yandex.qatools.allure.model.SeverityLevel;

public class AddPropertiesView extends BorderPane implements IResourceActionSource {

    public static final Logger LOGGER = Logger.getLogger(AddPropertiesView.class.getName());

    private VBox content = new VBox();
    private FormPane formPane = new FormPane("properites-stage-form", 4);
    private TextField nameField = new TextField();
    private Hyperlink tmsLink = new Hyperlink("");
    private TextArea descriptionField = new TextArea();
    private TextField idField = new TextField();
    private ComboBox<String> severities = new ComboBox<>();
    private IGroupTabPane groupsPane;
    private TestPropertiesInfo issueInfo;

    public AddPropertiesView(TestPropertiesInfo issueInfo) {
        this.issueInfo = issueInfo;
        initComponents();
     // @formatter:off
        Label severityLabel = new Label("Severity: ");
        severityLabel.setMinWidth(Region.USE_PREF_SIZE);
        tmsLink.setOnAction((e) -> {
            try {
                Desktop.getDesktop().browse(new URI(tmsLink.getText()));
            } catch (Exception e1) {
                FXUIUtils._showMessageDialog(null, "Unable to open link: " + tmsLink.getText(), "Unable to open link",
                        AlertType.ERROR);
                e1.printStackTrace();
            }
        });
        formPane.addFormField("Name: ", nameField)
                .addFormField("Description: ", descriptionField)
                .addFormField("ID: ", idField, severityLabel, severities);
        String tmsPattern = System.getProperty(Constants.PROP_TMS_PATTERN);
        if (tmsPattern != null && tmsPattern.length() > 0) {
            tmsLink.textProperty().bind(Bindings.format(tmsPattern, idField.textProperty()));
            formPane.addFormField("", tmsLink);
        }
        // @formatter:on
        setCenter(content);
    }

    private void initComponents() {
        groupsPane = GuiceInjector.get().getInstance(IGroupTabPane.class);
        if (groupsPane != null) {
            groupsPane.initialize(issueInfo);
        }
        nameField.setText(issueInfo.getRawName());
        descriptionField.setPrefRowCount(2);
        String description = issueInfo.getDescription();
        if (description != null) {
            descriptionField.setText(description);
        }
        String id = issueInfo.getId();
        if (id != null) {
            idField.setText(id);
        }
        severities.setItems(getSeverity());
        String severity = issueInfo.getSeverity();
        if (severity != null) {
            severities.getSelectionModel().select(severity);
        } else {
            severities.getSelectionModel().select("normal");
        }
        if (groupsPane != null) {
            content.getChildren().addAll(formPane, (Node) groupsPane);
        } else {
            content.getChildren().addAll(formPane);
        }
    }

    private ObservableList<String> getSeverity() {
        ObservableList<String> severities = FXCollections.observableArrayList();
        for (SeverityLevel severityLevel : SeverityLevel.values()) {
            severities.add(severityLevel.value());
        }
        return severities;
    }

    private void setInfo() {
        issueInfo.setRawName(nameField.getText());
        issueInfo.setDescription(descriptionField.getText());
        issueInfo.setId(idField.getText());
        issueInfo.setSeverity(severities.getSelectionModel().getSelectedItem());
    }

    public void write(File file) {
        setInfo();
        if (file == null) {
            file = issueInfo.save();
        } else {
            file = issueInfo.save(file);
        }
        GlobalResourceChangeListener.get().updated(this, new FileResource(file));
        if (groupsPane != null) {
            groupsPane.updateGroups(this, file.toPath());
        }
    }
}
