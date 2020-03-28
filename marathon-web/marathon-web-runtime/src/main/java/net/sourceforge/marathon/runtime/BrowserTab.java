/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * All Rights Reserved.
 ******************************************************************************/
package net.sourceforge.marathon.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.ie.InternetExplorerDriverLogLevel;
import org.openqa.selenium.os.ExecutableFinder;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser.ExtensionFilter;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.projectselection.FormPane;
import net.sourceforge.marathon.fx.projectselection.FormPane.ISetConstraints;
import net.sourceforge.marathon.runtime.fx.api.FileSelectionHandler;
import net.sourceforge.marathon.runtime.fx.api.IFileSelectedAction;

public abstract class BrowserTab extends Tab implements IFileSelectedAction {

    private final class VBoxExtension extends VBox implements ISetConstraints {
        private VBoxExtension(double spacing, Node... children) {
            super(spacing, children);
        }

        @Override
        public void setFormConstraints(FormPane form) {
        }
    }

    public static class BrowserPreference {
        private String name;
        private String type;
        private String value;

        public BrowserPreference(String name, String type, String value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    public static class PreferenceTableView extends TableView<BrowserPreference> implements ISetConstraints {
        public PreferenceTableView() {
            getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            setPrefHeight(100);
            setEditable(false);
            TableColumn<BrowserPreference, String> colName = new TableColumn<>("Name");
            colName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colName.prefWidthProperty().bind(widthProperty().multiply(0.5));
            TableColumn<BrowserPreference, String> colType = new TableColumn<>("Type");
            colType.setCellValueFactory(new PropertyValueFactory<>("type"));
            colType.prefWidthProperty().bind(widthProperty().multiply(0.20));
            TableColumn<BrowserPreference, String> colValue = new TableColumn<>("Value");
            colValue.setCellValueFactory(new PropertyValueFactory<>("value"));
            colValue.prefWidthProperty().bind(widthProperty().multiply(0.25));
            getColumns().add(colName);
            getColumns().add(colType);
            getColumns().add(colValue);
        }

        @Override
        public void setFormConstraints(FormPane form) {
        }

        public void removeSelected() {
            getItems().removeAll(getSelectionModel().getSelectedItems());
        }

        public String getText() {
            ObservableList<BrowserPreference> items = getItems();
            if (items.size() == 0)
                return "";
            JSONArray jsonArray = new JSONArray();
            for (BrowserPreference pref : items) {
                jsonArray.put(
                        new JSONObject().put("name", pref.getName()).put("type", pref.getType()).put("value", pref.getValue()));
            }
            return jsonArray.toString(2);
        }

        public void setText(String value) {
            if ("".equals(value) || value == null)
                return;
            ObservableList<BrowserPreference> items = getItems();
            JSONArray array = new JSONArray(value);
            for (int i = 0; i < array.length(); i++) {
                JSONObject p = array.getJSONObject(i);
                items.add(new BrowserPreference(p.getString("name"), p.getString("type"), p.getString("value")));
            }
        }

    }

    public static class FileListView extends ListView<File> implements ISetConstraints {

        public void setPrefRowCount(int n) {
            setPrefHeight(100);
        }

        public void setText(String listOfFiles) {
            ObservableList<File> items = getItems();
            items.clear();
            BufferedReader reader = new BufferedReader(new StringReader(listOfFiles));
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.equals("")) {
                        items.add(new File(line));
                    }
                }
            } catch (IOException e) {
            }
        }

        public String getText() {
            StringBuilder sb = new StringBuilder();
            ObservableList<File> items = getItems();
            for (File file : items) {
                sb.append(file.getAbsolutePath()).append("\n");
            }
            return sb.toString();
        }

        public void removeSelected() {
            if (getSelectionModel().getSelectedIndex() != -1)
                getItems().removeAll(getSelectionModel().getSelectedItems());
        }

        @Override
        public void setFormConstraints(FormPane form) {
            GridPane.setHgrow(this, Priority.ALWAYS);
        }
    }

    private TextField nameField;
    private TextField wdExeField;
    private TextField browserExeField;
    private TextField logFileField;
    private Button browseWDExeButton;
    private Button browseLogFileButton;
    private Button browseExtensionFileButton;
    private Button removeExtensionFileButton;
    private Button browseBrowserExeButton;
    private CheckBox verbose;
    private CheckBox silent;
    private CheckBox useTechnologyPreview;
    private CheckBox useCleanSession;
    private CheckBox assumeUntrustedCertificateIssuer;
    private CheckBox acceptUntrustedCertificates;
    private CheckBox alwaysLoadNoFocusLib;
    private TextArea arguments;
    private TextArea wdArguments;
    private FileListView extensions;
    private PreferenceTableView preferences;
    private Button addPreferenceButton;
    private Button removePreferenceButton;
    private ChoiceBox<UnexpectedAlertBehaviour> unexpectedAlertBehaviour;
    private ChoiceBox<InternetExplorerDriverLogLevel> ieLogLevel;
    private ChoiceBox<PageLoadStrategy> pageLoadStrategy;
    private TextArea environment;
    private FormPane basicPane;
    private FormPane advancedPane;

    public BrowserTab(String tabName) {
        super(tabName);
        setContent(getRoot());
    }

    public Node getRoot() {
        basicPane = new FormPane("browser-config-basic", 3);
        advancedPane = new FormPane("browser-config-advanced", 3);
        addBrowserName();
        addWebDriverExeBrowse();
        addBrowserExeBrowse();
        addArguments();
        addWdArguments();
        addUseCleanSession();
        addUseTechnologyPreview();

        addVerbose();
        addIELogLevel();
        addSilent();
        addLogFileBrowse();
        addEnvironment();
        addExtensions();
        addPageLoadStrategy();
        addUnexpectedAlertBehavior();
        addAssumeUntrustedCertificateIssuer();
        addAcceptUntrustedCertificates();
        addAlwaysLoadNoFocusLib();
        addBrowserPreferences();

        ScrollPane sp1 = new ScrollPane(basicPane);
        sp1.setFitToWidth(true);
        TitledPane basicTitledPane = new TitledPane("Basic Settings", sp1);
        ScrollPane sp2 = new ScrollPane(advancedPane);
        sp2.setFitToWidth(true);
        TitledPane advancedTitledPane = new TitledPane("Advanced Settings", sp2);
        Accordion accordion = new Accordion(basicTitledPane, advancedTitledPane);
        accordion.setExpandedPane(basicTitledPane);
        return accordion;
    }

    public void addAssumeUntrustedCertificateIssuer() {
        assumeUntrustedCertificateIssuer = new CheckBox("Assume Untrusted Certificate Issuer");
        assumeUntrustedCertificateIssuer.setSelected(
                BrowserConfig.instance().getValue(getBrowserName(), "browser-assume-untrusted-certificate-issuser", false));
        advancedPane.addFormField("", assumeUntrustedCertificateIssuer);
    }

    public void addAcceptUntrustedCertificates() {
        acceptUntrustedCertificates = new CheckBox("Accept Untrusted Certificates");
        acceptUntrustedCertificates
                .setSelected(BrowserConfig.instance().getValue(getBrowserName(), "browser-accept-untrusted-certificates", false));
        advancedPane.addFormField("", acceptUntrustedCertificates);
    }

    public void addAlwaysLoadNoFocusLib() {
        alwaysLoadNoFocusLib = new CheckBox("Always Load No Focus Library (Linux)");
        alwaysLoadNoFocusLib
                .setSelected(BrowserConfig.instance().getValue(getBrowserName(), "browser-always-load-no-focus-lib", false));
        advancedPane.addFormField("", alwaysLoadNoFocusLib);
    }

    public void addIELogLevel() {
        ieLogLevel = new ChoiceBox<>();
        ieLogLevel.getItems().add(null);
        ieLogLevel.getItems().addAll(FXCollections.observableArrayList(InternetExplorerDriverLogLevel.values()));
        String value = BrowserConfig.instance().getValue(getBrowserName(), "webdriver-ie-log-level");
        if (value != null)
            ieLogLevel.getSelectionModel().select(InternetExplorerDriverLogLevel.valueOf(value));
        advancedPane.addFormField("Log Level:", ieLogLevel);
    }

    public void addUnexpectedAlertBehavior() {
        unexpectedAlertBehaviour = new ChoiceBox<>();
        unexpectedAlertBehaviour.getItems().add(null);
        unexpectedAlertBehaviour.getItems().addAll(FXCollections.observableArrayList(UnexpectedAlertBehaviour.values()));
        String value = BrowserConfig.instance().getValue(getBrowserName(), "browser-unexpected-alert-behaviour");
        if (value != null)
            unexpectedAlertBehaviour.getSelectionModel().select(UnexpectedAlertBehaviour.fromString(value));
        advancedPane.addFormField("Unexpected alert behaviour:", unexpectedAlertBehaviour);
    }

    public void addPageLoadStrategy() {
        pageLoadStrategy = new ChoiceBox<>();
        pageLoadStrategy.getItems().add(null);
        pageLoadStrategy.getItems().addAll(FXCollections.observableArrayList(PageLoadStrategy.values()));
        String value = BrowserConfig.instance().getValue(getBrowserName(), "browser-page-load-strategy");
        if (value != null)
            pageLoadStrategy.getSelectionModel().select(PageLoadStrategy.fromString(value));
        advancedPane.addFormField("Page load strategy:", pageLoadStrategy);
    }

    public void addExtensions() {
        addExtensions(getExtensionDescription(), getExtensionExt());
    }

    public String getExtensionDescription() {
        return null;
    }

    public String getExtensionExt() {
        return null;
    }

    public void addBrowserPreferences() {
        preferences = new PreferenceTableView();
        preferences.setText(BrowserConfig.instance().getValue(getBrowserName(), "browser-preferences", ""));
        preferences.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<BrowserPreference>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends BrowserPreference> c) {
                removePreferenceButton.setDisable(preferences.getSelectionModel().getSelectedItems().size() <= 0);
            }
        });
        addPreferenceButton = FXUIUtils.createButton("add", "Add a preference", true, "Add");
        addPreferenceButton.setOnAction((e) -> {
            AddPreferenceStage stage = new AddPreferenceStage();
            stage.getStage().showAndWait();
            if (stage.getSelected() != null) {
                preferences.getItems().add(stage.getSelected());
            }
        });
        addPreferenceButton.setMaxWidth(Double.MAX_VALUE);
        removePreferenceButton = FXUIUtils.createButton("remove", "Remove selected preferences", false, "Remove");
        removePreferenceButton.setMaxWidth(Double.MAX_VALUE);
        removePreferenceButton.setOnAction((e) -> preferences.removeSelected());
        VBox vbox = new VBoxExtension(5, addPreferenceButton, removePreferenceButton);
        advancedPane.addFormField("Preferences", preferences, vbox);
    }

    public void addExtensions(String description, String ext) {
        extensions = new FileListView();
        extensions.setPrefRowCount(3);
        extensions.setText(BrowserConfig.instance().getValue(getBrowserName(), "browser-extensions", ""));
        browseExtensionFileButton = FXUIUtils.createButton("browse", "Browse Extension file", true, "Browse");
        ExtensionFilter filter = null;
        if (description != null)
            filter = new ExtensionFilter(description, ext);
        FileSelectionHandler fileSelectionHandler = new FileSelectionHandler(this, filter, null, browseExtensionFileButton,
                "Select Browser extension file");
        fileSelectionHandler.setMode(FileSelectionHandler.FILE_CHOOSER);
        browseExtensionFileButton.setOnAction(fileSelectionHandler);
        Label prompt = new Label("One extension per line.");
        advancedPane.addFormField("Extensions:", prompt, 2, 3);
        removeExtensionFileButton = FXUIUtils.createButton("remove", "Remove selected extensions", true, "Remove");
        removeExtensionFileButton.setDisable(true);
        extensions.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<File>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends File> c) {
                removeExtensionFileButton.setDisable(extensions.getSelectionModel().getSelectedItems().size() <= 0);
            }
        });
        removeExtensionFileButton.setOnAction((e) -> extensions.removeSelected());
        advancedPane.addFormField("", extensions, new VBoxExtension(5, browseExtensionFileButton, removeExtensionFileButton));
    }

    public void addArguments() {
        arguments = new TextArea();
        arguments.setPrefRowCount(3);
        arguments.setText(BrowserConfig.instance().getValue(getBrowserName(), "browser-arguments", ""));
        Label prompt = new Label("One argument per line.");
        basicPane.addFormField("Browser Arguments:", prompt, 2, 3);
        basicPane.addFormField("", arguments);
    }

    public void addWdArguments() {
        wdArguments = new TextArea();
        wdArguments.setPrefRowCount(3);
        wdArguments.setText(BrowserConfig.instance().getValue(getBrowserName(), "webdriver-arguments", ""));
        Label prompt = new Label("One argument per line.");
        basicPane.addFormField("WebDriver Arguments:", prompt, 2, 3);
        basicPane.addFormField("", wdArguments);
    }

    public void addVerbose() {
        verbose = new CheckBox("Verbose");
        verbose.setSelected(BrowserConfig.instance().getValue(getBrowserName(), "webdriver-verbose", false));
        advancedPane.addFormField("", verbose);
    }

    public void addSilent() {
        silent = new CheckBox("Silent");
        silent.setSelected(BrowserConfig.instance().getValue(getBrowserName(), "webdriver-silent", true));
        advancedPane.addFormField("", silent);
    }

    public void addUseCleanSession() {
        useCleanSession = new CheckBox("Use Clean Session");
        useCleanSession.setSelected(BrowserConfig.instance().getValue(getBrowserName(), "browser-use-clean-session", false));
        basicPane.addFormField("", useCleanSession);
    }

    public void addUseTechnologyPreview() {
        useTechnologyPreview = new CheckBox("Use Technology Preview");
        useTechnologyPreview
                .setSelected(BrowserConfig.instance().getValue(getBrowserName(), "webdriver-use-technology-preview", false));
        basicPane.addFormField("", useTechnologyPreview);
    }

    public void addEnvironment() {
        environment = new TextArea();
        environment.setText(BrowserConfig.instance().getValue(getBrowserName(), "browser-environment", ""));
        environment.setPrefRowCount(3);
        Label prompt = new Label("One variable per line. Eg:\nTMP=/temp-browser\n");
        advancedPane.addFormField("Environment Variables:", prompt, 2, 1);
        advancedPane.addFormField("", environment);
    }

    protected abstract String getBrowserName();

    protected void addBrowserName() {
        nameField = new TextField();
        nameField.setText(getBrowserName());
        nameField.setDisable(true);
        basicPane.addFormField("Browser Name:", nameField, 2, 1);
    }

    protected void addWebDriverExeBrowse() {
        String value = BrowserConfig.instance().getValue(getBrowserName(), "webdriver-exe-path");
        String def = new ExecutableFinder().find(getWebDriverExecutableName());
        wdExeField = new TextField();
        if (def != null)
            wdExeField.setPromptText(def);
        if (value != null)
            wdExeField.setText(value);
        browseWDExeButton = FXUIUtils.createButton("browse", "Browse WebDriver Executable", true, "Browse");
        FileSelectionHandler fileSelectionHandler = new FileSelectionHandler(this, null, null, browseWDExeButton,
                "Select WebDriver executable");
        fileSelectionHandler.setMode(FileSelectionHandler.FILE_CHOOSER);
        browseWDExeButton.setOnAction(fileSelectionHandler);

        basicPane.addFormField("WebDriver Executable:", wdExeField, browseWDExeButton);
    }

    protected void addLogFileBrowse() {
        String value = BrowserConfig.instance().getValue(getBrowserName(), "webdriver-log-file-path");
        logFileField = new TextField();
        if (value != null)
            logFileField.setText(value);
        browseLogFileButton = FXUIUtils.createButton("browse", "Browse Log file", true, "Browse");
        FileSelectionHandler fileSelectionHandler = new FileSelectionHandler(this, null, null, browseLogFileButton,
                "Select WebDriver log file");
        fileSelectionHandler.setMode(FileSelectionHandler.FILE_SAVE_CHOOSER);
        browseLogFileButton.setOnAction(fileSelectionHandler);

        advancedPane.addFormField("WebDriver Log File:", logFileField, browseLogFileButton);
    }

    protected abstract String getWebDriverExecutableName();

    protected void addBrowserExeBrowse() {
        String value = BrowserConfig.instance().getValue(getBrowserName(), "browser-exe-path");
        browserExeField = new TextField();
        if (value != null)
            browserExeField.setText(value);
        browseBrowserExeButton = FXUIUtils.createButton("browse", "Select Browser Executable", true, "Browse");
        FileSelectionHandler fileSelectionHandler = new FileSelectionHandler(this, null, null, browseBrowserExeButton,
                "Select Browser executable");
        fileSelectionHandler.setMode(FileSelectionHandler.FILE_CHOOSER);
        browseBrowserExeButton.setOnAction(fileSelectionHandler);
        basicPane.addFormField("Browser Executable:", browserExeField, browseBrowserExeButton);
    }

    @Override
    public void filesSelected(List<File> selectedFiles, Object cookie) {
        if (selectedFiles != null && selectedFiles.size() != 0 && cookie == browseWDExeButton) {
            wdExeField.setText(selectedFiles.get(0).getAbsolutePath());
        }
        if (selectedFiles != null && selectedFiles.size() != 0 && cookie == browseLogFileButton) {
            logFileField.setText(selectedFiles.get(0).getAbsolutePath());
        }
        if (selectedFiles != null && selectedFiles.size() != 0 && cookie == browseBrowserExeButton) {
            browserExeField.setText(selectedFiles.get(0).getAbsolutePath());
        }
        if (selectedFiles != null && selectedFiles.size() > 0 && cookie == browseExtensionFileButton) {
            for (File file : selectedFiles) {
                extensions.getItems().add(file);
            }
        }
    }

    public String getBrowserExePath() {
        String text = browserExeField.getText();
        return text == null || text.equals("") ? null : text;
    }

    public String getWebDriverExePath() {
        String text = wdExeField.getText();
        return text == null || text.equals("") ? null : text;
    }

    public String getLogFilePath() {
        String text = logFileField.getText();
        return text == null || text.equals("") ? null : text;
    }

    public void save() {
        BrowserConfig config = BrowserConfig.instance();
        if (browserExeField != null)
            config.setValue(getBrowserName(), "browser-exe-path", getBrowserExePath());
        if (wdExeField != null)
            config.setValue(getBrowserName(), "webdriver-exe-path", getWebDriverExePath());
        if (logFileField != null)
            config.setValue(getBrowserName(), "webdriver-log-file-path", getLogFilePath());
        if (verbose != null)
            config.setValue(getBrowserName(), "webdriver-verbose", verbose.isSelected());
        if (silent != null)
            config.setValue(getBrowserName(), "webdriver-silent", silent.isSelected());
        if (useCleanSession != null)
            config.setValue(getBrowserName(), "browser-use-clean-session", useCleanSession.isSelected());
        if (useTechnologyPreview != null)
            config.setValue(getBrowserName(), "webdriver-use-technology-preview", useTechnologyPreview.isSelected());
        if (assumeUntrustedCertificateIssuer != null)
            config.setValue(getBrowserName(), "browser-assume-untrusted-certificate-issuser",
                    assumeUntrustedCertificateIssuer.isSelected());
        if (acceptUntrustedCertificates != null)
            config.setValue(getBrowserName(), "browser-accept-untrusted-certificates", acceptUntrustedCertificates.isSelected());
        if (alwaysLoadNoFocusLib != null)
            config.setValue(getBrowserName(), "browser-always-load-no-focus-lib", alwaysLoadNoFocusLib.isSelected());
        if (arguments != null)
            config.setValue(getBrowserName(), "browser-arguments", "".equals(arguments.getText()) ? null : arguments.getText());
        if (wdArguments != null)
            config.setValue(getBrowserName(), "webdriver-arguments",
                    "".equals(wdArguments.getText()) ? null : wdArguments.getText());
        if (extensions != null)
            config.setValue(getBrowserName(), "browser-extensions", "".equals(extensions.getText()) ? null : extensions.getText());
        if (preferences != null)
            config.setValue(getBrowserName(), "browser-preferences",
                    "".equals(preferences.getText()) ? null : preferences.getText());
        if (pageLoadStrategy != null)
            config.setValue(getBrowserName(), "browser-page-load-strategy",
                    pageLoadStrategy.getSelectionModel().getSelectedItem() == null ? null
                            : pageLoadStrategy.getSelectionModel().getSelectedItem().toString());
        if (unexpectedAlertBehaviour != null)
            config.setValue(getBrowserName(), "browser-unexpected-alert-behaviour",
                    unexpectedAlertBehaviour.getSelectionModel().getSelectedItem() == null ? null
                            : unexpectedAlertBehaviour.getSelectionModel().getSelectedItem().toString());
        if (ieLogLevel != null)
            config.setValue(getBrowserName(), "webdriver-ie-log-level",
                    ieLogLevel.getSelectionModel().getSelectedItem() == null ? null
                            : ieLogLevel.getSelectionModel().getSelectedItem().toString());
        if (environment != null)
            config.setValue(getBrowserName(), "browser-environment",
                    "".equals(environment.getText()) ? null : environment.getText());
    }
}
