package net.sourceforge.marathon.runtime.api;

public class WindowElement extends CompositeScriptElement {
    private static final long serialVersionUID = 1L;
    private WindowId windowId;

    public WindowElement(WindowId windowId) {
        this.windowId = windowId;
    }

    public WindowId getWindowId() {
        return windowId;
    }

    public String getTitle() {
        return windowId.getTitle();
    }

    public boolean owns(CompositeScriptElement child) {
        if (!(child instanceof WindowElement) || getTitle().equals(((WindowElement) child).windowId.getParentTitle()))
            return true;
        return false;
    }

    public String toScriptCode() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(Indent.getIndent() + ScriptModel.getModel().getScriptCodeForWindow(windowId));
        Indent.incIndent();
        buffer.append(super.toScriptCode());
        Indent.decIndent();
        buffer.append(Indent.getIndent() + ScriptModel.getModel().getScriptCodeForWindowClose(windowId));
        return buffer.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof WindowElement) {
            WindowElement that = (WindowElement) obj;
            new ObjectComparator();
            return ObjectComparator.compare(that.windowId, this.windowId) == 0;
        }
        return false;
    }

    public int hashCode() {
        return windowId.hashCode();
    }

    public boolean isUndo() {
        return false;
    }

    public IScriptElement getUndoElement() {
        return null;
    }
}
