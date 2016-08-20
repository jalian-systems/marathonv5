# $Id: playback.rb 269 2009-01-16 08:07:57Z kd $
# Marathon JRuby Interfaces
#
#   Interface routines to hook into Marathon runtime. These calls are used
#   by Marathon to record operations on components. Some of the functions
#   are utility functions that can be used while developing the test
#   scripts.
#

java_import 'java.lang.System'
java_import 'net.sourceforge.marathon.runtime.api.ComponentId'
java_import 'net.sourceforge.marathon.ruby.MarathonRuby'
java_import 'net.sourceforge.marathon.runtime.api.ChooserHelper'
java_import 'net.sourceforge.marathon.runtime.api.Constants'
java_import 'net.sourceforge.marathon.api.TestAttributes'
java_import 'org.json.JSONObject'
java_import 'net.sourceforge.marathon.display.WaitMessageDialog'

require 'marathon/results'
require 'json'

LOGGER = java.util.logging.Logger.getLogger('net.sourceforge.marathon.ruby.MarathonRuby.PlaybackWeb')

class MyBenchMark
  def report(s)
    WaitMessageDialog.setVisible(true, s)
    begin_time = Time.now
    begin
      yield
    ensure
      end_time = Time.now
      LOGGER.info(s + ": #{(end_time - begin_time)} seconds")
    end
  end

  def close
    WaitMessageDialog.setVisible(false);
  end
end

class Wait

  attr_accessor :message

  DEFAULT_TIMEOUT  = 5
  DEFAULT_INTERVAL = 0.2

  #
  # Create a new Wait instance
  #
  # @param [Hash] opts Options for this instance
  # @option opts [Numeric] :timeout (5) Seconds to wait before timing out.
  # @option opts [Numeric] :interval (0.2) Seconds to sleep between polls.
  # @option opts [String] :message Exception mesage if timed out.
  # @option opts [Array, Exception] :ignore Exceptions to ignore while polling (default: Error::NoSuchElementError)
  #
  def initialize(opts = {})
    @timeout  = opts.fetch(:timeout, DEFAULT_TIMEOUT)
    @interval = opts.fetch(:interval, DEFAULT_INTERVAL)
    @message  = opts[:message]
    @ignored  = Array(opts[:ignore] || Selenium::WebDriver::Error::NoSuchElementError)
  end

  #
  # Wait until the given block returns a true value.
  #
  # @raise [Error::TimeOutError]
  # @return [Object] the result of the block
  #

  def until(&blk)
    end_time = Time.now + @timeout
    last_error = nil

    until Time.now > end_time
      begin
        result = yield
        return result if result
      rescue *@ignored => last_error
        # swallowed
      end

      sleep @interval
    end

    if @message
      msg = @message.dup unless @message.respond_to?(:call)
      msg = @message.call if @message.respond_to?(:call)
    else
      msg = "timed out after #{@timeout} seconds"
    end

    msg << " (#{last_error.message})" if last_error

    raise Selenium::WebDriver::Error::TimeOutError, msg
  end

end # Wait

class RubyMarathon < MarathonRuby

  attr_reader :dndCopyKey
  field_accessor :namingStrategy
  def initialize(url)
    puts 'Initialize ' + url
    @_select = Proc.new { |e, text|
      tag = e.tag_name
  
      if(tag.downcase == 'input')
        type = e.attribute('type').downcase
        matched = ["text", "password", "color", "date", "datetime", "datetime-local",
          "number", "range", "search", "tel", "time", "url",
          "week", "email", "file", "month"].find { |e| e == type }
  
        if(matched != nil)
          e.clear
          e.send_keys text
          next
        end
        matched = ['radio', 'checkbox'].find { |e| e == type }
        if(matched != nil)
          selected = e.selected?.to_s
          if(text.downcase != selected.downcase)
            e.click
          end
          next
        end
      end
  
      if(tag.downcase == 'textarea')
        e.clear
        e.send_keys text
        next
      end
  
      if(tag.downcase == 'select')
        option = Selenium::WebDriver::Support::Select.new(e)
        JSON.parse(text).each { |v|
          option.select_by(:text, v)
        }
      end
    }
    
    @resolvers = []
    @resolvers.push({ :can_handle => Proc.new { |e| true }, :select => @_select })

    resolvers_dir = File.join(System.getProperty('marathon.project.dir', '.'), 'extensions')
    if(Dir.exist?(resolvers_dir))
      Dir.glob(resolvers_dir  + '/enabled-*.rb') { |f|
        resolver = eval(File.read(f))
        @resolvers.unshift(resolver)
      }
    end
    @context_handles = []
    @refresh_if_stale = true
    @component_wait_ms = System.getProperty("marathon.COMPONENT_WAIT_MS", "30000").to_i
    @document_wait_time = 1
    if(url.length != 0)
      caps = Selenium::WebDriver::Remote::Capabilities.new
      @webdriver = Selenium::WebDriver.for(:remote, :url => url, :desired_capabilities => caps)
      if TestAttributes.get("marathon.profile.url") != nil
        @webdriver.get(TestAttributes.get("marathon.profile.url"))
      end
    end
    @current_search_context = @webdriver
    @collector = Collector.new(self)
    @no_fail_on_exit = false
    @dndCopyKey = :control
    begin
      @dndCopyKey = :alt if /Darwin/.match(`uname`)
    rescue
    end
    if System.getProperty("marathon.recording.port", "") != ""
      @scriptText = java.lang.Object.new.java_class.resource_as_string('/Marathon.js')
      @scriptText += java.lang.Object.new.java_class.resource_as_string('/PopUpWindow.js')
      @scriptText += java.lang.Object.new.java_class.resource_as_string('/AssertionWindow.js')
      @scriptText += java.lang.Object.new.java_class.resource_as_string('/initmarathon.js')
      if(Dir.exist?(resolvers_dir))
        Dir.glob(resolvers_dir  + '/enabled-*.js') { |f|
          @scriptText += File.read(f)
        }
      end
      @port = System.getProperty("marathon.recording.port").to_i
      load_script
    end
  end

  def set_document_wait_time(seconds)
    @document_wait_time = seconds if @document_wait_time > 0
  end

  def load_script
    begin
      WaitMessageDialog.setVisible(true, "Loading scripts...")
      bmark = MyBenchMark.new
      window_container = ""
      bmark.report("Waiting for document ready") {
        wait = Wait.new(:timeout => 30)
        wait.until {
          begin
            @webdriver.execute_script("return document.readyState == 'complete';")
          rescue
            false
          end
        }
      }
      if(@document_wait_time == 1)
        WaitMessageDialog.setVisible(true, "Waiting for " + @document_wait_time.to_s + " second")
      else
        WaitMessageDialog.setVisible(true, "Waiting for " + @document_wait_time.to_s + " seconds")
      end
      java.lang.Thread.sleep(@document_wait_time * 1000)
      bmark.report("Loading script to main") { @webdriver.execute_script(@scriptText, @port) }
      bmark.report("Waiting for omapLoad") {
        wait = Wait.new(:timeout => 30)
        wait.until {
          @webdriver.execute_script("return $marathon.omapLoaded;")
        }
      }
      bmark.report("Getting window container") {
        window_container = @webdriver.execute_script("return $marathon.getContainer(window);")
      }
      frames = []
      bmark.report("Finding iframes in main") {
        frames = @webdriver.find_elements(:tag_name, 'iframe')
      }
      frames.each { |f|
        load_script_frame(f, window_container, [], bmark)
        bmark.report("Switching to default content") {
          @webdriver.switch_to.default_content
        }
      }
    ensure
      WaitMessageDialog.setVisible(false);
    end
  end

  def load_script_frame(frame, parent_container, framestack, bmark)
    title = frame.attribute('title')
    frame_identity = nil
    bmark.report("Getting objectIdentity for " + title) {
      frame_identity = @webdriver.execute_script("return $marathon.getObjectIdentity(arguments[0]);", frame)
    }
    bmark.report("Switching to frame " + title) {
      @webdriver.switch_to.frame(frame)
    }
    bmark.report("Loading script for " + title) {
      @webdriver.execute_script(@scriptText, @port)
      @webdriver.execute_script("$marathon.setParentContainer(arguments[0]);", parent_container.to_json.to_s)
      @webdriver.execute_script("$marathon.setContainerIdentity(arguments[0]);", frame_identity.to_json.to_s)
    }
    bmark.report("Waiting for omapLoad for frame " + title) {
      wait = Wait.new(:timeout => 30)
      wait.until {
        @webdriver.execute_script("return $marathon.omapLoaded;")
      }
    }
    bmark.report("Getting container for " + title) {
      parent_container = @webdriver.execute_script("return $marathon.getContainer(window);")
    }
    frames = []
    bmark.report("Finding iframes in " + title) {
      frames = @webdriver.find_elements(:tag_name, 'iframe')
    }
    framestack.push(frame)
    frames.each { |f|
      load_script_frame(f, parent_container, framestack, bmark)
      bmark.report("Switching to default content in load_script_frame for " + title) {
        @webdriver.switch_to.default_content
      }
      bmark.report("Switching to frames in framestack " + title) {
        framestack.each{ |f| @webdriver.switch_to.frame(f) }
      }
    }
  end

  def set_refresh_if_stale(b)
    @refresh_if_stale = b
  end

  def refresh_if_stale(o)
    return o unless @refresh_if_stale
    begin
      o.enabled? if o.respond_to? :search_context
      return o
    rescue Selenium::WebDriver::Error::StaleElementReferenceError => e
      LOGGER.info("Recreating element with " + o.search_context.to_s + " with " + o.recognition_properties.to_s)
      return find_element(o.search_context, o.recognition_properties)
    end
  end

  def onWSConnectionClose(port)
    LOGGER.info("onWSConnectionClose: Loading Script")
    load_script
  end

  def execute_playback_script
    @webdriver.execute_script(@script_pb)
  end

  def driver()
    @webdriver
  end

  def fail_on_exit
    ! @no_fail_on_exit
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

  def getCurrentContext()
    @current_search_context
  end

  def handleFailure(e)
    raise e if result == nil
    @collector.addfailure(e, result) unless e.isAbortTestCase
    raise e.getMessage if e.isAbortTestCase
  end

  # Methods overridden from MarathonJava
  def quit()
    @webdriver.quit()
    cleanUp();
  end

  def context(title)
    search_context = @current_search_context
    @current_search_context = get_leaf_component(ComponentId.new(title, nil))
    namingStrategy.setTopLevelComponent(getContextAsAccessor(@current_search_context))
    @context_handles.push( search_context )
  end

  def frame(title, timeout)
    LOGGER.info("frame('" + title + "', " + timeout.to_s + ")") 
    search_context = get_leaf_component(ComponentId.new(title, nil))
    @webdriver.switch_to.frame search_context
    @current_search_context = @webdriver
    namingStrategy.setTopLevelComponent(getDriverAsAccessor())
    @context_handles.push( Proc.new { frame(title, timeout) } )
  end

  def window(title, timeout)
    LOGGER.info("window('" + title + "', " + timeout.to_s + ")") 
    bmark = MyBenchMark.new
    bmark.report("Waiting for window '" + title + "'") {
      wait = Wait.new(:timeout => 30, :message => "Waiting for window '" + title + "'")
      begin
        wait.until {
          waitSucceeded = false
          @webdriver.window_handles.each { |h|
            @webdriver.switch_to.window(h) unless waitSucceeded
            waitSucceeded = true if windowMatchingTitle(title)
          }
          waitSucceeded
        }
      ensure
        bmark.close
      end
    }
    @current_search_context = @webdriver
    namingStrategy.setTopLevelComponent(getDriverAsAccessor())
    @context_handles.push( Proc.new { window(title, timeout) } )
  end

  def close
    popped = @context_handles.pop
    LOGGER.info("closed(" + popped.to_s + ")")
    return if @context_handles.size == 0
    last = @context_handles.last
    if(last.is_a? Proc)
      handles = @context_handles.clone
      @current_search_context = Proc.new {
        @context_handles.clear
        handles.each { |h| h.call }
      }
    else
      @current_search_context = last
      namingStrategy.setTopLevelComponent(getContextAsAccessor(@current_search_context))
    end
    LOGGER.info("closed(search_context = " + @current_search_context.to_s + ")")
  end

  def getWindowDetails
    r = JSONObject.new
    location = @webdriver.execute_script('return location')
    r.put('location.href', location['href'])
    r.put('location.origin', location['origin'])

    r.put('location.protocol', location['protocol'])
    r.put('location.host', location['host'])
    r.put('location.hostname', location['hostname'])
    r.put('location.port', location['port'])
    r.put('location.pathname', location['pathname'])
    r.put('location.search', location['search'])
    r.put('document.URL', @webdriver.execute_script('return document.URL'))
    r.put('document.title', @webdriver.execute_script('return document.title'))
    r.put('current_url', @webdriver.current_url)
    r.put('title', @webdriver.title)
    return r.to_s
  end

  def get_leaf_component(id)
    if @current_search_context.is_a?(Proc)
      LOGGER.info("Calling search context proc")
      @current_search_context.call
    end

    omapComponent = namingStrategy.getOMapComponent(id)
    rps_raw = omapComponent.getComponentRecognitionProperties().to_a
    rps = rps_raw.map { |rp_raw|
      { :name => rp_raw.name, :method => rp_raw.method, :value => rp_raw.value }
    }
    
    if(@refresh_if_stale && @current_search_context == @webdriver && @context_handles.size > 1)
      handles = @context_handles.clone
      @context_handles.clear
      handles.each { |h| h.call }
    else
      @current_search_context = refresh_if_stale(@current_search_context)
    end
    find_element(@current_search_context, rps)
  end

  def find_element(search_context, rps)
    recognition_properties = rps.dup
    se_prop = ['id', 'name', 'link_text', 'partial_link_text', 'css', 'xpath', 'class_name', 'tag_name'].find { |f| rps.find { |rp| rp[:name] === f && rp[:method] == 'equals' } }
    if se_prop
      se_rp = rps.find { |rp| rp[:name] === se_prop }
      rps.keep_if { |rp| rp[:name] != se_prop }
    else
      se_rp = { :name => 'css', :value => '*', :method => 'equals' }
    end
    LOGGER.info('Finding elements using: ' + se_rp.to_s + ' and then ' + rps.to_s)
    matched = []
    wait = Wait.new(:timeout => @component_wait_ms/1000, :message => 'Unable to find an element using ' + recognition_properties.to_s)
    wait.until {
      matched = search_context.find_elements(se_rp[:name].to_sym, se_rp[:value])
      matched = filter_using_rps(matched, rps)
      wait.message = Proc.new {
        msg = "More than one component matched for " + recognition_properties.to_s + "\n"
        begin
          matched.each { |match|
            msg << "    " << @webdriver.execute_script('var txt, el= document.createElement("div"); el.appendChild(arguments[0].cloneNode(false)); txt= el.innerHTML; el= null; return txt.replace(/><.*/g,">");
  ', match) << "\n"
          }
        rescue
        end
        msg
      } if matched.length > 1
      wait.message = "No elements matched for " + recognition_properties.to_s if matched.length == 0
      matched.length == 1
    }
    e = matched[0]
    e.define_singleton_method(:search_context) { search_context }
    e.define_singleton_method(:recognition_properties) { recognition_properties }
    e
  end

  def setContext(o)
    LOGGER.info("Setting context...")
    @current_search_context = o
  end

  def filter_using_rps(matched, rps)
    rps.each { |rp|
      matched = filter_using_rp(matched, rp)
    }
    matched
  end

  def filter_using_rp(matched, rp)
    matched.find_all { |e|
      rp_matches(e, rp[:name], rp[:value], rp[:method])
    }
  end

  def rp_matches(e, name, value, method)
    actual = value_of(name, e);
    return false unless actual
    case method
    when 'equals'
      actual === value
    when 'startsWith'
      actual.start_with? value
    when 'endsWith'
      actual.end_with? value
    when 'contains'
      actual.index(value) != nil
    when 'matches'
      actual.match(value)
    else
      false
    end
  end

  def value_of(name, e)
    if name != 'class' && e.respond_to?(name)
      e.send(name)
    else
      e.attribute(name)
    end
  end

  def clickInternal(id, position, clickCount, modifiers, popupTrigger)
    e = get_leaf_component(id)
    action = @webdriver.action
    if(position == nil)
      action.move_to(e)
    else
      action.move_to(e, position.x, position.y)
    end
    if modifiers != nil
      modifiers.split('+').each { |m|
        action.key_down :shift if m == 'Shift'
        action.key_down :alt if m == 'Alt'
        action.key_down :meta if m == 'Meta'
        action.key_down :control if m == 'Ctrl'
      }
    end
    if popupTrigger
      action.context_click
    else
      if clickCount == 1
        action.click
      else
        action.double_click
      end
    end
    if modifiers != nil
      modifiers.split('+').each { |m|
        action.key_up :shift if m == 'Shift'
        action.key_up :alt if m == 'Alt'
        action.key_up :meta if m == 'Meta'
        action.key_up :control if m == 'Ctrl'
      }
    end
    wait = Wait.new(:timeout => @component_wait_ms/1000)
    wait.until {
	    begin
	      action.perform
	      true
	    rescue Exception => exc
	      LOGGER.warning('Retrying with scrollIntoView due to ' + exc.message)
	      begin
	      	@webdriver.execute_script('arguments[0].scrollIntoView(false);', e)
	      rescue
	      end
	      false
	    end
    }
  end

  def dragInternal(id, modifiers, startPos, endPos)
    e = get_leaf_component(id)
    action = @webdriver.action
    if modifiers != nil
      modifiers.split('+').each { |m|
        action.key_down :shift if m == 'Shift'
        action.key_down :alt if m == 'Alt'
        action.key_down :meta if m == 'Meta'
        action.key_down :control if m == 'Ctrl'
      }
    end
    action.move_to(e, startPos.x, startPos.y).click_and_hold.move_to(e, endPos.x, endPos.y).release
    if modifiers != nil
      modifiers.split('+').each { |m|
        action.key_up :shift if m == 'Shift'
        action.key_up :alt if m == 'Alt'
        action.key_up :meta if m == 'Meta'
        action.key_up :control if m == 'Ctrl'
      }
    end
    action.perform
  end

  def dragAndDrop(source, target, dndAction)
    e1 = get_leaf_component(source)
    e2 = get_leaf_component(target)
    action = @webdriver.action
    if dndAction == 'copy'
      action.key_down @dndCopyKey
      java.lang.Thread.sleep 50
    end
    action.click_and_hold(e1)
    action.move_to(e2).release
    if dndAction == 'copy'
      action.key_up @dndCopyKey
    end
    action.perform
  end

  def assertProperty(id, property, expected)
    actual = getProperty(id, property)
    begin
      throw
    rescue
      bt = @collector.convert($!.backtrace)
    end
    actual = "" if !actual
    assertEquals("Assertion failed: component = " + id.to_s + "\n     expected = `" + expected + "'\n     actual = `" + actual + "'",
    expected, actual, bt)
  end

  def assertPropertyContains(id, property, expected)
    actual = getProperty(id, property)
    begin
      throw
    rescue
      bt = @collector.convert($!.backtrace)
    end
    actual = "" if !actual
    assertContains("Assertion failed: component = " + id.to_s + "\n     expected = `" + expected + "'\n     actual = `" + actual + "'",
    expected, actual, bt)
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

  def waitProperty(id, property, expected)
    e = get_leaf_component(id)
    begin
      throw
    rescue
      bt = @collector.convert($!.backtrace)
    end
    wait = Wait.new(:timeout => @component_wait_ms/1000)
    wait.until {
      expected == e.attribute(property)
    }
  end

  def set_component_wait_ms(ms)
    @component_wait_ms = ms if @component_wait_ms >= 0
  end

  def convert(backtrace)
    @collector.convert(backtrace)
  end

  def assertContent(id, expected)
    e = get_leaf_component(id)
    actual = e.attribute "content"
    assertContentJava(expected.to_java([].to_java(:String).class), actual)
  end

  def keystroke(id, keys)
    sleepForSlowPlay
    e = get_leaf_component(id)
    e.send_keys getCharSequence(keys)
  end

  def selectString(id, text)
    e = get_leaf_component(id)
    @resolvers.each { |resolver|
      if(resolver[:can_handle].call(e))
        resolver[:select].call(e, text)
        break
      end
    }
  end
  
  def getComponent(id)
    get_leaf_component(id)
  end

  def getProperty(id, property)
    e = get_leaf_component(id)
    if(property != 'class' && e.respond_to?(property))
      e.send(property).to_s
    else
      e.attribute property
    end
  end

  def getComponent_any(id)
    return namingStrategy.toCSS(id, false)[0]
  end

  def set_no_fail_on_exit(b)
    @no_fail_on_exit = b
  end

  def saveScreenShot(path)
    @webdriver.save_screenshot(path)
    return true
  end

  def saveScreenShotOnError
    f = getErrorScreenShotFile
    @webdriver.save_screenshot(f) if f
  end

  def hover
  end

end

# Wait for a window to appear. The default timeout is 30seconds

def with_window(windowTitle, timeout = 0)
  $marathon.window(windowTitle, timeout)
  yield
  $marathon.close
  return true
end

def with_context(title)
  $marathon.context(title)
  yield
  $marathon.close
  return true
end

# Wait for a internal frame to appear. The default timeout is 30seconds

def with_frame(windowTitle, timeout = 0)
  $marathon.frame(windowTitle, timeout)
  yield
  $marathon.close
  return true
end

# Pop the window out of the stack. The next operation takes place
# only when the Window below the stack is focused or a new Window
# call is made.
#

def close
  $marathon.close
end

# Send the given keysequence to the application. Keysequence are
# of the form [modifier]+[modifier]+...+[keystroke]. If the given
# keysequence is a single character like 'A' - the corresponding
# keystroke (Shift+A) is sent to the application.

def keystroke(componentName, keysequence, componentInfo=nil)
  $marathon.keystroke(ComponentId.new(componentName, componentInfo), keysequence)
end

# Send a click to the component

def click(componentName, o1 = nil, o2 = nil, o3 = nil, o4 = nil, o5 = nil)
  $marathon.click(componentName, false, o1, o2, o3, o4, o5)
end

# Send a click to the component

def hover(componentName, delay = 500, componentInfo = nil)
  driver.action.move_to(get_component(componentName, componentInfo)).perform
  java.lang.Thread.sleep(delay)
end

# Send a drag to the component

def drag(componentName, o1, o2, o3, o4, o5 = nil, o6 = nil)
  $marathon.drag(componentName, o1, o2, o3, o4, o5, o6)
end

# Send a double click to the component

def doubleclick(componentName, o1 = nil, o2 = nil, o3 = nil, o4 = nil)
  $marathon.click(componentName, false, 2, o1, o2, o3, o4)
end

# Send a right click to the component

def rightclick(componentName, o1 = nil, o2 = nil, o3 = nil, o4 = nil, o5 = nil)
  $marathon.click(componentName, true, o1, o2, o3, o4, o5)
end

# Select a given component and set the state corresponding to the given text.

def select(componentName, text, componentInfo=nil)
  $marathon.select(ComponentId.new(componentName, componentInfo), text)
end

# Get the Java component represented by the given name that is visible and showing

def get_component(componentName, componentInfo=nil)
  return $marathon.getComponent(ComponentId.new(componentName, componentInfo))
end

# Get the Java component represented by the given name

def get_component_any(componentName, componentInfo=nil)
  return $marathon.getComponent_any(ComponentId.new(componentName, componentInfo))
end

# Get a map that contains components for the current window

def get_named_components()
  r = Hash.new
  names = get_component_names
  names.each { |n|
    begin
      r[n] = get_component_any(n)
    rescue
    end
  }
  return r
end

def get_component_names()
  return $marathon.getComponentNames()
end

# Get a list of all visible components in the current window
def dump_components()
  return driver.find_elements(:css, '*')
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

# Gets the title of the current window

def get_window()
  begin
    driver.title
  rescue
    nil
  end
end

# Gets the current window

def get_window_object()
  driver.manage.window
end

# Recording sequence for a drag and drop operation. Marathon uses a Clipboard copy and paste
# to perform the operation.

def drag_and_drop(source, sourceinfo, target, targetinfo, action)
  $marathon.dragAndDrop(ComponentId.new(source, sourceinfo), ComponentId.new(target, targetinfo), action)
end

# Main $marathon assertion function. Assert that the given value of the property matches that
# of the component currently in the application.

def assert_p(component, property, value, componentInfo=nil)
  $marathon.assertProperty(ComponentId.new(component, componentInfo), property, value)
end

def assert_contains(component, property, value, componentInfo=nil)
  $marathon.assertPropertyContains(ComponentId.new(component, componentInfo), property, value)
end

def wait_p(component, property, value, componentInfo=nil)
  $marathon.waitProperty(ComponentId.new(component, componentInfo), property, value)
end

def wait_p(component, property, value, componentInfo=nil)
  $marathon.waitProperty(ComponentId.new(component, componentInfo), property, value)
end

def assert_content(componentName, content, componentInfo=nil)
  $marathon.assertContent(ComponentId.new(componentName, componentInfo), content)
end

# Get a property for the given component. Note that what is returned is a String representation
# of the property

def get_p(component, property, componentInfo=nil)
  return $marathon.getProperty(ComponentId.new(component, componentInfo), property)
end

# Get a property for the given component. Note that what is returned is a Java object
def get_po(component, property, componentInfo=nil)
  return $marathon.getProperty(ComponentId.new(component, componentInfo), property)
end

# Capture an image of the current screen and save it to the specified file.
def screen_capture(fileName)
  return $marathon.saveScreenShot(fileName)
end

# Capture an image of the specified window and save it to the specified file.

def window_capture(fileName, windowName)
  return $marathon.saveScreenShot(fileName)
end

# Capture an image of the specified component and save it to the specified file.

def component_capture(fileName, windowName, componentName)
  return $marathon.saveScreenShot(fileName)
end

# Compare two images defined by their paths, returns their differences in an array [0] is no. of different pixels, [1] is the percentage.

def image_compare(path1, path2, differencesInPercent=0)
  return $marathon.compareImages(path1,path2,differencesInPercent)
end

def files_equal(path1, path2)
  return $marathon.filesEqual(path1, path2)
end

# Show and accept input from the given checklist

def accept_checklist(filename)
  return $marathon_trace_func.acceptChecklist(filename)
end

def show_checklist(filename)
  return $marathon_trace_func.showChecklist(filename)
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

# By default if the AUT exits, Marathon records that as an error. This flags turns off
# that behavior

def set_no_fail_on_exit(b)
  $marathon.set_no_fail_on_exit(b)
end

def name(arg)
  TestAttributes.put("marathon.test.name", arg)
end

def suite(arg)
  TestAttributes.put("marathon.suite.name", arg)
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

def driver()
  $marathon.driver
end

def use_native_events
end

def set_refresh_if_stale(b)
  $marathon.set_refresh_if_stale(b)
end

def method_missing(m, *args, &blk)
  if m.to_s.start_with?('with_')
    with_context(*args, &blk)
  elsif m.to_s.start_with?('click_')
    click(*args)
  elsif m.to_s.start_with?('doubleclick_')
    doubleclick(*args)
  elsif m.to_s.start_with?('rightclick_')
    rightclick(*args)
  elsif m.to_s.start_with?('select_')
    select(*args)
  else
    super
  end
end

def set_document_wait_time(seconds)
  $marathon.set_document_wait_time(seconds)
end

def set_component_wait_ms(ms)
  $marathon.set_component_wait_ms(ms)
end

