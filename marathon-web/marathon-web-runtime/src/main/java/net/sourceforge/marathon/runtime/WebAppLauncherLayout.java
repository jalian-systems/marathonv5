/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * All Rights Reserved.
 ******************************************************************************/
package net.sourceforge.marathon.runtime;

import java.util.Properties;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.projectselection.FormPane;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.fx.api.ISubPropertiesLayout;

public class WebAppLauncherLayout implements ISubPropertiesLayout {

    public static final Logger LOGGER = Logger.getLogger(WebAppLauncherLayout.class.getName());

    public static final String AUT_WEBAPP_URL_PREFIX = "marathon.webapp.url.prefix";
    public static final String AUT_WEBAPP_WIDTH = "marathon.webapp.width";
    public static final String AUT_WEBAPP_HIEGHT = "marathon.webapp.hieght";
    public static final String AUT_WEBAPP_URL_PATH = "marathon.webapp.url.path";
    private TextField urlPrefixField = new TextField();
    private TextField urlPathField = new TextField();
    private TextField widthField = new TextField();
    private TextField hieghtField = new TextField();
    private ComboBox<Browser> browsersField = new ComboBox<>(Browser.getBrowsers());
    private WebView help = new WebView();

    public WebAppLauncherLayout(ModalDialog<?> parent) {
    }

    @Override
    public Node getContent() {
        VBox box = new VBox();
        FormPane form = new FormPane("web-app-launcher-layout", 2);
        //@formatter:off
        form.addFormField("URL (Prefix): ", urlPrefixField)
            .addFormField("Start Path:" , urlPathField)
            .addFormField("Default Browser", browsersField)
            .addFormField("Initial Window Size:", createSizePane());
        help.getEngine().load(this.getClass().getResource("/BrowsersHelp/index.html").toExternalForm());
        help.setPrefSize(800, 300);
        VBox.setVgrow(help, Priority.ALWAYS);
        HBox.setHgrow(help, Priority.ALWAYS);
        box.getChildren().addAll(form, help);
        //@formatter:on
        Platform.runLater(() -> help.getEngine().reload());
        return box;
    }

    private Node createSizePane() {
        HBox sizeBox = new HBox(10);
        widthField.setTextFormatter(new TextFormatter<Integer>(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                if (object == null)
                    return "1024";
                return Integer.toString(object.intValue());
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (Throwable t) {
                    return 1024;
                }
            }
        }));
        hieghtField.setTextFormatter(new TextFormatter<Integer>(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                if (object == null)
                    return "768";
                return Integer.toString(object.intValue());
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (Throwable t) {
                    return 768;
                }
            }
        }));
        Button rotate = FXUIUtils.createButton("rotate", "Transpose width and hieght");
        rotate.setOnAction((event) -> {
            String width = widthField.getText();
            widthField.setText(hieghtField.getText());
            hieghtField.setText(width);
        });
        ComboBox<String> presets = new ComboBox<String>(FXCollections.observableArrayList("320X480", "360X640", "1024X768",
                "1280X800", "1280X600", "1280X980", "1920X1080"));
        presets.setOnAction((event) -> {
            String preset = presets.getSelectionModel().getSelectedItem();
            if (preset != null) {
                String[] split = preset.split("X");
                widthField.setText(split[0]);
                hieghtField.setText(split[1]);
            }
        });
        sizeBox.getChildren().addAll(widthField, new Text("X"), hieghtField, rotate, presets);
        return sizeBox;
    }

    @Override
    public String getName() {
        return "Web Application Launcher";
    }

    @Override
    public ImageView getIcon() {
        return null;
    }

    @Override
    public void getProperties(Properties props) {
        props.setProperty(AUT_WEBAPP_URL_PREFIX, urlPrefixField.getText());
        if (urlPathField.getText() != null) {
            props.setProperty(AUT_WEBAPP_URL_PATH, urlPathField.getText());
        } else {
            props.setProperty(AUT_WEBAPP_URL_PATH, "");
        }
        props.setProperty(Constants.AUT_WEBAPP_DEFAULT_BROWSER, browsersField.getSelectionModel().getSelectedItem().getProxy());
        props.setProperty(AUT_WEBAPP_WIDTH, widthField.getText());
        props.setProperty(AUT_WEBAPP_HIEGHT, hieghtField.getText());
    }

    @Override
    public void setProperties(Properties props) {
        urlPrefixField.setText(props.getProperty(AUT_WEBAPP_URL_PREFIX));
        urlPathField.setText(props.getProperty(AUT_WEBAPP_URL_PATH));
        browsersField.getSelectionModel().select(Browser.find(props.getProperty(Constants.AUT_WEBAPP_DEFAULT_BROWSER)));
        widthField.setText(props.getProperty(AUT_WEBAPP_WIDTH, "1024"));
        hieghtField.setText(props.getProperty(AUT_WEBAPP_HIEGHT, "768"));
    }

    @Override
    public boolean isValidInput(boolean showAlert) {
        if (urlPrefixField.getText() == null || urlPrefixField.getText().trim().length() == 0) {
            if (showAlert) {
                FXUIUtils.showMessageDialog(null, "URL can't be empty", "Launcher", AlertType.ERROR);
                Platform.runLater(() -> urlPrefixField.requestFocus());
            }
            return false;
        }
        if (browsersField.getSelectionModel().getSelectedIndex() == -1) {
            if (showAlert) {
                FXUIUtils.showMessageDialog(null, "A default browser should be selected", "Launcher", AlertType.ERROR);
                Platform.runLater(() -> browsersField.requestFocus());
            }
            return false;
        }
        if (!validInteger(widthField.getText())) {
            if (showAlert) {
                FXUIUtils.showMessageDialog(null, "Invalid value for width field", "Launcher", AlertType.ERROR);
                Platform.runLater(() -> widthField.requestFocus());
            }
            return false;
        }
        if (!validInteger(hieghtField.getText())) {
            if (showAlert) {
                FXUIUtils.showMessageDialog(null, "Invalid value for hieght field", "Launcher", AlertType.ERROR);
                Platform.runLater(() -> hieghtField.requestFocus());
            }
            return false;
        }
        return true;
    }

    private boolean validInteger(String text) {
        if (text != null) {
            try {
                Integer.parseInt(text);
                return true;
            } catch (Throwable t) {

            }
        }
        return false;
    }

}
