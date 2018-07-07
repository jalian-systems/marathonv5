package net.sourceforge.marathon.compat;

public enum JavaVersion {
    JAVA_6(6), JAVA_7(7), JAVA_8(8), JAVA_9(9), JAVA_10(10), JAVA_GT_10(Integer.MAX_VALUE);

    private int numeric;

    JavaVersion(int numeric) {
        this.numeric = numeric;
    }

    private static JavaVersion currentVersion;

    public static JavaVersion current() {
        if (currentVersion == null) {
            currentVersion = parseSystemProperty(System.getProperty("java.version"));
        }
        return currentVersion;
    }

    private static JavaVersion parseSystemProperty(String version) {
        if (version.startsWith("1.6"))
            return JAVA_6;
        else if (version.startsWith("1.7"))
            return JAVA_7;
        else if (version.startsWith("1.8"))
            return JAVA_8;
        else if (version.startsWith("9."))
            return JAVA_9;
        else if (version.startsWith("10."))
            return JAVA_10;
        return JAVA_GT_10;
    }

    public boolean greaterThan(JavaVersion version) {
        return numeric > version.numeric;
    }

    public boolean is(JavaVersion version) {
        return this == version;
    }
}
