package net.sourceforge.marathon.runtime.api;

public class Indent {
    private static String DEFAULT_INDENT;
    private static String INDENT;
    private static final String SPACES = "        ";

    static {
        INDENT = DEFAULT_INDENT = SPACES.substring(0, 4);
    }

    public static void setDefaultIndent(boolean convert, int tabSize) {
        if (convert) {
            INDENT = DEFAULT_INDENT = SPACES.substring(0, tabSize);
        } else {
            INDENT = DEFAULT_INDENT = "\t";
        }
    }

    public static String getDefaultIndent() {
        return DEFAULT_INDENT;
    }

    private static void setIndent(String iNDENT) {
        INDENT = iNDENT;
    }

    public static void incIndent() {
        setIndent(INDENT + DEFAULT_INDENT);
    }

    public static void decIndent() {
        INDENT = INDENT.replaceFirst(DEFAULT_INDENT, "");
    }

    public static String getIndent() {
        return INDENT;
    }
}
