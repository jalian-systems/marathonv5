package net.sourceforge.marathon.runtime.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecordableList {
    private List<IScriptElement> impl = new ArrayList<IScriptElement>();

    public int size() {
        return impl.size();
    }

    public ComponentId getComponentId() {
        return null;
    }

    public WindowId getWindowId() {
        return null;
    }

    public String toScriptCode() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < impl.size(); i++) {
            IScriptElement recordable = (IScriptElement) impl.get(i);
            if (recordable instanceof CompositeScriptElement && buffer.length() > 0) {
                buffer.append("\n");
            }
            buffer.append(recordable.toScriptCode());
            if (recordable instanceof CompositeScriptElement && nextIsNotAContainer(i)) {
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }

    private boolean nextIsNotAContainer(int i) {
        return i + 1 < impl.size() && !(impl.get(i + 1) instanceof CompositeScriptElement);
    }

    public IScriptElement get(int i) {
        return (IScriptElement) impl.get(i);
    }

    public void add(IScriptElement recordable) {
        impl.add(recordable);
    }

    public Iterator<IScriptElement> iterator() {
        return impl.iterator();
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof RecordableList))
            return false;
        return this.impl.equals(((RecordableList) o).impl);
    }

    public int hashCode() {
        return impl.hashCode();
    }

    public IScriptElement last() {
        if (size() == 0)
            return null;
        return (IScriptElement) impl.get(size() - 1);
    }

    public void removeLast() {
        if (size() > 0)
            impl.remove(size() - 1);
    }

    public boolean canOverride(IScriptElement other) {
        return false;
    }

    public void addFirst(IScriptElement tag) {
        impl.add(0, tag);
    }
}
