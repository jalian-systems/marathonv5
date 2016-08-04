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
package net.sourceforge.marathon.display;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import net.sourceforge.marathon.fx.display.FixtureSelection;
import net.sourceforge.marathon.fx.display.IFixtureSelectionHandler;

public class FixtureSelector {

    public String selectFixture(DisplayWindow parent, String[] fixtures, String fixture) {
        FixtureSelection fixtureSelection = new FixtureSelection(FXCollections.observableArrayList(Arrays.asList(fixtures)),
                fixture == null ? "default" : fixture);

        List<String> selectedFixtures = new ArrayList<>();
        fixtureSelection.setFixtureSelectionHandler(new IFixtureSelectionHandler() {
            @Override public void handleFixture(String selectedFixture) {
                selectedFixtures.add(selectedFixture);
            }
        });
        fixtureSelection.getStage().showAndWait();
        if (selectedFixtures.size() != 0 && selectedFixtures.get(0) != null) {
            return selectedFixtures.get(0);
        }
        return null;
    }

}
