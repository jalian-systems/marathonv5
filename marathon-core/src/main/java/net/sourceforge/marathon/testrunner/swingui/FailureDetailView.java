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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.sourceforge.marathon.display.ResultPane.IResultPaneSelectionListener;
import net.sourceforge.marathon.junit.MarathonAssertion;
import net.sourceforge.marathon.runtime.api.Failure;
import net.sourceforge.marathon.runtime.api.SourceLine;

public class FailureDetailView extends JScrollPane {
    private static final long serialVersionUID = 1L;
    private DefaultListModel<Object> dataModel;
    private IResultPaneSelectionListener resultPaneSelectionListener;

    private static class StackElement {
        private SourceLine sourceLine;

        public StackElement(SourceLine line) {
            this.sourceLine = line;
        }

        @Override public String toString() {
            return "    at " + sourceLine.fileName + ":" + sourceLine.lineNumber + " in function " + sourceLine.functionName;
        }
    }

    public FailureDetailView() {
        setBorder(null);
        setBackground(Color.WHITE);
        setViewportView(createList());
        setColumnHeaderView(new JLabel("Trace", Icons.TRACE, SwingConstants.LEFT));
    }

    private JList<Object> createList() {
        dataModel = new DefaultListModel<Object>();
        final JList<Object> stackTrace = new JList<Object>(dataModel);
        stackTrace.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    Object selectedValue = stackTrace.getSelectedValue();
                    if (selectedValue instanceof StackElement) {
                        resultPaneSelectionListener.resultSelected(((StackElement) selectedValue).sourceLine);
                    }
                }
            }
        });
        return stackTrace;
    }

    public void reset() {
        dataModel.removeAllElements();
    }

    public void setException(Throwable exception) {
        reset();
        if (exception == null) {
            return;
        }
        if (exception instanceof MarathonAssertion) {
            MarathonAssertion massert = (MarathonAssertion) exception;
            Failure[] failures = massert.getFailures();
            for (Failure failure : failures) {
                String message = failure.getMessage();
                message = message.replaceAll("\t", "    ");
                dataModel.addElement(message);
                SourceLine[] traceback = failure.getTraceback();
                for (SourceLine sourceLine : traceback) {
                    dataModel.addElement(new StackElement(sourceLine));
                }
            }
        }
    }

    public void setResultPaneListener(IResultPaneSelectionListener resultPaneSelectionListener) {
        this.resultPaneSelectionListener = resultPaneSelectionListener;
    }
}
