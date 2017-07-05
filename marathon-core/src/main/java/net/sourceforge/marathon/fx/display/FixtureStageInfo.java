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

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.projectselection.ApplicationLayout;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.ProjectFile;

public class FixtureStageInfo {

    public static final Logger LOGGER = Logger.getLogger(FixtureStageInfo.class.getName());

    private ApplicationLayout applicationLayout;
    private List<String> fixtures;
    private String fixtureName;
    private String description;
    private boolean reuseFixture;

    public FixtureStageInfo(List<String> fixtures) {
        this.fixtures = fixtures;
    }

    public List<String> getFixtures() {
        return fixtures;
    }

    public ApplicationLayout getApplicationLayout(ModalDialog<?> parent) {
        applicationLayout = new ApplicationLayout(parent);
        return applicationLayout;
    }

    public boolean isValidInput(boolean showAlert) {
        return applicationLayout.isValidInput(showAlert);
    }

    public void setFixtureName(String fixtureName) {
        this.fixtureName = fixtureName;
    }

    public String getFixtureName() {
        return fixtureName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setReuseFixture(boolean reuseFixture) {
        this.reuseFixture = reuseFixture;
    }

    public boolean isReuseFixture() {
        return reuseFixture;
    }

    public String getSelectedLauncher() {
        return applicationLayout.getClassName();
    }

    public void setProperties() {
        try {
            Properties properties = ProjectFile.getProjectProperties();
            properties.setProperty(Constants.PROP_PROJECT_DIR, System.getProperty(Constants.PROP_PROJECT_DIR));
            applicationLayout.setProperties(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getProperties() {
        Properties props = new Properties();
        props.setProperty(Constants.PROP_PROJECT_DIR, System.getProperty(Constants.PROP_PROJECT_DIR));
        props.setProperty(Constants.FIXTURE_DESCRIPTION, description);
        props.setProperty(Constants.FIXTURE_REUSE, Boolean.valueOf(reuseFixture).toString());
        applicationLayout.getProperties(props);
        return props;
    }

}
