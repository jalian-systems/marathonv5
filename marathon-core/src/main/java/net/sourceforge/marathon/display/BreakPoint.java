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
package net.sourceforge.marathon.display;

import java.io.Serializable;
import java.util.logging.Logger;

public class BreakPoint implements Serializable {

    public static final Logger LOGGER = Logger.getLogger(BreakPoint.class.getName());

    private static final long serialVersionUID = 1L;

    private final int linenumber;
    private final String filePath;

    public BreakPoint(String filePath, int linenumber) {
        this.filePath = filePath;
        this.linenumber = linenumber;
    }

    @Override
    public int hashCode() {
        return (filePath + linenumber).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof BreakPoint && ((BreakPoint) obj).filePath.equals(filePath)
                && ((BreakPoint) obj).linenumber == linenumber;
    }

    public boolean shouldSave() {
        return !filePath.startsWith("Untitled");
    }
}
