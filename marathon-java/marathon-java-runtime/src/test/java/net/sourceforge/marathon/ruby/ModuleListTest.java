package net.sourceforge.marathon.ruby;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.marathon.runtime.api.Argument;
import net.sourceforge.marathon.runtime.api.Function;
import net.sourceforge.marathon.runtime.api.Module;

import org.jruby.Ruby;

@Test public class ModuleListTest {

    // @formatter:off
    private static final String[] TESTMODULE = { "=begin", "comment", "=end", "def module_function_1(f, s='Hello')", "end" };
    private static final String[] TESTMODULE_WITH_WINDOW = {
      "=begin",
      "This is an example with with_window call and comments",
      "=end",
      "",
      "def login(name='Name', password='Password')",
      "  with_window('Login') {",
      "    select('Name', name)",
      "    select('Password', password)",
      "  }",
      "end",
      "",
      "def login2(name='Name', password='Password')",
      "    select('Name', name)",
      "    select('Password', password)",
      "end",
    };
    private static final String[] TESTMODULE_WITH_ARRAY_PARAMS = {
        "=begin",
        "This is an example with module call with a list as parameters",
        "=end",
        "",
        "def select_one(integers=[1,2,3,4], strings=['One', 'Two', 'Three', 'Four'])",
        "end",
        "",
        "=begin",
        "This is an example with module call with a list as parameters that are not same type",
        "=end",
        "",
        "def select_two(array=[1,'One'])",
        "end",
        "",
        "=begin",
        "This is an example with boolean default values",
        "=end",
        "",
        "def select_true(truth_value=true, false_value=false)",
        "end",
        "",
        "=begin",
        "This is an example with boolean default values",
        "=end",
        "",
        "def select_false(false_value=false)",
        "end",
    };
    // @formatter:on

    @BeforeMethod public void setUp() throws Exception {
        new File("emptymoduledir").mkdirs();
        createModuleFile("moduledir/testmodule.rb", convert2code(TESTMODULE));
        createModuleFile("moduledir2/subdir/testmodule.rb", convert2code(TESTMODULE));
        createModuleFile("moduledir2/subdir.rb", convert2code(TESTMODULE));
        createModuleFile("authentication/login.rb", convert2code(TESTMODULE_WITH_WINDOW));
        createModuleFile("selection/arrays.rb", convert2code(TESTMODULE_WITH_ARRAY_PARAMS));
    }

    @AfterMethod public void tearDown() throws Exception {
        deleteDir(new File("emptymoduledir"));
        deleteDir(new File("moduledir"));
        deleteDir(new File("moduledir2"));
        deleteDir(new File("authentication"));
        deleteDir(new File("selection"));
    }

    private void createModuleFile(String filename, String code) throws IOException {
        StringTokenizer tok = new StringTokenizer(filename, "/");
        File parent = null;
        while (tok.hasMoreTokens()) {
            String name = tok.nextToken();
            if (tok.hasMoreTokens()) {
                if (parent == null) {
                    parent = new File(name);
                    parent.mkdir();
                } else {
                    parent = new File(parent, name);
                    parent.mkdir();
                }
            } else {
                FileOutputStream os = new FileOutputStream(new File(parent, name));
                os.write(code.getBytes());
                os.close();
            }
        }
    }

    private String convert2code(String[] code) {
        StringBuffer sb = new StringBuffer();
        String lineSepearator = System.getProperty("line.separator");
        for (int i = 0; i < code.length; i++) {
            sb.append(code[i]).append(lineSepearator);
        }
        return sb.toString();
    }

    private void deleteDir(File file) {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                deleteDir(listFiles[i]);
            }
        }
        file.delete();
    }

    public void moduleListReturnsEmptyArrayWhenNoModulesAreAvailable() throws Exception {
        ModuleList moduleList = new ModuleList(Ruby.newInstance(), "emptymoduledir");
        Module top = moduleList.getTop();
        assertEquals(0, top.getChildren().size());
        assertEquals(0, top.getFunctions().size());
    }

    public void returnsFunctionsFromAFile() throws Exception {
        ModuleList moduleList = new ModuleList(Ruby.newInstance(), "moduledir");
        Module top = moduleList.getTop();
        assertEquals(1, top.getChildren().size());
        assertEquals(0, top.getFunctions().size());
        Module module = top.getChildren().get(0);
        assertEquals(1, module.getFunctions().size());
        assertEquals(0, module.getChildren().size());
        Function f = module.getFunctions().get(0);
        assertEquals("module_function_1", f.getName());
        assertEquals("f", f.getArguments().get(0).getName());
        assertEquals("s", f.getArguments().get(1).getName());
        assertEquals(null, f.getArguments().get(0).getDefault());
        assertEquals("Hello", f.getArguments().get(1).getDefault());
    }

    public void returnsFunctionsFromSubdirectories() throws Exception {
        ModuleList moduleList = new ModuleList(Ruby.newInstance(), "moduledir2");
        Module top = moduleList.getTop();
        assertEquals(2, top.getChildren().size());
        Module subdir = top.getChildren().get(0);
        assertEquals("subdir", subdir.getName());
        Module subdir2 = top.getChildren().get(1);
        assertEquals("subdir", subdir2.getName());
    }

    public void checkForWindowNameAndComments() throws Exception {
        ModuleList moduleList = new ModuleList(Ruby.newInstance(), "authentication");
        Module top = moduleList.getTop();
        List<Module> modules = top.getChildren();
        assertEquals(1, modules.size());
        Module login = modules.get(0);
        assertEquals("login", login.getName());
        List<Function> functions = login.getFunctions();
        assertEquals(2, functions.size());
        Function loginFunction = functions.get(0);
        String lineSepearator = System.getProperty("line.separator");
        assertEquals(
                convert2code(new String[] { "=begin", "This is an example with with_window call and comments", "=end" }).trim(),
                loginFunction.getDocumentation().trim().replaceAll("\n", lineSepearator));
        assertEquals("Login", loginFunction.getWindow());
        Function loginFunction2 = functions.get(1);
        assertEquals("", loginFunction2.getDocumentation());
        assertNull(loginFunction2.getWindow());
    }

    public void checkForListArgs() throws Exception {
        ModuleList moduleList = new ModuleList(Ruby.newInstance(), "selection");
        Module top = moduleList.getTop();
        List<Module> modules = top.getChildren();
        assertEquals(1, modules.size());
        Module arrays = modules.get(0);
        assertEquals("arrays", arrays.getName());
        List<Function> functions = arrays.getFunctions();
        assertEquals(3, functions.size());
        Function selectOneFunction = functions.get(0);
        assertEquals("select_one", selectOneFunction.getName());
        List<Argument> arguments = selectOneFunction.getArguments();
        assertEquals(2, arguments.size());
        Argument arg = arguments.get(0);
        assertEquals("integers", arg.getName());
        assertNull(arg.getDefault());
        assertNotNull(arg.getDefaultList());
        arg = arguments.get(1);
        assertEquals("strings", arg.getName());
        assertNull(arg.getDefault(), arg.getDefault());
        assertNotNull(arg.getDefaultList());
    }
}
