package net.sourceforge.marathon.runtime.api;

public abstract class DefaultMatcher implements IPropertyAccessor {

    @Override public boolean isMatched(String method, String name, String value) {
        String actual = getProperty(name);
        if (actual == null)
            return false;
        if (method.equals(IPropertyAccessor.METHOD_ENDS_WITH))
            return actual.endsWith(value);
        else if (method.equals(IPropertyAccessor.METHOD_EQUALS))
            return actual.equals(value);
        else if (method.equals(IPropertyAccessor.METHOD_EQUALS_IGNORE_CASE))
            return actual.equalsIgnoreCase(value);
        else if (method.equals(IPropertyAccessor.METHOD_MATCHES))
            return actual.matches(value);
        else if (method.equals(IPropertyAccessor.METHOD_STARTS_WITH))
            return actual.startsWith(value);
        else if (method.equals(IPropertyAccessor.METHOD_CONTAINS))
            return actual.contains(value);
        return false;
    }

}
