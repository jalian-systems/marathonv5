package net.sourceforge.marathon.testrunner.swingui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultTreeCellRenderer;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.marathon.junit.MarathonTestCase;
import net.sourceforge.marathon.testrunner.swingui.TestTreeNode.State;

public class TestTreeNodeRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 1L;
    private TestRunner testRunner;

    public TestTreeNodeRenderer(TestRunner testRunner) {
        this.testRunner = testRunner;
    }

    @Override public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
            int row, boolean hasFocus) {
        TestTreeNode node = (TestTreeNode) value;
        Test test = node.getTest();
        if (test instanceof TestSuite) {
            value = ((TestSuite) test).getName();
        } else if (test instanceof MarathonTestCase) {
            value = ((MarathonTestCase) test).getName();
        } else {
            value = test.toString();
        }
        Icon icon = test instanceof TestSuite ? Icons.T_TSUITE : Icons.T_TEST;
        State state = node.getState();

        if (test instanceof TestSuite) {
            if (state == State.RUNNING)
                icon = Icons.T_TSUITERUN;
            if (state == State.SUCCESS)
                icon = Icons.T_TSUITEOK;
            if (state == State.ERROR)
                icon = Icons.T_TSUITEERROR;
            if (state == State.FAILURE)
                icon = Icons.T_TSUITEFAIL;
        } else {
            if (state == State.RUNNING)
                icon = Icons.T_TESTRUN;
            if (state == State.SUCCESS)
                icon = Icons.T_TESTOK;
            if (state == State.ERROR)
                icon = Icons.T_TESTERROR;
            if (state == State.FAILURE)
                icon = Icons.T_TESTFAIL;
        }
        Component label = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (label instanceof JLabel) {
            ((JLabel) label).setIcon(icon);
            label.setPreferredSize(new JLabel(((JLabel) label).getText(), icon, SwingConstants.LEFT).getPreferredSize());
            if (testRunner.showFailures() && state != State.FAILURE && state != State.ERROR) {
                label.setPreferredSize(new Dimension(0, 0));
            }
        }
        return label;
    }
}
