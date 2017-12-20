/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.util;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Matches filenames to given pattern
 */
public class FilePatternMatcher {

    public static final Logger LOGGER = Logger.getLogger(FilePatternMatcher.class.getName());

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
        for (Pattern element : hiddenFiles) {
            if (element.matcher(name).matches()) {
                return true;
            }
        }
        return false;
    }
}
