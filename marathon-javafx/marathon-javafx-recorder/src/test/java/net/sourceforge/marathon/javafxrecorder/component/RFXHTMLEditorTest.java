package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import net.sourceforge.marathon.javafx.tests.HTMLEditorSample;
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

    @Override protected Pane getMainPane() {
        return new HTMLEditorSample();
    }
}
