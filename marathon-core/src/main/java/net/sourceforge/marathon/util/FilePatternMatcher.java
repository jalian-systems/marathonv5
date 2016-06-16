package net.sourceforge.marathon.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Matches filenames to given pattern
 */
public class FilePatternMatcher {
    private ArrayList<Pattern> hiddenFiles = new ArrayList<Pattern>();

    /**
     * Construct a FilePatternMatcher. The matchPatterns is a space delimeted
     * string with regular expressions to match with.
     * 
     * @param matchPatterns
     */
    public FilePatternMatcher(String matchPatterns) {
        if (matchPatterns != null) {
            StringTokenizer toke = new StringTokenizer(matchPatterns);
            while (toke.hasMoreTokens()) {
                hiddenFiles.add(Pattern.compile(toke.nextToken()));
            }
        }
    }

    /**
     * Check whether the name of the given file matches the given pattern.
     * 
     * @param file
     * @return
     */
    public boolean isMatch(File file) {
        String name = file.getName();
        for (Iterator<Pattern> iter = hiddenFiles.iterator(); iter.hasNext();) {
            Pattern element = iter.next();
            if (element.matcher(name).matches())
                return true;
        }
        return false;
    }
}
