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
    private DefaultListModel dataModel;
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

    private JList createList() {
        dataModel = new DefaultListModel();
        final JList stackTrace = new JList(dataModel);
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
