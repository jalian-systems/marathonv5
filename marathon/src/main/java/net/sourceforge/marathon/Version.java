package net.sourceforge.marathon;

import net.sourceforge.marathon.api.IVersion;

/**
 * Marathon version information. This tmpl file is used to generated
 * Version.java. Version.java is not part of the source code control.
 */
public class Version {
    private final static IVersion version;
    static {
        IVersion xversion = null ;
        try {
            xversion = (IVersion) Class.forName("Version").newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        version = xversion;
    }

    /**
     * No one can create a Version object
     * 
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private Version() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    }

    /**
     * @return the version given in the ant build file.
     */
    public static String id() {
        return version.id();
    }

    /**
     * @return the product name
     */
    public static String product() {
        return version.product();
    }

    /**
     * @return the timestamp of the given build.
     */
    public static String tstamp() {
        return version.tstamp();
    }

    public static String build() {
        return version.build();
    }

    public static String blurbTitle() {
        return version.blurbTitle();
    }

    public static String blurbCompany() {
        return version.blurbCompany();
    }

    public static String blurbWebsite() {
        return version.blurbWebsite();
    }

    public static String blurbCredits() {
        return version.blurbCredits();
    }
}
