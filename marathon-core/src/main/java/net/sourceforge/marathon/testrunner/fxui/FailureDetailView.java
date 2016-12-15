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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.display.ResultPane.IResultPaneSelectionListener;
import net.sourceforge.marathon.junit.MarathonAssertion;
import net.sourceforge.marathon.runtime.api.Failure;
import net.sourceforge.marathon.runtime.api.SourceLine;

public class FailureDetailView extends BorderPane {

    private ListView<Object> stackTrace;
    private ObservableList<Object> traces;
    private IResultPaneSelectionListener resultPaneSelectionListener;

    public FailureDetailView() {
        setTop(new Label("Trace", FXUIUtils.getIcon("trace")));
        initStackTrace();
        setCenter(stackTrace);
    }

    private void initStackTrace() {
        traces = FXCollections.observableArrayList();
        stackTrace = new ListView<>(traces);
        stackTrace.setOnMousePressed((event) -> {
            if (event.getClickCount() > 1) {
                Object selectedItem = stackTrace.getSelectionModel().getSelectedItem();
                if (selectedItem instanceof StackElement) {
                    resultPaneSelectionListener.resultSelected(((StackElement) selectedItem).sourceLine);
                }
            }
        });
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
                traces.add(message);
                SourceLine[] traceback = failure.getTraceback();
                for (SourceLine sourceLine : traceback) {
                    traces.add(new StackElement(sourceLine));
                }
            }
        }
    }

    void reset() {
        traces.clear();
    }

    private static class StackElement {
        private SourceLine sourceLine;

        public StackElement(SourceLine line) {
            this.sourceLine = line;
        }

        @Override public String toString() {
            return "    at " + sourceLine.fileName + ":" + sourceLine.lineNumber + " in function " + sourceLine.functionName;
        }
    }

    public void setResultPaneListener(IResultPaneSelectionListener resultPaneSelectionListener) {
        this.resultPaneSelectionListener = resultPaneSelectionListener;
    }

}
