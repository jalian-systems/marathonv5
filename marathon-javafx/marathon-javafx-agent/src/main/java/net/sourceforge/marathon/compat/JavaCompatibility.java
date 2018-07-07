package net.sourceforge.marathon.compat;

import java.util.Iterator;

import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;

public class JavaCompatibility {

    public static ObservableList<Stage> getStages() {
        if (JavaVersion.current().is(JavaVersion.JAVA_8))
            return Java8.getStages();
        else
            return Java9.getStages();
    }

    public static Iterator<Window> getWindows() {
        if (JavaVersion.current().is(JavaVersion.JAVA_8))
            return Java8.getWindows();
        else
            return Java9.getWindows();
    }

    public static String getChar(KeyCode keyCode) {
        if (JavaVersion.current().is(JavaVersion.JAVA_8))
            return Java8.getChar(keyCode);
        else
            return Java9.getChar(keyCode);
    }

    public static int getCode(KeyCode keyCode) {
        if (JavaVersion.current().is(JavaVersion.JAVA_8))
            return Java8.getCode(keyCode);
        else
            return Java9.getCode(keyCode);
    }

    public static String getUrl(Image image) {
        if (JavaVersion.current().is(JavaVersion.JAVA_8))
            return Java8.getUrl(image);
        else
            return Java9.getUrl(image);
    }

    public static void waitTillAllEventsProcessed() {
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
        }
    }

    public static String getRootAccessCode() {
        String v8 = "    javafx.scene.Node m$r = ((javafx.stage.Stage) com.sun.javafx.stage.StageHelper.getStages().get(0)).getScene().getRoot();";
        String v9 = "    javafx.scene.Node m$r = ((javafx.stage.Stage) javafx.stage.Window.getWindows().get(0)).getScene().getRoot();";
        return JavaVersion.current().greaterThan(JavaVersion.JAVA_8) ? v9 : v8;
    }

}
