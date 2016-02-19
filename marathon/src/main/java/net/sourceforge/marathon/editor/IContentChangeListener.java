package net.sourceforge.marathon.editor;

import java.util.EventListener;

public interface IContentChangeListener extends EventListener {
    public abstract void contentChanged();
}
