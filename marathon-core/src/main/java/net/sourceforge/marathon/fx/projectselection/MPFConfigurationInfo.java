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
package net.sourceforge.marathon.fx.projectselection;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import javafx.scene.control.Alert.AlertType;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IRuntimeLauncherModel;
import net.sourceforge.marathon.runtime.api.IScriptModel;
import net.sourceforge.marathon.runtime.api.MPFUtils;
import net.sourceforge.marathon.runtime.api.ProjectFile;
import net.sourceforge.marathon.runtime.fx.api.IPropertiesLayout;

public class MPFConfigurationInfo {

    private final static Logger logger = Logger.getLogger(MPFConfigurationStage.class.getName());

    private String title;
    private String dirName;
    private ApplicationLayout applicationLayout;
    private Properties properties;

    public MPFConfigurationInfo(String title, String dirName, Properties properties) {
        this.title = title;
        this.dirName = dirName;
        this.properties = properties;
    }

    public MPFConfigurationInfo(String title) {
        this(title, null, null);
    }

    public String getTitle() {
        return title;
    }

    public String getDirName() {
        return dirName;
    }

    public IPropertiesLayout[] getProperties(ModalDialog<?> parent) {
        applicationLayout = new ApplicationLayout(parent);
        return new IPropertiesLayout[] { new ProjectLayout(parent), applicationLayout, new ScriptLayout(parent) };
    }

    public IRuntimeLauncherModel getLauncherModel() {
        return applicationLayout.getSelectedModel();
    }

    public IScriptModel getSelectedScriptModel(String selectedScript)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> klass = Class.forName(selectedScript);
        return (IScriptModel) klass.newInstance();
    }

    public Properties getUserProperties() {
        if (properties == null) {
            return getDefaultProperties();
        }
        return properties;
    }

    private Properties getDefaultProperties() {
        Properties props = new Properties();
        props.setProperty(Constants.PROP_USE_FIELD_NAMES, Boolean.TRUE.toString());
        props.setProperty(Constants.PROP_PROPPREFIX + "java.util.logging.config.file", "%marathon.project.dir%/logging.properties");
        props.setProperty(Constants.PROP_PROJECT_FRAMEWORK, System.getProperty(Constants.PROP_PROJECT_FRAMEWORK));
        return props;
    }

    public File saveProjectFile(IPropertiesLayout[] layouts) {
        Properties propsFromPanels = getProperties(layouts);
        File projectDir = new File(propsFromPanels.getProperty(Constants.PROP_PROJECT_DIR));
        createMarathonDirectories(propsFromPanels);
        createDefaultFixture(propsFromPanels, new File(projectDir, Constants.DIR_FIXTURES));
        MPFUtils.convertPathChar(propsFromPanels);
        copyMarathonDirProperties(propsFromPanels);
        try {
            Properties saveProps = getProperties(layouts);
            copyMarathonDirProperties(saveProps);
            saveProps.remove(Constants.PROP_PROJECT_DIR);
            ProjectFile.updateProperties(projectDir, saveProps);
        } catch (RuntimeException e) {
            FXUIUtils.showMessageDialog(null, "Can't store the settings: " + e.getMessage(), "Error", AlertType.INFORMATION);
            return null;
        } catch (Exception e) {
            FXUIUtils.showMessageDialog(null, "Can't store the settings: " + e.getMessage(), "Error", AlertType.INFORMATION);
            return null;
        }
        return projectDir;
    }

    public Properties getProperties(IPropertiesLayout[] layouts) {
        Properties properties = new Properties();
        getPropertiesFromLayouts(layouts, properties);

        properties.setProperty(Constants.PROP_APPLICATION_LAUNCHTIME, "60000");
        return properties;
    }

    private void getPropertiesFromLayouts(IPropertiesLayout[] panelsArray, Properties properties) {
        if (panelsArray != null) {
            for (IPropertiesLayout element : panelsArray) {
                element.getProperties(properties);
            }
        }
    }

    private void createMarathonDirectories(Properties props) {
        String projectDir = props.getProperty(Constants.PROP_PROJECT_DIR);
        if (props.getProperty(Constants.PROP_TEST_DIR) == null) {
            createMarathonDir(projectDir, Constants.DIR_TESTCASES);
        }
        if (props.getProperty(Constants.PROP_SUITE_DIR) == null) {
            createMarathonDir(projectDir, Constants.DIR_TESTSUITES);
        }
        if (props.getProperty(Constants.PROP_FEATURE_DIR) == null) {
            createMarathonDir(projectDir, Constants.DIR_FEATURES);
        }
        if (props.getProperty(Constants.PROP_ISSUE_DIR) == null) {
            createMarathonDir(projectDir, Constants.DIR_ISSUES);
        }
        if (props.getProperty(Constants.PROP_STORY_DIR) == null) {
            createMarathonDir(projectDir, Constants.DIR_STORIES);
        }
        if (props.getProperty(Constants.PROP_CHECKLIST_DIR) == null) {
            createMarathonDir(projectDir, Constants.DIR_CHECKLIST);
            File srcDir = new File(System.getProperty(Constants.PROP_HOME), "Checklists");
            File destDir = new File(projectDir, Constants.DIR_CHECKLIST);
            try {
                FileUtils.copyDirectory(srcDir, destDir);
            } catch (IOException e1) {
                logger.warning("Unable to copy Checklists folder from " + srcDir + " to " + destDir);
                e1.printStackTrace();
            }
            File destFile = new File(projectDir, "logging.properties");
            try {
                FileUtils.copyInputStreamToFile(MPFConfigurationStage.class.getResourceAsStream("/logging.properties"), destFile);
            } catch (IOException e) {
                logger.warning("Copy file failed: " + destFile);
                e.printStackTrace();
            }
        }
        if (props.getProperty(Constants.PROP_MODULE_DIRS) == null) {
            createMarathonDir(projectDir, Constants.DIR_MODULE);
        }
        if (props.getProperty(Constants.PROP_DATA_DIR) == null) {
            createMarathonDir(projectDir, Constants.DIR_DATA);
        }
        if (props.getProperty(Constants.PROP_FIXTURE_DIR) == null) {
            createMarathonDir(projectDir, Constants.DIR_FIXTURES);
        }
    }

    private void createDefaultFixture(Properties props, File fixtureDir) {
        try {
            if (getLauncherModel() == null) {
                return;
            }
            getSelectedScriptModel(props.getProperty(Constants.PROP_PROJECT_SCRIPT_MODEL)).createDefaultFixture(null, props,
                    fixtureDir, getLauncherModel().getPropertyKeys());
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
    }

    private void copyMarathonDirProperties(Properties props) {
        if (props.getProperty(Constants.PROP_TEST_DIR) == null) {
            props.setProperty(Constants.PROP_TEST_DIR, "%" + Constants.PROP_PROJECT_DIR + "%/" + Constants.DIR_TESTCASES);
        }
        if (props.getProperty(Constants.PROP_SUITE_DIR) == null) {
            props.setProperty(Constants.PROP_SUITE_DIR, "%" + Constants.PROP_PROJECT_DIR + "%/" + Constants.DIR_TESTSUITES);
        }
        if (props.getProperty(Constants.PROP_FEATURE_DIR) == null) {
            props.setProperty(Constants.PROP_FEATURE_DIR, "%" + Constants.PROP_PROJECT_DIR + "%/" + Constants.DIR_FEATURES);
        }
        if (props.getProperty(Constants.PROP_ISSUE_DIR) == null) {
            props.setProperty(Constants.PROP_ISSUE_DIR, "%" + Constants.PROP_PROJECT_DIR + "%/" + Constants.DIR_ISSUES);
        }
        if (props.getProperty(Constants.PROP_STORY_DIR) == null) {
            props.setProperty(Constants.PROP_STORY_DIR, "%" + Constants.PROP_PROJECT_DIR + "%/" + Constants.DIR_STORIES);
        }
        if (props.getProperty(Constants.PROP_CHECKLIST_DIR) == null) {
            props.setProperty(Constants.PROP_CHECKLIST_DIR, "%" + Constants.PROP_PROJECT_DIR + "%/" + Constants.DIR_CHECKLIST);
        }
        if (props.getProperty(Constants.PROP_MODULE_DIRS) == null) {
            props.setProperty(Constants.PROP_MODULE_DIRS, "%" + Constants.PROP_PROJECT_DIR + "%/" + Constants.DIR_MODULE);
        }
        if (props.getProperty(Constants.PROP_DATA_DIR) == null) {
            props.setProperty(Constants.PROP_DATA_DIR, "%" + Constants.PROP_PROJECT_DIR + "%/" + Constants.DIR_DATA);
        }
        if (props.getProperty(Constants.PROP_FIXTURE_DIR) == null) {
            props.setProperty(Constants.PROP_FIXTURE_DIR, "%" + Constants.PROP_PROJECT_DIR + "%/" + Constants.DIR_FIXTURES);
        }
    }

    private void createMarathonDir(String projectDir, String dir) {
        File file = new File(projectDir, dir);
        if (!file.mkdirs()) {
            if (!file.exists() || !file.isDirectory()) {
                logger.warning("Unable to create folder: " + file + " - Marathon might not be able to use the project folder");
            }
        }
    }
}
