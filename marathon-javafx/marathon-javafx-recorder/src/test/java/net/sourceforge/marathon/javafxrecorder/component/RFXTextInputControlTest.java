package net.sourceforge.marathon.javafxrecorder.component;

import org.testng.annotations.Test;

import ensemble.samples.controls.text.TextFieldSample;
import javafx.scene.layout.Pane;

public class RFXTextInputControlTest extends RFXComponentTest {

	@Override
	protected Pane getMainPane() {
		return new TextFieldSample();
	}

	@Test
	public void f() throws InterruptedException {
		Thread.sleep(3000);
	}

	@Test
	public void g() throws InterruptedException {
		Thread.sleep(3000);
	}
}
