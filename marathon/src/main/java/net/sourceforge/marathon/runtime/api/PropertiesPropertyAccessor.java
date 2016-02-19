package net.sourceforge.marathon.runtime.api;

import java.io.Serializable;
import java.util.Properties;

public class PropertiesPropertyAccessor extends DefaultMatcher implements Serializable {
    private static final long serialVersionUID = 1L;
    private Properties attributes;

    public PropertiesPropertyAccessor() {
    }

    public PropertiesPropertyAccessor(Properties attributes) {
        this.attributes = attributes;
    }

    @Override public String getProperty(String name) {
        return attributes.getProperty(name);
    }

}
