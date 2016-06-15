package net.sourceforge.marathon.ruby;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.regex.Pattern;

import net.sourceforge.marathon.runtime.api.FixturePropertyHelper;

@Test public class FixturePropertyHelperTest {

    // @formatter:off
    private String typicalTestScript = 
            "#{{{ Marathon\n" +
            "require_fixture 'default'\n" +
            "#}}} Marathon\n" +
            "\n" +
            "def test\n" +
            "\n" +
            "    $java_recorded_version=\"1.6.0_26\"\n" +
            "    with_window(\"SwingSet2\") {\n" +
            "        select(\"JColorChooser\", \"true\")\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "end\n" +
            "\n"
            ;
    protected String fixtureProperties = 
        "#{{{ Fixture Properties\n" +
        "fixture_properties = {\n" +
        "       :main_class => 'SwingSet2',\n" +
        "       :program_arguments => '',\n" +
        "       :vm_arguments => '',\n" +
        "       :working_directory => '',\n" +
        "       :java_executable => '%java.home%/bin/java',\n" +
        "       :java_properties => { :user_name => 'KD' },\n" +
        "       :class_path => [ '%marathon.project.dir%/../tutorial/deployment/webstart/examples/webstart_AppWithCustomProgressIndicator/lib/SwingSet2.jar' ]\n" +
        "}\n" +
        "#}}}\n" ;

    // @formatter: on
    
    private static final Pattern FIXTURE_IMPORT_MATCHER = Pattern.compile("\\s*require_fixture\\s\\s*['\"](.*)['\"].*");
    private RubyScriptModel rubyScriptModel;

    @BeforeMethod public void setup() {
        rubyScriptModel = new RubyScriptModel();
    }
    
    public void findFixture() {
        FixturePropertyHelper model = new FixturePropertyHelper(rubyScriptModel) {
            @Override protected BufferedReader getFixtureReader(String fixture) {
                return new BufferedReader(new StringReader(fixtureProperties));
            }
        };
        AssertJUnit.assertEquals("default", model.findFixture(typicalTestScript, FIXTURE_IMPORT_MATCHER));
    }
    
    public void findFixtureProperties() {
        FixturePropertyHelper model = new FixturePropertyHelper(rubyScriptModel) {
            @Override protected String getFixturePropertiesPart(String fixture) {
                return fixtureProperties ;
            }
        };
        model.findFixtureProperties("default");
    }

    public void findFixtureProperties2() {
        FixturePropertyHelper model = new FixturePropertyHelper(rubyScriptModel) {
            @Override protected BufferedReader getFixtureReader(String fixture) {
                return new BufferedReader(new StringReader(fixtureProperties));
            }
        };
        model.findFixtureProperties("default");
    }
}
