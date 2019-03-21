package net.sourceforge.marathon.component.jide;

import java.awt.Component;
import java.awt.Point;

import com.jidesoft.spinner.DateSpinner;

import net.sourceforge.marathon.component.RComponent;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RJideDateSpinnerElement extends RComponent {

    private String prevDate;

    public RJideDateSpinnerElement(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override
    public void focusGained(RComponent prev) {
        DateSpinner ds = (DateSpinner) getComponent();
        prevDate = ds._timeEditor.getTextField().getText();
    }

    @Override
    public void focusLost(RComponent next) {
        DateSpinner ds = (DateSpinner) getComponent();
        String currentDate = ds._timeEditor.getTextField().getText();
        if (!currentDate.equals(prevDate))
            recorder.recordSelect(this, currentDate);
    }

    @Override
    public String _getText() {
        DateSpinner ds = (DateSpinner) getComponent();
        return ds._timeEditor.getTextField().getText();
    }

    @Override
    public String getTagName() {
        return "date-spinner";
    }
}
