package net.sourceforge.marathon.ruby;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.StringWriter;
import java.util.Properties;

import net.sourceforge.marathon.ruby.RubyScriptModel;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.Failure;
import net.sourceforge.marathon.runtime.api.PlaybackResult;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.Block;
import org.jruby.runtime.builtin.IRubyObject;

@Test public class RubyScriptTest {

    private StringWriter out;
    private StringWriter err;
    private PlaybackResult result;

    private static final String[] SCRIPT_CONTENTS_ERROR_FROM_RUBY = { "print 'I am here'", "def my_function",
            "  raise NameError, 'Name error thrown'", "end" };

    private static final String[] SCRIPT_CONTENTS_ERROR_FROM_JAVA = { "print 'I am here'", "def my_function",
            "  include_class '" + RubyScriptTest.class.getCanonicalName() + "'", "  TestRubyScript.throwError", "end" };

    @BeforeMethod public void setUp() throws Exception {
        out = new StringWriter();
        err = new StringWriter();
        result = new PlaybackResult();
        createDir("./testDir");
        System.setProperty(Constants.PROP_PROJECT_DIR, new File("./testDir").getCanonicalPath());
        System.setProperty(Constants.PROP_FIXTURE_DIR, new File("./testDir").getCanonicalPath());
        System.setProperty(Constants.PROP_TEST_DIR, new File("./testDir").getCanonicalPath());
        System.setProperty(Constants.PROP_MODULE_DIRS, new File("./testDir").getCanonicalPath());
        System.setProperty(Constants.PROP_PROJECT_SCRIPT_MODEL, RubyScriptModel.class.getName());
        System.setProperty(Constants.PROP_PROJECT_NAME, "test_project");
        System.setProperty(Constants.PROP_HOME, "marathon-home");
    }

    private static File createDir(String name) {
        File file = new File(name);
        file.mkdir();
        return file;
    }

    @AfterMethod public void tearDown() throws Exception {
        Properties properties = System.getProperties();
        properties.remove(Constants.PROP_PROJECT_DIR);
        properties.remove(Constants.PROP_MODULE_DIRS);
        properties.remove(Constants.PROP_TEST_DIR);
        properties.remove(Constants.PROP_FIXTURE_DIR);
        properties.remove(Constants.PROP_PROJECT_SCRIPT_MODEL);
        properties.remove(Constants.PROP_PROJECT_NAME);
        properties.remove(Constants.PROP_HOME);
        System.setProperties(properties);
        deleteRecursive(new File("./testDir"));
    }

    private static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            for (int i = 0; i < list.length; i++) {
                deleteRecursive(list[i]);
            }
        }
        file.delete();
    }

    public void resultsLoaded() throws Throwable {
        try {
            RubyScript script = new RubyScript(out, err, converToCode(SCRIPT_CONTENTS_ERROR_FROM_RUBY),
                    new File(System.getProperty(Constants.PROP_PROJECT_DIR), "dummyfile.rb").getAbsolutePath(), false, null);
            script.setDriverURL("");
            Ruby interpreter = script.getInterpreter();
            assertTrue("Collector not defined", interpreter.isClassDefined("Collector"));
            RubyClass collectorClass = interpreter.getClass("Collector");
            IRubyObject presult = JavaEmbedUtils.javaToRuby(interpreter, result);
            IRubyObject collector = collectorClass.newInstance(interpreter.getCurrentContext(), new IRubyObject[] { null },
                    new Block(null));
            IRubyObject rubyObject = interpreter.evalScriptlet("proc { my_function }");
            try {
                collector.callMethod(interpreter.getCurrentContext(), "callprotected", new IRubyObject[] { rubyObject, presult });
            } catch (Throwable t) {

            }
            System.err.println(out);
            assertEquals(1, result.failureCount());
            Failure[] failures = result.failures();
            assertEquals(new File(System.getProperty(Constants.PROP_PROJECT_DIR), "dummyfile.rb").getAbsolutePath(),
                    failures[0].getTraceback()[0].fileName);
            assertEquals("my_function", failures[0].getTraceback()[0].functionName);
        } catch (Throwable t) {
            System.err.println("TestRubyScript.testResultsCapturesJavaError(): " + out.toString());
            System.err.println("TestRubyScript.testResultsCapturesJavaError(): " + out.toString());
            throw t;
        }
    }

    @Test(enabled = false) public void resultsCapturesJavaError() throws Exception {
        RubyScript script = new RubyScript(out, err, converToCode(SCRIPT_CONTENTS_ERROR_FROM_JAVA),
                new File(System.getProperty(Constants.PROP_PROJECT_DIR), "dummyfile.rb").getAbsolutePath(), false, null);
        script.setDriverURL("");
        Ruby interpreter = script.getInterpreter();
        assertTrue("Collector not defined", interpreter.isClassDefined("Collector"));
        RubyClass collectorClass = interpreter.getClass("Collector");
        IRubyObject presult = JavaEmbedUtils.javaToRuby(interpreter, result);
        IRubyObject collector = collectorClass.newInstance(interpreter.getCurrentContext(), new IRubyObject[0], new Block(null));
        IRubyObject rubyObject = interpreter.evalScriptlet("proc { my_function }");
        try {
            collector.callMethod(interpreter.getCurrentContext(), "callprotected", new IRubyObject[] { rubyObject, presult });
        } catch (Throwable t) {

        }
        assertEquals(1, result.failureCount());
        Failure[] failures = result.failures();
        assertTrue("Should end with TestRubyScript.java. but has " + failures[0].getTraceback()[0].fileName,
                failures[0].getTraceback()[0].fileName.endsWith("TestRubyScript.java"));
        assertEquals("throwError", failures[0].getTraceback()[0].functionName);
    }

    private String converToCode(String[] scriptContents) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < scriptContents.length; i++) {
            sb.append(scriptContents[i]).append("\n");
        }
        return sb.toString();
    }

}
