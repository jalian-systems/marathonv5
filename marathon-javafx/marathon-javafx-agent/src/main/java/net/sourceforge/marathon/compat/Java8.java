package net.sourceforge.marathon.compat;

import java.util.Iterator;

import com.sun.javafx.stage.StageHelper;

import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Java8 {

    private static ObservableList<Stage> stages;

    public static ObservableList<Stage> getStages() {
        if (stages == null)
            stages = StageHelper.getStages();
        return stages;
    }

    @SuppressWarnings("deprecation") public static Iterator<Window> getWindows() {
        return Window.impl_getWindows();
    }

    @SuppressWarnings("deprecation") public static String getChar(KeyCode keyCode) {
        return keyCode.impl_getChar();
    }

    @SuppressWarnings("deprecation") public static int getCode(KeyCode keyCode) {
        return keyCode.impl_getCode();
    }

    @SuppressWarnings("deprecation") public static String getUrl(Image image) {
        return image.impl_getUrl();
    }

}
