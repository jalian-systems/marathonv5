# $Id: results.rb 260 2009-01-13 05:53:15Z kd $
#
java_import 'net.sourceforge.marathon.runtime.api.SourceLine'

class Collector
  def initialize(runtime)
    @runtime = runtime
    @playbackresult = nil
    @exclude = nil
  end

  def callprotected(function, result, *args)
    @playbackresult = result
      begin
        function.call(*args)
		  return 1
		rescue NativeException => e
		  addjavaerror(e)
		  raise
    rescue EOFError => e
		  addrubyEOFerror(e)
		  raise
		rescue => e
		  addrubyerror(e)
		  raise
      end
      return 0
  end

  def addfailure(message, backtrace)
    _addfailure(message, backtrace, nil)
  end

  def addrubyerror(exception)
    _addfailure(exception.to_s + ": " + exception.message.to_s, exception.backtrace, nil)
  end

  def addrubyEOFerror(exception)
    _addfailure(exception.to_s + ": " + "Application may have aborted - use set_no_fail_on_exit to suppress this error", exception.backtrace, nil) if(@runtime != nil && @runtime.fail_on_exit)

  end

  def addjavaerror(exception)
    _addfailure(exception.message, exception.backtrace, exception.cause)
  end

  def _addfailure(message, backtrace, e)
    lines = convert(backtrace)
    @playbackresult.addFailure(message, lines.to_java(SourceLine), e)
    @runtime.saveScreenShotOnError
  end

  def convert(backtrace)
    backtrace.find_all { |item| not excluded(item) }.map { |item|
      item = item[6,item.length] if(item.index('file:/') == 0)
      matched = item.match(/(.*):(.*):(.*)/).to_a
      fname = "Unknown"
      fname = matched[3].match("`(.*)'").to_a[1] if matched[3]
      SourceLine.new(matched[1], fname, matched[2].to_i)
    }
  end

  def excluded(item)
    dir = File.absolute_path(java.lang.System.getProperty('marathon.project.dir'))
    item.index("uri:") == 0 || !item.include?(dir)
  end
end
