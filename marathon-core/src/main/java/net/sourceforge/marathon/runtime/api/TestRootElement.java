package net.sourceforge.marathon.runtime.api;

public class TestRootElement extends CompositeScriptElement {
    private static final long serialVersionUID = 1L;
    private String story;

    public TestRootElement(String story) {
        this.story = story;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof TestRootElement))
            return false;
        return super.equals(obj) && ObjectComparator.compare(story, ((TestRootElement) obj).story) == 0;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public WindowId getWindowId() {
        return null;
    }

    public boolean owns(CompositeScriptElement child) {
        return true;
    }

    public boolean isUndo() {
        return false;
    }

    public IScriptElement getUndoElement() {
        return null;
    }
}
