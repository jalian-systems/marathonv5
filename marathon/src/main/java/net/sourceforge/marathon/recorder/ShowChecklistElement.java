package net.sourceforge.marathon.recorder;

import net.sourceforge.marathon.runtime.api.ComponentId;
import net.sourceforge.marathon.runtime.api.IScriptElement;
import net.sourceforge.marathon.runtime.api.Indent;
import net.sourceforge.marathon.runtime.api.ScriptModel;
import net.sourceforge.marathon.runtime.api.WindowId;

public class ShowChecklistElement implements IScriptElement {
    private static final long serialVersionUID = 1L;
    private String fileName;
    private final WindowId windowId;

    public ShowChecklistElement(WindowId windowId, String fileName) {
        this.windowId = windowId;
        this.fileName = fileName;
    }

    public String toScriptCode() {
        return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForShowChecklist(fileName);
    }

    public ComponentId getComponentId() {
        return null;
    }

    public WindowId getWindowId() {
        return windowId;
    }

    public boolean isUndo() {
        return false;
    }

    public IScriptElement getUndoElement() {
        return null;
    }
}
