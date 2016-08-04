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
package net.sourceforge.marathon.testrunner.fxui;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.Region;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.junit.MarathonTestCase;
import net.sourceforge.marathon.testrunner.fxui.TestTreeItem.State;

public class TestTreeItemCell extends TreeCell<Test> {

    private TestRunner testRunner;

    public TestTreeItemCell(TestRunner testRunner) {
        this.testRunner = testRunner;
    }

    @Override protected void updateItem(Test test, boolean empty) {
        super.updateItem(test, empty);
        if (test != null && !empty) {
            String value;
            if (test instanceof TestSuite) {
                value = ((TestSuite) test).getName();
            } else if (test instanceof MarathonTestCase) {
                value = ((MarathonTestCase) test).getName();
            } else {
                value = test.toString();
            }
            setText(value);
            Node icon = test instanceof TestSuite ? FXUIUtils.getIcon("tsuite") : FXUIUtils.getIcon("ttest");
            State state = ((TestTreeItem) getTreeItem()).getState();

            if (test instanceof TestSuite) {
                if (state == State.RUNNING) {
                    icon = FXUIUtils.getIcon("wait16trans");
                }
                if (state == State.SUCCESS) {
                    icon = FXUIUtils.getIcon("tsuiteok");
                }
                if (state == State.ERROR) {
                    icon = FXUIUtils.getIcon("tsuiteerror");
                }
                if (state == State.FAILURE) {
                    icon = FXUIUtils.getIcon("tsuitefail");
                }
            } else {
                if (state == State.RUNNING) {
                    icon = FXUIUtils.getIcon("wait16trans");
                }
                if (state == State.SUCCESS) {
                    icon = FXUIUtils.getIcon("testok");
                }
                if (state == State.ERROR) {
                    icon = FXUIUtils.getIcon("testerror");
                }
                if (state == State.FAILURE) {
                    icon = FXUIUtils.getIcon("testfail");
                }
            }
            setGraphic(icon);
            setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            if (testRunner.showFailures() && state != State.FAILURE && state != State.ERROR) {
                setGraphic(null);
                setText(null);
                setPrefSize(0.0, 0.0);
            }
        } else {
            setText(null);
            setGraphic(null);
        }
    }

}
