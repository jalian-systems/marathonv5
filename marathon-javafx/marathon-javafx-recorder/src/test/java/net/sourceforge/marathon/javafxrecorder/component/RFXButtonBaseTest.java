package net.sourceforge.marathon.javafxrecorder.component;

import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import ensemble.samples.controls.buttons.ColorButtonSample;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXButtonBaseTest extends RFXComponentTest {

    @Override protected Pane getMainPane() {
        return new ColorButtonSample();
    }

    @Test public void click() {
        Button button = (Button) getPrimaryStage().getScene().getRoot().lookup(".button");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                RFXButtonBase rfxButtonBase = new RFXButtonBase(button, null, null, lr);
                Point2D sceneXY = button.localToScene(new Point2D(3, 3));
                PickResult pickResult = new PickResult(button, sceneXY.getX(), sceneXY.getY());
                Point2D screenXY = button.localToScreen(new Point2D(3, 3));
                MouseEvent me = new MouseEvent(button, button, MouseEvent.MOUSE_PRESSED, 3, 3, sceneXY.getX(), screenXY.getY(),
                        MouseButton.PRIMARY, 1, false, false, false, false, true, false, false, false, false, false, pickResult);
                rfxButtonBase.mouseButton1Pressed(me);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("click", select.getCall());
        AssertJUnit.assertEquals("", select.getParameters()[0]);
    }

    @Test public void getText() {
        Button button = (Button) getPrimaryStage().getScene().getRoot().lookup(".button");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                RFXButtonBase rfxButtonBase = new RFXButtonBase(button, null, null, lr);
                Point2D sceneXY = button.localToScene(new Point2D(3, 3));
                PickResult pickResult = new PickResult(button, sceneXY.getX(), sceneXY.getY());
                Point2D screenXY = button.localToScreen(new Point2D(3, 3));
                MouseEvent me = new MouseEvent(button, button, MouseEvent.MOUSE_PRESSED, 3, 3, sceneXY.getX(), screenXY.getY(),
                        MouseButton.PRIMARY, 1, false, false, false, false, true, false, false, false, false, false, pickResult);
                rfxButtonBase.mouseButton1Pressed(me);
                text.add(rfxButtonBase.getAttribute("text"));
            }
        });
        new Wait("Waiting for button text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Color", text.get(0));
    }

}
