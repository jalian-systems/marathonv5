#{{{ Marathon
require_fixture 'default'
#}}} Marathon

require 'pp'
require 'rspec'
require 'rspec/core/runner'

severity("normal")

class Listener
  def start(notification)
  end

  def example_started(notification)
    file_path   = File.absolute_path(notification.example.metadata[:file_path])
    line_number = notification.example.metadata[:line_number]
    desc = notification.example.metadata[:full_description]
    file_name = file_path[$spec_folder.length + 1, file_path.length].sub('.rb', '_' + line_number.to_s + '.rb')
    test_file_path = File.absolute_path($marathon_project_dir) + "/TestCases/" + file_name
    FileUtils.mkdir_p(File.dirname(test_file_path))
    File.open(test_file_path, 'w') { |file|
      file.puts(<<-RUBY)
\#{{{ Marathon
require_fixture 'default'
\#}}} Marathon

require 'rspec'
require 'rspec/core/runner'
require 'spec_wrapper'

name(#{file_name.inspect})
description(#{desc.inspect})
severity("normal")

def test

  RSpec.clear_examples
  RSpec::Core::Runner::run([ #{file_path.inspect}, '--example', #{desc.inspect} ], $stderr, $stdout)
  
end
RUBY
    }
  end

  def example_passed(notification)
  end

  def example_failed(notification)
  end
end

$fake = StringIO.new
RSpec.reset

RSpec.configure do |config|
  config.output_stream = $fake
  config.error_stream = $fake
  config.reporter.register_listener Listener.new, :example_started
  config.before(:all) do
      raise 'Fail each test immediately'
  end
end
RSpec.clear_examples

$spec_folder = File.absolute_path($marathon_project_dir + "/spec/")

def test

  RSpec::Core::Runner::run([ $spec_folder ], $fake, $fake)
  
end
