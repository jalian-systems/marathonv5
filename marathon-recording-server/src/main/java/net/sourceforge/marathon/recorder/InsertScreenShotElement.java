package net.sourceforge.marathon.recorder;

import net.sourceforge.marathon.runtime.api.IScriptElement;
import net.sourceforge.marathon.runtime.api.Indent;
import net.sourceforge.marathon.runtime.api.RecordingScriptModel;
import net.sourceforge.marathon.runtime.api.WindowId;

public class InsertScreenShotElement implements IScriptElement {

    private static final long serialVersionUID = 1L;
    private final WindowId windowId;
    private String description;

    public InsertScreenShotElement(WindowId windowId, String description) {
        this.windowId = windowId;
        this.description = description;
    }

    @Override
    public String toScriptCode() {
        return Indent.getIndent() + RecordingScriptModel.getModel().getScriptCodeForInsertScreenShot(description);
    }

    @Override
    public WindowId getWindowId() {
        return windowId;
    }

    @Override
    public IScriptElement getUndoElement() {
        return null;
    }

    @Override
    public boolean isUndo() {
        return false;
    }

}
