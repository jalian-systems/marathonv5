package net.sourceforge.marathon.runtime.api;

import java.io.Serializable;

public class WindowId implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String title;
    private boolean frame = false;
    private WindowId parent;

    protected WindowId() {
    }

    public WindowId(String title, WindowId parent, boolean frame) {
        this.title = title;
        this.parent = parent;
        this.frame = frame;
    }

    public String getTitle() {
        return title;
    }

    public String getParentTitle() {
        if (parent == null)
            return null;
        return parent.getTitle();
    }

    public String toString() {
        if (parent != null)
            return parent.toString() + ">>" + getTitle();
        return getTitle();
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (frame ? 1231 : 1237);
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WindowId other = (WindowId) obj;
        if (frame != other.frame)
            return false;
        if (parent == null) {
            if (other.parent != null)
                return false;
        } else if (!parent.equals(other.parent))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }

    public boolean isFrame() {
        return frame;
    }

    public WindowId getParent() {
        return parent;
    }

    public void addToTagInserter(TagInserter tagInserter, IScriptElement recordable) {
        if (parent != null)
            parent.addToTagInserter(tagInserter, null);
        tagInserter.add(new WindowElement(this), recordable);
    }

}
