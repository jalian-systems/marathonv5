require 'rspec'
require 'rspec/core/runner'

severity("normal")

class Listener
  def start(notification)
  end

  def example_started(notification)
  end

  def example_passed(notification)
  end

  def example_failed(notification)
    file_path   = notification.example.metadata[:file_path]
    line_number = notification.example.metadata[:line_number]
    desc = notification.example.metadata[:full_description]
    kls = notification.example.metadata[:described_class]
    bt = "#{file_path}:#{line_number}:in `#{kls}'"
    $marathon.addfailure(desc, [bt])
  end
end

RSpec.reset
RSpec.clear_examples

RSpec.configure do |config|
  config.reporter.register_listener Listener.new, :example_failed
end

