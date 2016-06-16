package net.sourceforge.marathon.runtime;

import net.sourceforge.marathon.api.INamingStrategy;
import net.sourceforge.marathon.objectmap.ObjectMapNamingStrategy;

public class NamingStrategyFactory {

    public static Class<? extends INamingStrategy> nsClass = ObjectMapNamingStrategy.class;

    public static INamingStrategy get() {
        try {
            return nsClass.newInstance();
        } catch (Throwable t) {
            throw new RuntimeException("Unable to create a new instance of naming strategy", t);
        }
    }

}
