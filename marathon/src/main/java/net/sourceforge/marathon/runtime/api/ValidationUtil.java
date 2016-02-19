package net.sourceforge.marathon.runtime.api;

import java.util.StringTokenizer;

public class ValidationUtil {

    public static boolean isValidClassName(String className) {
        if (className.contains(".."))
            return false;
        StringTokenizer tok = new StringTokenizer(className, ".");
        while (tok.hasMoreTokens()) {
            if (!ValidationUtil.isValidIdentifier(tok.nextToken()))
                return false;
        }
        return true;
    }

    public static boolean isValidIdentifier(String part) {
        char[] cs = part.toCharArray();
        if (cs.length == 0 || !Character.isJavaIdentifierStart(cs[0]))
            return false;
        for (int i = 1; i < cs.length; i++) {
            if (!Character.isJavaIdentifierPart(cs[i]))
                return false;
        }
        return true;
    }

    public static boolean isValidMethodName(String text) {
        return isValidIdentifier(text);
    }

}
