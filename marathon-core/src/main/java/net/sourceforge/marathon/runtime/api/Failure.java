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
package net.sourceforge.marathon.runtime.api;

import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Logger;

public class Failure implements Serializable {

    public static final Logger LOGGER = Logger.getLogger(Failure.class.getName());

    private static final long serialVersionUID = 1L;
    private String message;
    private SourceLine[] traceback;
    private Throwable throwable;

    public Failure(String message, SourceLine[] traceback, Throwable throwable) {
        this.message = message;
        this.traceback = traceback;
        this.throwable = throwable;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message + "\n(" + Arrays.asList(traceback) + ")";
    }

    public SourceLine[] getTraceback() {
        return traceback;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getFileName() {
        return getSourceLine().fileName;
    }

    private SourceLine getSourceLine() {
        SourceLine line;
        if (traceback.length == 0 || traceback == null) {
            line = new SourceLine("", "", -1);
        } else {
            line = traceback[0];
        }
        return line;
    }

    public String getLineNumber() {
        return "line " + getSourceLine().lineNumber + " in function " + getSourceLine().functionName;
    }
}
