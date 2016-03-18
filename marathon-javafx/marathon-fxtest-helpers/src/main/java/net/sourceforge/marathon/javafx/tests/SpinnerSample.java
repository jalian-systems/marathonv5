package net.sourceforge.marathon.javafx.tests;

import java.util.ArrayList;
import java.util.List;

import ensemble.Sample;
import javafx.collections.FXCollections;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;

public class SpinnerSample extends Sample {

    public SpinnerSample() {
        Spinner<Object> listSpinner = createListSpinner();
        Spinner<Integer> integerSpinner = createIntegerSpinner();
        Spinner<Double> doubleSpinner = createDoubleSpinner();

        HBox hBox = new HBox();
        hBox.setSpacing(20.0);
        hBox.getChildren().addAll(listSpinner, integerSpinner, doubleSpinner);
        getChildren().add(hBox);
    }

    private Spinner<Object> createListSpinner() {
        Spinner<Object> spinner = new Spinner<>();
        spinner.setId("list-spinner");
        List<Object> names = new ArrayList<Object>();
        names.add("January");
        names.add("February");
        names.add("March");
        names.add("April");
        names.add("May");
        names.add("June");
        names.add("July");
        names.add("August");
        names.add("September");
        names.add("October");
        names.add("November");
        names.add("December");
        spinner.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<Object>(FXCollections.observableArrayList(names)));
        return spinner;
    }

    private Spinner<Integer> createIntegerSpinner() {
        Spinner<Integer> spinner = new Spinner<>();
        spinner.setId("integer-spinner");
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50));
        return spinner;
    }

    private Spinner<Double> createDoubleSpinner() {
        Spinner<Double> spinner = new Spinner<Double>();
        spinner.setId("double-spinner");
        spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(25.50, 50.50));
        spinner.setEditable(true);
        return spinner;
    }
}
