package net.sourceforge.marathon.junit.textui;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import junit.framework.Test;

public interface IOutputter {
    public void output(Writer writer, Test testSuite, Map<Test, MarathonTestResult> testOutputMap) throws IOException;
}
