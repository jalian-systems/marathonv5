package net.sourceforge.marathon.javafx.tests;

import ensemble.Sample;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;

public class DatePickerSample extends Sample {

    public DatePickerSample() {
        HBox hBox = new HBox();
        hBox.setSpacing(15);

        DatePicker uneditableDatePicker = new DatePicker();
        uneditableDatePicker.setEditable(false);

        DatePicker editablDatePicker = new DatePicker();
        editablDatePicker.setPromptText("Edit or Pick...");
        hBox.getChildren().addAll(uneditableDatePicker, editablDatePicker);
        getChildren().add(hBox);
    }

}
