package net.sourceforge.marathon.action;

import net.sourceforge.marathon.runtime.api.ComponentId;
import net.sourceforge.marathon.runtime.api.IScriptElement;
import net.sourceforge.marathon.runtime.api.WindowId;

public abstract class AbstractScriptElement implements IScriptElement {
    private static final long serialVersionUID = 4204837019479682968L;
    private ComponentId componentId;
    private WindowId windowId;

    public AbstractScriptElement(ComponentId componentId, WindowId windowId) {
        this.componentId = componentId;
        this.windowId = windowId;
    }

    public ComponentId getComponentId() {
        return componentId;
    }

    public WindowId getWindowId() {
        return windowId;
    }

    public String toString() {
        return toScriptCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof AbstractScriptElement && toString().equals(obj.toString());
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean isUndo() {
        return false;
    }

    public IScriptElement getUndoElement() {
        return null;
    }
}
