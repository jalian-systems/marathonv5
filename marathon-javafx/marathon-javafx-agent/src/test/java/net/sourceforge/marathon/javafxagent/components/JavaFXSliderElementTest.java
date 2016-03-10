package net.sourceforge.marathon.javafxagent.components;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.SliderSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXSliderElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement slider;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        slider = driver.findElementByTagName("slider");
    }

    @Test public void setSliderValue() {
        Slider sliderNode = (Slider) getPrimaryStage().getScene().getRoot().lookup(".slider");
        slider.marathon_select("25.0");
        new Wait("Waiting for slider value to be set.") {
            @Override public boolean until() {
                return 25.0 == sliderNode.getValue();
            }
        };
    }

    @Test(expectedExceptions = NumberFormatException.class) public void illegalArgumentException() {
        Slider sliderNode = (Slider) getPrimaryStage().getScene().getRoot().lookup(".slider");
        slider.marathon_select("ten");
        new Wait("Waiting for slider value to be set.") {
            @Override public boolean until() {
                return 25.0 == sliderNode.getValue();
            }
        };
    }

    @Override protected Pane getMainPane() {
        return new SliderSample();
    }
}
