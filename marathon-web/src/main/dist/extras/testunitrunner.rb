require 'test/unit'
require 'test/unit/ui/console/testrunner'

class TestUnitRunner < Test::Unit::UI::Console::TestRunner
  def initialize(suite, options = {})
    super
  end

  def add_fault(fault)
    $fault = fault
    if(fault.is_a?(Test::Unit::Failure))
      $marathon.addfailure(fault.message, fault.location)
    else
      $marathon.addfailure(fault.exception.message, fault.exception.backtrace)
    end
    super
  end

end
