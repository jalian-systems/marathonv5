package net.sourceforge.marathon.objectmap;

import java.io.Serializable;

import net.sourceforge.marathon.runtime.api.IPropertyAccessor;

public class OMapRecognitionProperty implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String value;
    private String method;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean isMatch(IPropertyAccessor pa) {
        return pa.isMatched(method, name, value);
    }

    @Override public String toString() {
        return "[" + name + ", " + value + ", " + method + "]";
    }

    public static String[] getMethodOptions() {
        return new String[] { IPropertyAccessor.METHOD_CONTAINS, IPropertyAccessor.METHOD_ENDS_WITH,
                IPropertyAccessor.METHOD_EQUALS, IPropertyAccessor.METHOD_EQUALS_IGNORE_CASE, IPropertyAccessor.METHOD_MATCHES,
                IPropertyAccessor.METHOD_STARTS_WITH };
    }
}
