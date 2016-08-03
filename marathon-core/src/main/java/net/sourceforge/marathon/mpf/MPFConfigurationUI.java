/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.mpf;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.apache.commons.io.FileUtils;

import com.jgoodies.forms.factories.Borders;

import net.sourceforge.marathon.Main;
import net.sourceforge.marathon.api.ITestApplication;
import net.sourceforge.marathon.junit.textui.StdOutLogger;
import net.sourceforge.marathon.runtime.TestApplication;
import net.sourceforge.marathon.runtime.api.ButtonBarFactory;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.EscapeDialog;
import net.sourceforge.marathon.runtime.api.IPropertiesPanel;
import net.sourceforge.marathon.runtime.api.IRuntimeLauncherModel;
import net.sourceforge.marathon.runtime.api.IScriptModel;
import net.sourceforge.marathon.runtime.api.MPFUtils;
import net.sourceforge.marathon.runtime.api.RuntimeLogger;
import net.sourceforge.marathon.runtime.api.UIUtils;

public class MPFConfigurationUI extends EscapeDialog {

    private final static Logger logger = Logger.getLogger(MPFConfigurationUI.class.getName());

    private static final long serialVersionUID = 1L;
    public static final ImageIcon BANNER = new ImageIcon(
            MPFConfigurationUI.class.getClassLoader().getResource("net/sourceforge/marathon/mpf/images/banner.png"));;
    private IPropertiesPanel[] panels;
    private String dirName = null;
    private JTabbedPane tabbedPane;

    private ApplicationPanel applicationPanel;

    private JButton cancelButton;

    private JButton saveButton;

    public MPFConfigurationUI(JDialog parent) {
        this(null, parent);
    }

    public MPFConfigurationUI(JFrame parent) {
        this(null, parent);
    }

    public MPFConfigurationUI(String dirName, JDialog parent) {
        super(parent, "Configure - (New Project)", true);
        RuntimeLogger.setRuntimeLogger(new StdOutLogger());
        initConfigurationUI(dirName);
    }

    public MPFConfigurationUI(String dirName, JFrame parent) {
        super(parent, "Configure", true);
        RuntimeLogger.setRuntimeLogger(new StdOutLogger());
        initConfigurationUI(dirName);
    }

    private void initConfigurationUI(String dirName) {
        this.dirName = dirName;
        setProjectFramework(dirName);
        applicationPanel = new ApplicationPanel(this);
        panels = new IPropertiesPanel[] { new ProjectPanel(this), applicationPanel, new ScriptPanel(this) };
        BannerPanel bannerPanel = new BannerPanel();
        String[] lines;
        if (dirName != null)
            lines = new String[] { "Update a Marathon Project" };
        else
            lines = new String[] { "Create a Marathon Project" };
        BannerPanel.Sheet sheet = new BannerPanel.Sheet("Create and manage configuration", lines, BANNER);
        bannerPanel.addSheet(sheet, "main");
        getContentPane().add(bannerPanel, BorderLayout.NORTH);
        tabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.putClientProperty("jgoodies.noContentBorder", Boolean.TRUE);
        for (int i = 0; i < panels.length; i++) {
            tabbedPane.addTab(panels[i].getName(), panels[i].getIcon(), panels[i].getPanel());
        }
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_P);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_A);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_R);
        getContentPane().add(tabbedPane);
        JButton testButton = UIUtils.createTestButton();
        testButton.setMnemonic(KeyEvent.VK_T);
        testButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!validateInput())
                    return;
                ITestApplication application = getApplicationTester();
                try {
                    application.launch();
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(MPFConfigurationUI.this, "Unable to launch application " + e1);
                    e1.printStackTrace();
                }
            }
        });
        cancelButton = UIUtils.createCancelButton();
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MPFConfigurationUI.this.dispose();
            }
        });
        saveButton = UIUtils.createSaveButton();
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (validateInput()) {
                    saveProjectFile();
                    dispose();
                }
            }
        });
        JPanel buttonPanel = ButtonBarFactory.buildOKCancelApplyBar(saveButton, cancelButton, testButton);
        buttonPanel.setBorder(Borders.createEmptyBorder("0dlu, 0dlu, 3dlu, 9dlu"));
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        Properties properties = new Properties();
        if (dirName != null) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(new File(dirName, Constants.PROJECT_FILE));
                properties.load(fileInputStream);
            } catch (FileNotFoundException e) {
                return;
            } catch (IOException e) {
                return;
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            properties.setProperty(Constants.PROP_PROJECT_DIR, dirName);
            String name = properties.getProperty(Constants.PROP_PROJECT_NAME);
            if (name != null)
                setTitle("Configure - " + name);
        } else {
            properties = getDefaultProperties();
        }
        setProperties(properties);
        setSize(800, 600);
    }

    private void setProjectFramework(String dirName2) {
        Properties properties = new Properties();
        if (dirName != null) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(new File(dirName, Constants.PROJECT_FILE));
                properties.load(fileInputStream);
            } catch (FileNotFoundException e) {
                return;
            } catch (IOException e) {
                return;
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        String framework = properties.getProperty(Constants.PROP_PROJECT_FRAMEWORK);
        if (framework != null) {
            System.setProperty(Constants.PROP_PROJECT_FRAMEWORK, framework);
        }
        String launcherModel = properties.getProperty(Constants.PROP_PROJECT_LAUNCHER_MODEL);
        if (launcherModel != null) {
            System.setProperty(Constants.PROP_PROJECT_LAUNCHER_MODEL, launcherModel);
        }
    }

    protected ITestApplication getApplicationTester() {
        Properties props = getProperties();
        ITestApplication applciationTester = new TestApplication(MPFConfigurationUI.this, props);
        return applciationTester;
    }

    private IRuntimeLauncherModel getLauncherModel() {
        return applicationPanel.getSelectedModel();
    }

    private Properties getDefaultProperties() {
        Properties props = new Properties();
        props.setProperty(Constants.PROP_USE_FIELD_NAMES, Boolean.TRUE.toString());
        props.setProperty(Constants.PROP_PROPPREFIX + "java.util.logging.config.file", "%marathon.project.dir%/logging.properties");
        props.setProperty(Constants.PROP_PROJECT_FRAMEWORK, System.getProperty(Constants.PROP_PROJECT_FRAMEWORK));
        return props;
    }

    private void setProperties(Properties props) {
        setPropertiesToPanels(panels, props);
    }

    private void setPropertiesToPanels(IPropertiesPanel[] panelsArray, Properties props) {
        if (panelsArray != null)
            for (int i = 0; i < panelsArray.length; i++)
                panelsArray[i].setProperties(props);
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        getPropertiesFromPanels(panels, properties);

        properties.setProperty(Constants.PROP_APPLICATION_LAUNCHTIME, "60000");
        return properties;
    }

    private void getPropertiesFromPanels(IPropertiesPanel[] panelsArray, Properties properties) {
        if (panelsArray != null)
            for (int i = 0; i < panelsArray.length; i++) {
                panelsArray[i].getProperties(properties);
            }
    }

    public String getProjectDirectory() {
        setLocation(getParent().getX() + 20, getParent().getY() + 20);
        setVisible(true);
        return dirName;
    }

    private boolean validateInput() {
        return validatePanelInputs(panels);
    }

    private boolean validatePanelInputs(IPropertiesPanel[] panelsArray) {
        if (panelsArray != null)
            for (int i = 0; i < panelsArray.length; i++) {
                if (!panelsArray[i].isValidInput()) {
                    tabbedPane.setSelectedComponent(panelsArray[i].getPanel());
                    return false;
                }
            }
        return true;
    }

    private void saveProjectFile() {
        Properties propsFromPanels = getProperties();
        File projectDir = new File(propsFromPanels.getProperty(Constants.PROP_PROJECT_DIR));
        createMarathonDirectories(propsFromPanels);
        createDefaultFixture(propsFromPanels, new File(projectDir, Constants.DIR_FIXTURES));
        MPFUtils.convertPathChar(propsFromPanels);
        copyMarathonDirProperties(propsFromPanels);
        try {
            Properties saveProps = getProperties();
            copyMarathonDirProperties(saveProps);
            saveProps.remove(Constants.PROP_PROJECT_DIR);
            FileOutputStream fileOutputStream = new FileOutputStream(new File(projectDir, Constants.PROJECT_FILE));
            try {
                saveProps.store(fileOutputStream, "Marathon Project File");
            } finally {
                fileOutputStream.close();
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Can't store the settings: " + e.getMessage());
            return;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Can't store the settings: " + e.getMessage());
            return;
        }
        Main.processMPF(projectDir.getAbsolutePath());
        dirName = projectDir.toString();
        dispose();
    }

    private void createDefaultFixture(Properties props, File fixtureDir) {
        try {
            if (getLauncherModel() == null)
                return;
            getSelectedScriptModel(props.getProperty(Constants.PROP_PROJECT_SCRIPT_MODEL)).createDefaultFixture(this, props,
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
        if (props.getProperty(Constants.PROP_TEST_DIR) == null)
            props.setProperty(Constants.PROP_TEST_DIR, "%" + Constants.PROP_PROJECT_DIR + "%/" + Constants.DIR_TESTCASES);
        if (props.getProperty(Constants.PROP_SUITE_DIR) == null)
            props.setProperty(Constants.PROP_SUITE_DIR, "%" + Constants.PROP_PROJECT_DIR + "%/" + Constants.DIR_TESTSUITES);
        if (props.getProperty(Constants.PROP_CHECKLIST_DIR) == null)
            props.setProperty(Constants.PROP_CHECKLIST_DIR, "%" + Constants.PROP_PROJECT_DIR + "%/" + Constants.DIR_CHECKLIST);
        if (props.getProperty(Constants.PROP_MODULE_DIRS) == null)
            props.setProperty(Constants.PROP_MODULE_DIRS, "%" + Constants.PROP_PROJECT_DIR + "%/" + Constants.DIR_MODULE);
        if (props.getProperty(Constants.PROP_DATA_DIR) == null)
            props.setProperty(Constants.PROP_DATA_DIR, "%" + Constants.PROP_PROJECT_DIR + "%/" + Constants.DIR_DATA);
        if (props.getProperty(Constants.PROP_FIXTURE_DIR) == null)
            props.setProperty(Constants.PROP_FIXTURE_DIR, "%" + Constants.PROP_PROJECT_DIR + "%/" + Constants.DIR_FIXTURES);
    }

    private void createMarathonDirectories(Properties props) {
        String projectDir = props.getProperty(Constants.PROP_PROJECT_DIR);
        if (props.getProperty(Constants.PROP_TEST_DIR) == null)
            createMarathonDir(projectDir, Constants.DIR_TESTCASES);
        if (props.getProperty(Constants.PROP_SUITE_DIR) == null)
            createMarathonDir(projectDir, Constants.DIR_TESTSUITES);
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
                FileUtils.copyInputStreamToFile(MPFConfigurationUI.class.getResourceAsStream("/logging.properties"), destFile);
            } catch (IOException e) {
                logger.warning("Copy file failed: " + destFile);
                e.printStackTrace();
            }
        }
        if (props.getProperty(Constants.PROP_MODULE_DIRS) == null)
            createMarathonDir(projectDir, Constants.DIR_MODULE);
        if (props.getProperty(Constants.PROP_DATA_DIR) == null)
            createMarathonDir(projectDir, Constants.DIR_DATA);
        if (props.getProperty(Constants.PROP_FIXTURE_DIR) == null)
            createMarathonDir(projectDir, Constants.DIR_FIXTURES);
    }

    private void createMarathonDir(String projectDir, String dir) {
        File file = new File(projectDir, dir);
        if (!file.mkdirs()) {
            logger.warning("Unable to create folder: " + file + " - Marathon might not be able to use the project folder");
        }
    }

    private IScriptModel getSelectedScriptModel(String selectedScript)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> klass = Class.forName(selectedScript);
        return (IScriptModel) klass.newInstance();
    }

    @Override public JButton getOKButton() {
        return saveButton;
    }

    @Override public JButton getCloseButton() {
        return cancelButton;
    }

}
