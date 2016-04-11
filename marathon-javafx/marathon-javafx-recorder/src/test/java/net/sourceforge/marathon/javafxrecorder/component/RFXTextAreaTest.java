package net.sourceforge.marathon.javafxrecorder.component;

import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.TextAreaSample;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTextAreaTest extends RFXComponentTest {

    @Override protected Pane getMainPane() {
        return new TextAreaSample();
    }

    @Test public void select() {
        final TextArea textArea = (TextArea) getPrimaryStage().getScene().getRoot().lookup(".text-area");
        Platform.runLater(new Runnable() {
            @Override public void run() {
                textArea.setText("Hello World");
            }
        });
        LoggingRecorder lr = new LoggingRecorder();
        RFXComponent rTextField = new RFXTextInputControl(textArea, null, null, lr);
        Platform.runLater(new Runnable() {
            @Override public void run() {
                rTextField.focusLost(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("Hello World", select.getParameters()[0]);
    }

    @Test public void getText() {
        final TextArea textArea = (TextArea) getPrimaryStage().getScene().getRoot().lookup(".text-area");
        LoggingRecorder lr = new LoggingRecorder();
        List<Object> text = new ArrayList<>();
        Platform.runLater(() -> {
            RFXComponent rTextField = new RFXTextInputControl(textArea, null, null, lr);
            textArea.setText("Hello World");
            rTextField.focusLost(null);
            text.add(rTextField.getAttribute("text"));
        });
        new Wait("Waiting for text area text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Hello World", text.get(0));
    }

    @Test public void selectWithSpecialChars() throws InterruptedException {
        final TextArea textArea = (TextArea) getPrimaryStage().getScene().getRoot().lookup(".text-area");
        Platform.runLater(new Runnable() {
            @Override public void run() {
                textArea.setText("Hello\n World'\"");
            }
        });
        LoggingRecorder lr = new LoggingRecorder();
        RFXComponent rTextField = new RFXTextInputControl(textArea, null, null, lr);
        Platform.runLater(new Runnable() {
            @Override public void run() {
                rTextField.focusLost(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("Hello\n World'\"", select.getParameters()[0]);
    }

    @Test public void selectWithUtf8Chars() throws InterruptedException {
        final TextArea textArea = (TextArea) getPrimaryStage().getScene().getRoot().lookup(".text-area");
        Platform.runLater(new Runnable() {
            @Override public void run() {
                textArea.setText("å∫ç∂´ƒ©˙ˆ∆");
            }
        });
        LoggingRecorder lr = new LoggingRecorder();
        RFXComponent rTextField = new RFXTextInputControl(textArea, null, null, lr);
        Platform.runLater(new Runnable() {
            @Override public void run() {
                rTextField.focusLost(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("å∫ç∂´ƒ©˙ˆ∆", select.getParameters()[0]);
    }
}
