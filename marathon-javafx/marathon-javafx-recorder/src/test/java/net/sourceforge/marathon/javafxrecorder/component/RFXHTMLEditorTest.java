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
package net.sourceforge.marathon.javafxrecorder.component;

import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import net.sourceforge.marathon.javafx.tests.HTMLEditorSample;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXHTMLEditorTest extends RFXComponentTest {

    @Test public void select() {
        HTMLEditor editor = (HTMLEditor) getPrimaryStage().getScene().getRoot().lookup(".html-editor");
        LoggingRecorder lr = new LoggingRecorder();
        String text = "This is a test text";
        final String htmlText = "<html><font color=\"RED\"><h1><This is also content>" + text + "</h1></html>";
        Platform.runLater(() -> {
            RFXHTMLEditor rfxhtmlEditor = new RFXHTMLEditor(editor, null, null, lr);
            editor.setHtmlText(htmlText);
            rfxhtmlEditor.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals(htmlText, recording.getParameters()[0]);
    }

    @Test public void getText() {
        HTMLEditor editor = (HTMLEditor) getPrimaryStage().getScene().getRoot().lookup(".html-editor");
        LoggingRecorder lr = new LoggingRecorder();
        String text = "This is a test text";
        final String htmlText = "<html><font color=\"RED\"><h1><This is also content>" + text + "</h1></html>";
        List<String> attributeText = new ArrayList<>();
        Platform.runLater(() -> {
            RFXHTMLEditor rfxhtmlEditor = new RFXHTMLEditor(editor, null, null, lr);
            editor.setHtmlText(htmlText);
            rfxhtmlEditor.focusLost(null);
            attributeText.add(rfxhtmlEditor.getAttribute("text"));
        });
        new Wait("Waiting for html editor text.") {
            @Override public boolean until() {
                return attributeText.size() > 0;
            }
        };
        AssertJUnit.assertEquals(htmlText, attributeText.get(0));
    }

    @Override protected Pane getMainPane() {
        return new HTMLEditorSample();
    }
}
