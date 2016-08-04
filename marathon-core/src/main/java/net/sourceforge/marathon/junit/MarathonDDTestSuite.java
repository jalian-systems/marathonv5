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
package net.sourceforge.marathon.junit;

import java.io.File;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IConsole;

public class MarathonDDTestSuite extends TestSuite implements Test {
    protected DDTestRunner ddt;
    private String name;
    private File file;

    public MarathonDDTestSuite(File file, boolean acceptChecklist, IConsole console) throws IOException {
        this.file = file;
        this.name = createDotFormat(file);
        ddt = new DDTestRunner(console, file);
        while (ddt.hasNext()) {
            ddt.next();
            Test testCase = createDDTTest(file, acceptChecklist, console);
            super.addTest(testCase);
        }
    }

    protected Test createDDTTest(File file, boolean acceptChecklist, IConsole console) {
        MarathonTestCase marathonTestCase = new MarathonTestCase(file, acceptChecklist, console, ddt.getDataVariables(),
                ddt.getName());
        marathonTestCase.setFullName(name + ddt.getName());
        return marathonTestCase;
    }

    private String createDotFormat(File file) throws IOException {
        String sourcePath = new File(System.getProperty(Constants.PROP_TEST_DIR)).getCanonicalPath();
        String filename = file.getCanonicalPath();
        if (!filename.startsWith(sourcePath)) {
            throw new IOException("Test file not in test directory");
        }
        filename = filename.substring(sourcePath.length() + 1);
        filename = filename.replace(File.separatorChar, '.');
        return filename.substring(0, filename.length() - 3);
    }

    @Override public String getName() {
        return name;
    }

    @Override public String toString() {
        return getName();
    }

    public File getFile() {
        return file;
    }
}
