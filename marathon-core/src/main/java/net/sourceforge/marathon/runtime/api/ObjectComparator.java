package net.sourceforge.marathon.runtime.api;

public class ObjectComparator {
    public static int compare(Object o1, Object o2) {
        if (o1 == null ^ o2 == null)
            return o1 == null ? -1 : 1;
        if (o1 == null)
            return 0;
        return o1.equals(o2) ? 0 : -1;
    }
}
