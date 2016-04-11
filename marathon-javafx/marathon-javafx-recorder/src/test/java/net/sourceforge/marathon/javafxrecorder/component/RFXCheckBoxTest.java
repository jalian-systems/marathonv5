package net.sourceforge.marathon.javafxrecorder.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import ensemble.samples.controls.buttons.CheckBoxes;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXCheckBoxTest extends RFXComponentTest {

    @Override protected Pane getMainPane() {
        return new CheckBoxes();
    }

    @Test public void selectCheckBoxSelected() throws Throwable {
        CheckBox checkBox = findCheckbox("Simple checkbox");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                checkBox.setSelected(true);
                RFXCheckBox rfxCheckBox = new RFXCheckBox(checkBox, null, null, lr);
                rfxCheckBox.mouseClicked(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("checked", select.getParameters()[0]);
    }

    private CheckBox findCheckbox(String text) {
        Set<Node> checkBox = getPrimaryStage().getScene().getRoot().lookupAll(".check-box");
        for (Node node : checkBox) {
            if (((CheckBox) node).getText().equals(text)) {
                return (CheckBox) node;
            }
        }
        return null;
    }

    @Test public void selectCheckBoxNotSelected() throws Throwable {
        CheckBox checkBox = findCheckbox("Simple checkbox");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                RFXCheckBox rfxCheckBox = new RFXCheckBox(checkBox, null, null, lr);
                checkBox.setSelected(false);
                rfxCheckBox.mouseEntered(null);
                checkBox.setSelected(true);
                rfxCheckBox.mouseClicked(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("checked", select.getParameters()[0]);
    }

    @Test public void selectCheckBoxSelectedTriState() throws Throwable {
        CheckBox checkBox = findCheckbox("Three state checkbox");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                checkBox.setIndeterminate(true);
                checkBox.setSelected(true);
                RFXCheckBox rfxCheckBox = new RFXCheckBox(checkBox, null, null, lr);
                rfxCheckBox.mouseClicked(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("indeterminate", select.getParameters()[0]);
    }

    @Test public void getText() throws Throwable {
        CheckBox checkBox = findCheckbox("Simple checkbox");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                checkBox.setSelected(true);
                RFXCheckBox rfxCheckBox = new RFXCheckBox(checkBox, null, null, lr);
                rfxCheckBox.mouseClicked(null);
                text.add(rfxCheckBox._getText());
            }
        });
        new Wait("Waiting for checkbox text") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Simple checkbox", text.get(0));
    }

}
