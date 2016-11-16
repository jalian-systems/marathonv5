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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestTreeItem extends TreeItem<Test> {

    enum State {
        NORMAL, RUNNING, SUCCESS, ERROR, FAILURE
    }

    private State state;
    private Test test;
    private ObservableList<TreeItem<Test>> children;

    private Throwable exception;

    public TestTreeItem(Test test) {
        setValue(test);
        this.state = State.NORMAL;
        this.test = test;
    }

    @Override public ObservableList<TreeItem<Test>> getChildren() {
        if (children != null) {
            return super.getChildren();
        }
        children = FXCollections.observableArrayList();
        if (test instanceof TestSuite) {
            TestSuite suite = (TestSuite) test;
            int countTestCases = suite.testCount();
            for (int i = 0; i < countTestCases; i++) {
                children.add(new TestTreeItem(suite.testAt(i)));
            }
        }
        super.getChildren().setAll(children);
        return super.getChildren();
    }

    @Override public boolean isLeaf() {
        return !(test instanceof TestSuite);
    }

    public void setState(State state) {
        if (!isLeaf()) {
            if (state == State.SUCCESS || state == State.FAILURE || state == State.ERROR || state == State.NORMAL) {
                if (this.state == null || this.state == State.NORMAL || this.state == State.SUCCESS) {
                    this.state = state;
                } else if ((this.state == State.ERROR || this.state == State.FAILURE) && state != State.SUCCESS) {
                    this.state = state;
                }
            }
        } else {
            if (state == State.SUCCESS) {
                if (this.state != State.FAILURE && this.state != State.ERROR) {
                    this.state = State.SUCCESS;
                }
            } else {
                this.state = state;
            }
        }
        if (getParent() != null) {
            ((TestTreeItem) getParent()).setState(state);
        }
    }

    public State getState() {
        return state;
    }

    public int getIndex() {
        if (getParent() != null) {
            return ((TestTreeItem) getParent()).getIndex() + getParent().getChildren().indexOf(this) + 1;
        }
        return 0;

    }

    public void setThrowable(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }

    public Test getTest() {
        return test;
    }
}
