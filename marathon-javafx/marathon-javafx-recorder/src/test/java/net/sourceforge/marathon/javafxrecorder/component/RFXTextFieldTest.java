package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import ensemble.samples.controls.text.TextFieldSample;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTextFieldTest extends RFXComponentTest {

	@Override
	protected Pane getMainPane() {
		return new TextFieldSample();
	}

	@Test
	public void select() throws InterruptedException {
		final TextField textField = (TextField) getPrimaryStage().getScene().getRoot().lookup(".text-field");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				textField.setText("Hello World");
			}
		});
		LoggingRecorder lr = new LoggingRecorder();
		RFXComponent rTextField = new RFXTextInputControl(textField, null, null, lr);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				rTextField.focusLost(null);
			}
		});
		List<Recording> recordings = lr.waitAndGetRecordings(1);
		Recording select = recordings.get(0);
		AssertJUnit.assertEquals("recordSelect", select.getCall());
		AssertJUnit.assertEquals("Hello World", select.getParameters()[0]);
	}

}
