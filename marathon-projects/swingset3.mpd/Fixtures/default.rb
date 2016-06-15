=begin
Launcher uses the properties specified here to launch the application
=end

#{{{ Fixture Properties
fixture_properties = {
        'marathon.project.launcher.model' => "net.sourceforge.marathon.runtime.RuntimeLauncherModel",
        'marathon.application.mainclass' => "com.sun.swingset3.SwingSet3",
        'marathon.application.arguments' => "",
        'marathon.application.vm.arguments' => "",
        'marathon.application.java.home' => "",
        'marathon.application.working.dir' => "",
        'marathon.application.classpath' => "%marathon.project.dir%/../swingset3/lib/AnimatedTransitions.jar;%marathon.project.dir%/../swingset3/lib/AppFramework.jar;%marathon.project.dir%/../swingset3/lib/TimingFramework.jar;%marathon.project.dir%/../swingset3/lib/javaws.jar;%marathon.project.dir%/../swingset3/lib/swing-worker.jar;%marathon.project.dir%/../swingset3/lib/swingx.jar;%marathon.project.dir%/../swingset3/SwingSet3.jar",
        'marathon.fixture.reuse' => ""
    }
#}}} Fixture Properties

=begin
Default Fixture
=end

class Fixture

=begin
    Marathon executes this method at the end of test script.
=end

    def teardown
        
    end

=begin
    Marathon executes this method before the test script.
=end

    def setup
    end

=begin
    Marathon executes this method after the first window of the application is displayed.
    You can add any Marathon script elements here.
=end

    def test_setup
        
    end

end

$fixture = Fixture.new

=begin
Any code you add below this comment is executed before the application is started.
You can use any ruby script here and not selenium and marathon script elements.
=end

