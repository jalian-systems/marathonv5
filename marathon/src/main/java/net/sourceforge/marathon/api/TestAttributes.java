package net.sourceforge.marathon.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TestAttributes {

    private static ThreadLocal<Map<String, Object>> localStorage = new ThreadLocal<Map<String, Object>>() {
        protected java.util.Map<String, Object> initialValue() {
            return new HashMap<String, Object>();
        };
    };

    public static void clear() {
        get().clear();
    }

    public static boolean containsKey(Object arg0) {
        return get().containsKey(arg0);
    }

    public static boolean containsValue(Object arg0) {
        return get().containsValue(arg0);
    }

    public static Set<Entry<String, Object>> entrySet() {
        return get().entrySet();
    }

    public static Object get(Object arg0) {
        return get().get(arg0);
    }

    public static boolean isEmpty() {
        return get().isEmpty();
    }

    public static Set<String> keySet() {
        return get().keySet();
    }

    public static Object put(String arg0, Object arg1) {
        return get().put(arg0, arg1);
    }

    public static void putAll(Map<? extends String, ? extends Object> arg0) {
        get().putAll(arg0);
    }

    public static Object remove(Object arg0) {
        return get().remove(arg0);
    }

    public static int size() {
        return get().size();
    }

    public static Collection<Object> values() {
        return get().values();
    }

    public static Map<String, Object> get() {
        return localStorage.get();
    }

}
