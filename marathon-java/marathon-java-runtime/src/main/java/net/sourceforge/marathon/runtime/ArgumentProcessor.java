package net.sourceforge.marathon.runtime;

import java.util.ArrayList;
import java.util.List;

public class ArgumentProcessor {

    private String fArgs;
    private int fIndex = 0;
    private int ch = -1;

    public ArgumentProcessor(String args) {
        fArgs = args;
    }

    public List<String> parseArguments() {
        List<String> v = new ArrayList<String>();

        ch = getNext();
        while (ch > 0) {
            if (Character.isWhitespace((char) ch)) {
                ch = getNext();
            } else {
                if (ch == '"') {
                    StringBuffer buf = new StringBuffer();
                    buf.append(parseString());
                    if (buf.length() == 0 && isWindows()) {
                        // empty string on windows platform
                        buf.append("\"\""); //$NON-NLS-1$
                    }
                    v.add(buf.toString());
                } else {
                    v.add(parseToken());
                }
            }
        }

        return v;
    }

    private boolean isWindows() {
        return false;
    }

    private int getNext() {
        if (fArgs != null && fIndex < fArgs.length())
            return fArgs.charAt(fIndex++);
        return -1;
    }

    private String parseString() {
        ch = getNext();
        if (ch == '"') {
            ch = getNext();
            return ""; //$NON-NLS-1$
        }
        StringBuffer buf = new StringBuffer();
        while (ch > 0 && ch != '"') {
            if (ch == '\\') {
                ch = getNext();
                if (ch != '"') { // Only escape double quotes
                    buf.append('\\');
                } else {
                    if (isWindows()) {
                        // @see Bug 26870. Windows requires an extra escape for
                        // embedded strings
                        buf.append('\\');
                    }
                }
            }
            if (ch > 0) {
                buf.append((char) ch);
                ch = getNext();
            }
        }
        ch = getNext();
        return buf.toString();
    }

    private String parseToken() {
        StringBuffer buf = new StringBuffer();

        while (ch > 0 && !Character.isWhitespace((char) ch)) {
            if (ch == '\\') {
                ch = getNext();
                if (Character.isWhitespace((char) ch)) {
                    // end of token, don't lose trailing backslash
                    buf.append('\\');
                    return buf.toString();
                }
                if (ch > 0) {
                    if (ch != '"') { // Only escape double quotes
                        buf.append('\\');
                    } else {
                        if (isWindows()) {
                            // @see Bug 26870. Windows requires an extra escape
                            // for embedded strings
                            buf.append('\\');
                        }
                    }
                    buf.append((char) ch);
                    ch = getNext();
                } else if (ch == -1) { // Don't lose a trailing backslash
                    buf.append('\\');
                }
            } else if (ch == '"') {
                buf.append(parseString());
            } else {
                buf.append((char) ch);
                ch = getNext();
            }
        }
        return buf.toString();
    }

}
