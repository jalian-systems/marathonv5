# $Id: playback.rb 269 2009-01-16 08:07:57Z kd $
# Marathon JRuby Interfaces
#
#   Interface routines to hook into Marathon runtime. These calls are used
#   by Marathon to record operations on components. Some of the functions
#   are utility functions that can be used while developing the test
#   scripts.
#

java_import 'java.lang.System'
java_import 'net.sourceforge.marathon.ruby.MarathonRuby'
java_import 'net.sourceforge.marathon.runtime.api.Constants'
java_import 'net.sourceforge.marathon.api.TestAttributes'

require 'marathon/results'
require 'cgi'

class RubyMarathon < MarathonRuby

    def initialize(url)
      @collector = Collector.new(self)
    end

    def execTest(test)
        @collector.callprotected(test, result)
    end

    def execFixtureSetup
        setup = proc { $fixture.setup }
        @collector.callprotected(setup, result)
    end

    def execFixtureTeardown
        teardown = proc { $fixture.teardown }
        @collector.callprotected(teardown, result)
    end

    def execTestSetup
        return unless $fixture.respond_to? :test_setup
        setup = proc { $fixture.test_setup }
        @collector.callprotected(setup, result)
    end

    def execTestTeardown
        return unless $fixture.respond_to? :test_teardown
        teardown = proc { $fixture.test_teardown }
        @collector.callprotected(teardown, result)
    end

    def handleFailure(e)
        raise e if result == nil
        @collector.addfailure(e, result) unless e.isAbortTestCase
        raise e.getMessage if e.isAbortTestCase
    end

    # Methods overridden from MarathonJava
    def quit()
        cleanUp();
    end

    def assertTrue(message, b)
        message = 'Expected true: but was false' if !message
        begin
          throw
        rescue
          bt = @collector.convert($!.backtrace)
        end
        assertEquals(message, true.to_s, b.to_s, bt)
    end
    
    def assertFalse(message, b)
        message = 'Expected false: but was true' if !message
        begin
          throw
        rescue
          bt = @collector.convert($!.backtrace)
        end
        assertEquals(message, false.to_s, b.to_s, bt)
    end
    
    def fail(message)
      begin
        throw
      rescue
        bt = @collector.convert($!.backtrace)
      end
      failTest(message, bt)
    end

    def error(message)
      begin
        throw
      rescue
        bt = @collector.convert($!.backtrace)
      end
      errorTest(message, bt)
    end

    def convert(backtrace)
        @collector.convert(backtrace)
    end
end

# Sleep for the given number of seconds

def sleep(seconds)
    java.lang.Thread.sleep(seconds * 1000)
end

# Fail the test case with the given message
def fail(message)
    $marathon.fail(message)
end

def error(message)
    $marathon.error(message)
end

# Compare two images defined by their paths, returns their differences in an array [0] is no. of different pixels, [1] is the percentage.

def image_compare(path1, path2, differencesInPercent=0)
    return $marathon.compareImages(path1,path2,differencesInPercent)
end

def files_equal(path1, path2)
    return $marathon.filesEqual(path1, path2)
end

def assert_equals(expected, actual, message = nil)
    begin
      throw
    rescue
      bt = $marathon.convert($!.backtrace)
    end
    $marathon.assertEquals(message, expected, actual, bt)
end

def assert_false(actual, message = nil)
    $marathon.assertFalse(message, actual)
end

def assert_true(actual, message = nil)
    $marathon.assertTrue(message, actual)
end

def use_data_file(filename)
end

def with_data(filename)
    reader = $marathon.get_data_reader(filename, $marathon_script_handle)
    while(reader.read_next)
        yield
    end
end

$fixture_dir = System.getProperty(Constants::PROP_FIXTURE_DIR)

def require_fixture(s)
    load $fixture_dir + '/' + s + '.rb'
end

def name(arg)
  TestAttributes.put("marathon.test.name", arg)
end
  
def suite(arg)
  TestAttributes.put("marathon.suite.name", arg)
end

def id(arg)
  TestAttributes.put("marathon.test.id", arg)
end

def severity(arg)
  TestAttributes.put("marathon.test.severity", arg) if ['blocker', 'critical', 'normal', 'minor', 'trivial'].include? arg.downcase
end

def description(desc, type = 'text')
  TestAttributes.put("marathon.test.description", [ desc, type ].to_java(:String))
end

def issue(s)
  TestAttributes.put("marathon.test.issues", [ s ].to_java(:String))
end

def issues(s, *others)
  TestAttributes.put("marathon.test.issues", [ s, others ].flatten.to_java(:String))
end

def story(s)
  TestAttributes.put("marathon.test.stories", [ s ].to_java(:String))
end

def stories(s, *others)
  TestAttributes.put("marathon.test.stories", [ s, others ].flatten.to_java(:String))
end

def feature(s)
  TestAttributes.put("marathon.test.features", [ s ].to_java(:String))
end

def features(s, *others)
  TestAttributes.put("marathon.test.features", [ s, others ].flatten.to_java(:String))
end

def marathon_help
    
end

