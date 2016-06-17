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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.metal.MetalProgressBarUI;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A progress bar showing the green/red status
 */
class ProgressBar extends JProgressBar {
    private final class ProgressBarUI extends MetalProgressBarUI {

        private JTextField nErrors;
        private JTextField nFailures;
        private JTextField nRuns;
        private Icon failureIcon = Icons.FAILURE;
        private Icon errorIcon = Icons.ERROR;
        private JPanel panel;

        public ProgressBarUI() {
            nErrors = createOutputField(3);
            nFailures = createOutputField(3);
            nRuns = createOutputField(3);
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(
                    "fill:3dlu:grow, pref, 3dlu, pref:grow, 3dlu, pref, 3dlu, pref:grow, 3dlu, pref, 3dlu, pref:grow, fill:3dlu:grow",
                    "fill:pref:grow"));
            builder.background(transparent);
            builder.nextColumn();
            builder.append("Runs:", nRuns);
            JLabel l1 = new JLabel("Errors:", errorIcon, SwingConstants.LEFT);
            builder.append(l1, nErrors);
            l1.setBackground(transparent);
            JLabel l2 = new JLabel("Failures:", failureIcon, SwingConstants.LEFT);
            l2.setBackground(transparent);
            builder.append(l2, nFailures);
            panel = builder.getPanel();
            panel.setDoubleBuffered(false);
        }

        private JTextField createOutputField(int width) {
            JTextField field = new JTextField();
            field.setHorizontalAlignment(SwingConstants.LEFT);
            field.setEditable(false);
            field.setBorder(BorderFactory.createEmptyBorder());
            field.setBackground(transparent);
            return field;
        }

        @Override protected Color getSelectionBackground() {
            return Color.black;
        }

        @Override protected Color getSelectionForeground() {
            return Color.black;
        }

        @Override protected void paintString(Graphics g, int x, int y, int width, int height, int amountFull, Insets b) {
            panel.setSize(width, height);
            nErrors.setText(errors + "");
            nFailures.setText(failures + "");
            nRuns.setText(progressBar.getValue() + "/" + progressBar.getMaximum());
            panel.doLayout();
            panel.paint(g);
        }
    }

    private static final long serialVersionUID = 1L;
    boolean error = false;
    private int failures;
    private int errors;

    private static Color green = new Color(80, 200, 80, 128);
    private static Color red = new Color(200, 80, 80, 128);
    private static Color transparent = new Color(255, 255, 255, 0);

    public ProgressBar() {
        setUI(new ProgressBarUI());
        setStringPainted(true);
        setForeground(getStatusColor());
    }

    private Color getStatusColor() {
        if (error)
            return red;
        return green;
    }

    public void reset(int total) {
        error = false;
        errors = 0;
        failures = 0;
        setForeground(getStatusColor());
        setMaximum(total);
        setValue(0);
        setString();
    }

    private void setString() {
        setString("Runs: " + getValue() + "/" + getMaximum() + "    Failures: " + failures + "    Errors: " + errors);
    }

    public void increment() {
        setValue(getValue() + 1);
        setString();
    }

    public void setError(boolean b) {
        error = true;
        setForeground(getStatusColor());
        repaint();
    }

    public void incrementErrors() {
        errors++;
    }

    public void incrementFailures() {
        failures++;
    }
}
