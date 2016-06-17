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
package net.sourceforge.marathon.testrunner.swingui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestTreeNode implements TreeNode {
    enum State {
        NORMAL, RUNNING, SUCCESS, ERROR, FAILURE
    }

    private static int counter = 0;

    private TestTreeNode parent;
    private List<TestTreeNode> children;
    private Test test;
    private State state;
    private int index;

    private Throwable exception;

    public TestTreeNode(Test test) {
        this(null, test);
    }

    public TestTreeNode(TestTreeNode parent, Test test) {
        if (parent == null) {
            counter = 0;
        }
        this.index = counter++;
        this.state = State.NORMAL;
        this.test = test;
        this.parent = parent;
        if (test instanceof TestSuite) {
            children = new ArrayList<TestTreeNode>();
            TestSuite suite = (TestSuite) test;
            int countTestCases = suite.testCount();
            for (int i = 0; i < countTestCases; i++) {
                children.add(new TestTreeNode(this, suite.testAt(i)));
            }
        }
    }

    @Override public TreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    @Override public int getChildCount() {
        return children.size();
    }

    @Override public TreeNode getParent() {
        return parent;
    }

    @Override public int getIndex(TreeNode node) {
        return children.indexOf(node);
    }

    @Override public boolean getAllowsChildren() {
        return children != null;
    }

    @Override public boolean isLeaf() {
        return children == null;
    }

    public Test getTest() {
        return test;
    }

    @Override public Enumeration<TestTreeNode> children() {
        return new Enumeration<TestTreeNode>() {
            int index = 0;

            @Override public boolean hasMoreElements() {
                return children != null && index < children.size();
            }

            @Override public TestTreeNode nextElement() {
                return children.get(index++);
            }
        };
    }

    public TreeNode[] getPath() {
        return getPathToRoot(this, 0);
    }

    protected TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
        TreeNode[] retNodes;

        /*
         * Check for null, in case someone passed in a null node, or they passed
         * in an element that isn't rooted at root.
         */
        if (aNode == null) {
            if (depth == 0)
                return null;
            else
                retNodes = new TreeNode[depth];
        } else {
            depth++;
            retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }

    public void setState(State state) {
        if (!isLeaf()) {
            if (state == State.SUCCESS || state == State.FAILURE || state == State.ERROR) {
                if (this.state == null || this.state == State.NORMAL || this.state == State.SUCCESS)
                    this.state = state;
                else if (this.state == State.FAILURE && state != State.SUCCESS)
                    this.state = state;
            }
        } else {
            if (state == State.SUCCESS) {
                if (this.state != State.FAILURE && this.state != State.ERROR)
                    this.state = State.SUCCESS;
            } else
                this.state = state;
        }
        if (parent != null)
            parent.setState(state);
    }

    public State getState() {
        return state;
    }

    public int getIndex() {
        return index;
    }

    public void setThrowable(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }
}
