package net.sourceforge.marathon.javaagent;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

public class WindowTitle {

    private Window window;

    public WindowTitle(Window window) {
        this.window = window;
    }

    public String getTitle() {
        String title = getTitle(window);
        Window[] windows = Window.getWindows();
        String original = title;
        int index = 1;
        for (Window w : windows) {
            if (w == window)
                return title;
            if (!w.isVisible())
                continue;
            String wTitle = getTitle(w);
            if (original.equals(wTitle))
                title = original + "(" + index++ + ")";
        }
        return title;
    }

    private String getTitle(Component component) {
        String title = null;
        if (component instanceof Dialog) {
            title = ((Dialog) component).getTitle();
        } else if (component instanceof Frame)
            title = ((Frame) component).getTitle();

        if (title == null || "".equals(title))
            title = component.getName();
        if (title == null || "".equals(title))
            title = component.getClass().getName();
        return title;
    }

}
