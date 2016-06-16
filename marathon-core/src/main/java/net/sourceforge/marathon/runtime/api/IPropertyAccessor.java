package net.sourceforge.marathon.runtime.api;

public interface IPropertyAccessor {

    public static final String METHOD_EQUALS_IGNORE_CASE = "equalsIgnoreCase";
    public static final String METHOD_EQUALS = "equals";
    public static final String METHOD_MATCHES = "matches";
    public static final String METHOD_STARTS_WITH = "startsWith";
    public static final String METHOD_ENDS_WITH = "endsWith";
    public static final String METHOD_CONTAINS = "contains";

    String getProperty(String name);

    boolean isMatched(String method, String name, String value);
}
