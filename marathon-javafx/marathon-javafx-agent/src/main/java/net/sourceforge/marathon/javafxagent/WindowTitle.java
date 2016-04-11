package net.sourceforge.marathon.javafxagent;

import com.sun.javafx.stage.StageHelper;

import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class WindowTitle {

    private Stage window;

    public WindowTitle(Stage window) {
        this.window = window;
    }

    public String getTitle() {
        String title = getTitle(window);
        ObservableList<Stage> windows = StageHelper.getStages();
        String original = title;
        int index = 1;
        for (Stage w : windows) {
            if (w == window)
                return title;
            if (!w.isShowing())
                continue;
            String wTitle = getTitle(w);
            if (original.equals(wTitle))
                title = original + "(" + index++ + ")";
        }
        return title;
    }

    private String getTitle(Stage component) {
        String title = component.getTitle();
        if (title == null)
            return component.getClass().getName();
        return title;
    }

}
