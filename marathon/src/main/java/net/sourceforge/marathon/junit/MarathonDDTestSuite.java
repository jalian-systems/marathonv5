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

    public MarathonDDTestSuite(File file, boolean acceptChecklist, IConsole console) throws IOException {
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
        if (!filename.startsWith(sourcePath))
            throw new IOException("Test file not in test directory");
        filename = filename.substring(sourcePath.length() + 1);
        filename = filename.replace(File.separatorChar, '.');
        return filename.substring(0, filename.length() - 3);
    }

    public String getName() {
        return name;
    }

    @Override public String toString() {
        return getName();
    }

}
