package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.PasswordFieldSample;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXPasswordFieldTest extends RFXComponentTest {

    @Test public void select() {
        final PasswordField passwordField = (PasswordField) getPrimaryStage().getScene().getRoot().lookup(".password-field");
        Platform.runLater(new Runnable() {
            @Override public void run() {
                passwordField.setText("Hello World");
            }
        });
        LoggingRecorder lr = new LoggingRecorder();
        RFXComponent rTextField = new RFXTextInputControl(passwordField, null, null, lr);
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

    @Override protected Pane getMainPane() {
        return new PasswordFieldSample();
    }
}
