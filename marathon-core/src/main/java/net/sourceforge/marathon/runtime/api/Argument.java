package net.sourceforge.marathon.runtime.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Argument implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        STRING, REGEX, NUMBER, NONE, BOOLEAN
    }

    private final String name;
    private final String defaultValue;
    private final List<String> defaultList;
    private final Type type;
    private static final List<String> trueList = new ArrayList<String>();
    private static final List<String> falseList = new ArrayList<String>();

    static {
        trueList.add("true");
        trueList.add("false");
        falseList.add("false");
        falseList.add("true");
    }

    public Argument(String name) {
        this.name = name;
        this.defaultValue = null;
        this.type = Type.NONE;
        this.defaultList = null;
    }

    public Argument(String name, String defaultValue, Type type) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.type = type;
        this.defaultList = null;
    }

    public Argument(String name, List<String> defaultList, Type type) {
        this.name = name;
        this.defaultList = defaultList;
        this.type = type;
        this.defaultValue = null;
    }

    public String getName() {
        return name;
    }

    public String getDefault() {
        if (defaultValue == null || type == Type.BOOLEAN)
            return null;
        return defaultValue;
    }

    public List<String> getDefaultList() {
        if (type == Type.BOOLEAN) {
            if (defaultValue.equals("true"))
                return trueList;
            else
                return falseList;
        }
        return defaultList;
    }

    @Override public String toString() {
        if (defaultValue == null)
            return name;
        return name + "(= " + defaultValue + ")";
    }

    public Type getType() {
        return type;
    }
}
