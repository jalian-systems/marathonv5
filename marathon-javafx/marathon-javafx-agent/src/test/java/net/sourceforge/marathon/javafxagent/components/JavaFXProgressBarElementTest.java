package net.sourceforge.marathon.javafxagent.components;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ensemble.samples.controls.ProgressBarSample;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXProgressBarElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement progressBar;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        progressBar = driver.findElementByTagName("progress-bar");
    }

    @Test public void select() {
        ProgressBar progressBarNode = (ProgressBar) getPrimaryStage().getScene().getRoot().lookup(".progress-bar");
        Platform.runLater(() -> {
            progressBar.marathon_select("0.20");
        });
        new Wait("Wating for progress bar progress to be set.") {
            @Override public boolean until() {
                return progressBarNode.getProgress() == 0.2;
            }
        };
    }

    @Override protected Pane getMainPane() {
        return new ProgressBarSample();
    }

}
