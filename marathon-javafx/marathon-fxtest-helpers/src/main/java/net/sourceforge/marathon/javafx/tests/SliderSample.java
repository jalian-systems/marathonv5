package net.sourceforge.marathon.javafx.tests;

import ensemble.Sample;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

public class SliderSample extends Sample {

    public SliderSample() {
        VBox root = new VBox();
        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(100);
        slider.setValue(40);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(50);
        slider.setMinorTickCount(5);
        slider.setBlockIncrement(10);
        root.getChildren().addAll(slider, new Button("Click me!!"));
        getChildren().add(root);
    }

}
