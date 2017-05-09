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
package net.sourceforge.marathon.runtime.fx.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.projectselection.FormPane;
import net.sourceforge.marathon.runtime.api.PlugInModelInfo;

public abstract class CompositeLayout implements IPropertiesLayout {

    public static final Logger LOGGER = Logger.getLogger(CompositeLayout.class.getName());

    private ComboBox<PlugInModelInfo> optionBox = new ComboBox<>();
    private TabPane optionTabpane = new TabPane();

    private ObservableList<PlugInModelInfo> model;
    protected ModalDialog<?> parent;
    private ISubPropertiesLayout[] layouts;

    public CompositeLayout(ModalDialog<?> parent) {
        this.parent = parent;
        this.model = createComboBoxModel(getResourceName());
        initComponents();
    }

    private void initComponents() {
        optionBox.setItems(model);
        optionBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                updateTabPane();
            }
        });
        optionBox.setCellFactory(new Callback<ListView<PlugInModelInfo>, ListCell<PlugInModelInfo>>() {
            @Override public ListCell<PlugInModelInfo> call(ListView<PlugInModelInfo> param) {
                return new LauncherCell();
            }
        });
        optionTabpane.setId("CompositeTabPane");
        optionTabpane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        optionTabpane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
        VBox.setVgrow(optionTabpane, Priority.ALWAYS);
    }

    protected abstract String getResourceName();

    @Override public Node getContent() {
        VBox content = new VBox();
        content.setId("CompositeLayout");
        content.getStyleClass().add("composite-layout");

        FormPane form = new FormPane("composite-layout-form", 2);
        form.addFormField(getOptionFieldName(), optionBox);

        content.getChildren().addAll(form, optionTabpane);
        return content;
    }

    private void updateTabPane() {
        optionTabpane.getTabs().clear();
        layouts = getLauncherLayouts();
        for (ISubPropertiesLayout p : layouts) {
            Node content = p.getContent();
            if (Boolean.getBoolean("marathon.show.id")) {
                parent.addToolTips(content);
            }
            String name = p.getName();
            Tab tab = new Tab(name, content);
            tab.setId(name);
            tab.setGraphic(p.getIcon());
            optionTabpane.getTabs().add(tab);
        }
    }

    public ISubPropertiesLayout[] getLauncherLayouts() {
        String selectedLauncher = getClassName();
        if (selectedLauncher == null) {
            return new ISubPropertiesLayout[] {};
        }
        try {
            ISublayoutProvider model = getLauncherModel(selectedLauncher);
            if (model != null) {
                return model.getSublayouts(parent);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            FXUIUtils.showMessageDialog(parent.getStage(), "Could not find launcher: " + selectedLauncher, "Error",
                    AlertType.ERROR);
        } catch (InstantiationException e) {
            e.printStackTrace();
            FXUIUtils.showMessageDialog(parent.getStage(), "Could not find launcher: " + selectedLauncher, "Error",
                    AlertType.ERROR);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            FXUIUtils.showMessageDialog(parent.getStage(), "Could not find launcher: " + selectedLauncher, "Error",
                    AlertType.ERROR);
        }
        return new ISubPropertiesLayout[] {};
    }

    private ObservableList<PlugInModelInfo> createComboBoxModel(String pluginName) {
        ObservableList<PlugInModelInfo> model = FXCollections.observableArrayList();
        Enumeration<URL> systemResources = null;
        try {
            systemResources = ClassLoader.getSystemResources(pluginName);
        } catch (IOException e) {
            FXUIUtils.showMessageDialog(parent.getStage(), "No resource found for " + pluginName + ".",
                    "No " + pluginName + " Support", AlertType.ERROR);
            e.printStackTrace();
            System.exit(1);
        }
        while (systemResources.hasMoreElements()) {
            Properties props = new Properties();
            try {
                URL url = systemResources.nextElement();
                props.load(url.openStream());
                Set<Entry<Object, Object>> entries = props.entrySet();
                for (Entry<Object, Object> entry : entries) {
                    model.add(new PlugInModelInfo((String) entry.getValue(), (String) entry.getKey()));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (model.size() == 0) {
            FXUIUtils.showMessageDialog(parent.getStage(), "No Marathon " + pluginName + " found.", "No " + pluginName + " Support",
                    AlertType.ERROR);
            System.exit(1);
        }
        return model;
    }

    protected ISublayoutProvider getLauncherModel(String launcher)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (launcher == null || launcher.equals("")) {
            return null;
        }
        Class<?> klass = Class.forName(launcher);
        return (ISublayoutProvider) klass.newInstance();
    }

    public String getClassName() {
        if (optionBox.getSelectionModel().getSelectedItem() == null) {
            return "";
        }
        return optionBox.getSelectionModel().getSelectedItem().className;
    }

    protected abstract String getOptionFieldName();

    @Override public boolean isValidInput(boolean showAlert) {
        if (optionBox.getSelectionModel().getSelectedItem() == null) {
            errorMessage();
            Platform.runLater(() -> optionBox.requestFocus());
            return false;
        }
        for (ISubPropertiesLayout p : layouts) {
            if (!p.isValidInput(showAlert)) {
                return false;
            }
        }
        return true;
    }

    @Override public void getProperties(Properties props) {
        props.setProperty(getClassProperty(), getClassName());
        for (ISubPropertiesLayout p : layouts) {
            p.getProperties(props);
        }
    }

    abstract protected String getClassProperty();

    @Override public void setProperties(Properties props) {
        setPlugInSelection(optionBox, props, getClassProperty());
        updateTabPane();
        for (IPropertiesLayout p : layouts) {
            p.setProperties(props);
        }
    }

    private void setPlugInSelection(ComboBox<PlugInModelInfo> comboBox, Properties props, String key) {
        String model = (String) props.get(key);
        if (model == null) {
            comboBox.getSelectionModel().select(findFirstValidModel());
        } else {
            comboBox.getSelectionModel().select(getPluginModel(model));
            if (!isSelectable()) {
                comboBox.setDisable(true);
            }
        }
    }

    private int findFirstValidModel() {
        int n = model.size();
        for (int i = 0; i < n; i++) {
            PlugInModelInfo elementAt = model.get(i);
            try {
                getLauncherModel(elementAt.className);
                return i;
            } catch (Throwable t) {
            }
        }
        return 0;
    }

    protected boolean isSelectable() {
        return true;
    }

    public PlugInModelInfo getPluginModel(String className) {
        for (int i = 0; i < model.size(); i++) {
            if (model.get(i).className.equals(className)) {
                return model.get(i);
            }
        }
        return null;
    }

    protected abstract void errorMessage();

    public class LauncherCell extends ListCell<PlugInModelInfo> {
        @Override protected void updateItem(PlugInModelInfo item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                try {
                    setText(item.toString());
                    getLauncherModel(item.className);
                } catch (Throwable t) {
                    setDisable(true);
                }
            } else {
                setText(null);
            }
        }
    }

}
