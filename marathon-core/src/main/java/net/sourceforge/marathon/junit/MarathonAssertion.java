/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.AssertionFailedError;
import net.sourceforge.marathon.runtime.api.Failure;

public class MarathonAssertion extends AssertionFailedError {
    private static final long serialVersionUID = 1L;
    private Failure[] failures;

    public MarathonAssertion(Failure[] failures, String testName) {
        super(failures != null && failures.length == 1 ? failures[0].getMessage() : "Multiple Failures");
        this.failures = failures;
    }

    public void printStackTrace() {
        super.printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream s) {
        super.printStackTrace(new PrintWriter(s));
    }

    public void printStackTrace(PrintWriter s) {
        StringWriter output;
        PrintWriter writer = new PrintWriter(output = new StringWriter());
        super.printStackTrace(writer);
        BufferedReader reader = new BufferedReader(new StringReader(output.toString()));
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        s.println(line);
        try {
            if (failures != null) {
                for (int i = 0; i < failures.length; i++) {
                    s.println("\tFailure: " + failures[i].getMessage());
                    if (failures[i].getTraceback().length > 0)
                        s.println("\tat " + failures[i].getTraceback()[0].functionName + "("
                                + getRelativeFileName(failures[i].getTraceback()[0].fileName) + ":"
                                + failures[i].getTraceback()[0].lineNumber + ")");
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (line != null) {
            s.println(line);
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getRelativeFileName(String fileName) throws IOException {
        String currentDir = new File(".").getCanonicalPath();
        fileName = new File(fileName).getCanonicalPath();
        if (fileName.startsWith(currentDir)) {
            fileName = fileName.substring(currentDir.length() + 1);
            if (fileName.equals(""))
                fileName = ".";
        }
        return fileName.replace('\\', '/');
    }

    public Failure[] getFailures() {
        return failures;
    }
}
