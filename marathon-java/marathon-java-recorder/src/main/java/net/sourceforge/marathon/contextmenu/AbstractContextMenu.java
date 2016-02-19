package net.sourceforge.marathon.contextmenu;

import net.sourceforge.marathon.component.RComponentFactory;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;

public abstract class AbstractContextMenu implements IContextMenu {

    protected final IJSONRecorder recorder;
    private final RComponentFactory finder;
    protected final ContextMenuWindow window;

    public AbstractContextMenu(ContextMenuWindow window, IJSONRecorder recorder, RComponentFactory finder) {
        this.window = window;
        this.recorder = recorder;
        this.finder = finder;
    }

    public IJSONRecorder getRecorder() {
        return recorder;
    }

    public RComponentFactory getFinder() {
        return finder;
    }

    public ContextMenuWindow getWindow() {
        return window;
    }
}
