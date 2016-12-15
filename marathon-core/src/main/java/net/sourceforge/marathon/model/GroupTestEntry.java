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
package net.sourceforge.marathon.model;

import java.io.IOException;
import java.nio.file.Path;

import junit.framework.Test;
import junit.framework.TestCase;
import net.sourceforge.marathon.junit.MarathonDDTestSuite;
import net.sourceforge.marathon.junit.MarathonTestCase;
import net.sourceforge.marathon.junit.TestCreator;
import net.sourceforge.marathon.runtime.api.IConsole;

public class GroupTestEntry extends GroupEntry {

    private Test test;
    private Path path;

    public GroupTestEntry(String name) throws IOException {
        super(GroupEntryType.TEST, name);
        test = new TestCreator(false, null).getTest(name);
        if (test == null) {
            throw new IOException("Failed to create test: " + name);
        }
        path = getFilePath();
    }

    @Override public String getName() {
        if (test instanceof MarathonDDTestSuite)
            return ((MarathonDDTestSuite) test).getName();
        return ((TestCase) test).getName();
    }

    @Override public Path getFilePath() {
        if (test instanceof MarathonDDTestSuite)
            return ((MarathonDDTestSuite) test).getFile().toPath();
        return ((MarathonTestCase) test).getFile().toPath();
    }

    @Override public void setName(String name) {
        if (test instanceof MarathonDDTestSuite)
            ((MarathonDDTestSuite) test).setName(name);
        else
            ((TestCase) test).setName(name);
    }

    @Override public Test getTest(boolean acceptChecklist, IConsole console) throws IOException {
        return new TestCreator(acceptChecklist, console).getTest(super.getRawName());
    }

    @Override public boolean canPlaySingle() {
        return test instanceof TestCase;
    }

    @Override public String toString() {
        return getName();
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (path == null ? 0 : path.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GroupTestEntry other = (GroupTestEntry) obj;
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        return true;
    }

    @Override public void refresh() {
        // TODO Auto-generated method stub

    }

    @Override public void rename(String text) {
        if (test instanceof MarathonDDTestSuite)
            ((MarathonDDTestSuite) test).setName(text);
        else
            ((TestCase) test).setName(text);
    }

}
