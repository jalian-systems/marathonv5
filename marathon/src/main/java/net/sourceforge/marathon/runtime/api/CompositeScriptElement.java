package net.sourceforge.marathon.runtime.api;

public abstract class CompositeScriptElement implements IScriptElement {
    private static final long serialVersionUID = 5400907759785664633L;
    private RecordableList list = new RecordableList();

    public void add(IScriptElement tag) {
        list.add(tag);
    }

    public void addFirst(IScriptElement tag) {
        list.addFirst(tag);
    }

    public String toScriptCode() {
        return list.toScriptCode();
    }

    public RecordableList getChildren() {
        return list;
    }

    /**
     * this is different than for action - action just compares text - we don't
     * want to compare our children's text, so unless this is the same instance,
     * containers are not the same objects
     */
    public boolean equals(Object that) {
        return this == that;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public abstract boolean owns(CompositeScriptElement child);

    public ComponentId getComponentId() {
        return null;
    }

    public boolean canOverride(IScriptElement other) {
        return false;
    }
}
