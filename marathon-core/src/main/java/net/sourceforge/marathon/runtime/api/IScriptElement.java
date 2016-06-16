package net.sourceforge.marathon.runtime.api;

import java.io.Serializable;

public interface IScriptElement extends Serializable {
    String toScriptCode();

    WindowId getWindowId();

    IScriptElement getUndoElement();

    boolean isUndo();

}
