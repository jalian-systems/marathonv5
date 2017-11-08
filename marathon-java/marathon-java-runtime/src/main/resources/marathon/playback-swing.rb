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

require 'marathon/results'
require 'cgi'

class RubyMarathon < MarathonRuby

    attr_reader :dndCopyKey
    
    def initialize(url)
        @cwms = System.getProperty("marathon.COMPONENT_WAIT_MS", "30000").to_i
        if(url.length != 0)
          caps = Selenium::WebDriver::Remote::Capabilities.new
          caps.browser_name = 'java'
        	@webdriver = Selenium::WebDriver.for(:remote, :url => url, :desired_capabilities => caps)
          if TestAttributes.get("marathon.profile.url") != nil
            @webdriver.get(TestAttributes.get("marathon.profile.url"))
          end
        	@webdriver.manage.timeouts.implicit_wait=@cwms/1000
        end
        @current_search_context = @webdriver
        @collector = Collector.new(self)
        @no_fail_on_exit = false
        @dndCopyKey = :control
        begin
          @dndCopyKey = :alt if /Darwin/.match(`uname`) 
        rescue
        end
    end

    def set_delay(delayInMS)
      @cwms = delayInMS
      System.setProperty("marathon.COMPONENT_WAIT_MS", delayInMS.to_s)
      @webdriver.manage.timeouts.implicit_wait=@cwms/1000
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

    def windowClosed(title)
        switch_to_window(title)
        @webdriver.close
    end

    def windowChanged(state)
        bounds = state.split(':')
        return if bounds.length != 4
        @webdriver.manage.window.move_to bounds[0], bounds[1]
        @webdriver.manage.window.resize_to bounds[2], bounds[3]
    end

    def getCurrentContext()
        @current_search_context
    end

    def handleFailure(e)
        raise e if result == nil
        @collector.addfailure(e, result) unless e.isAbortTestCase
        raise e.getMessage if e.isAbortTestCase
    end
            
    def switch_to_window(title)
      @webdriver.switch_to.window(getWinDetails(title))
    end

    # Methods overridden from MarathonJava
    def quit()
        @webdriver.quit()
        cleanUp();
    end

    def switchToContext(title)
      begin
        @current_search_context = get_leaf_component(ComponentId.new(title, nil))
      rescue java.util.NoSuchElementException => e
        nps = $marathon.getContainerNamingProperties('javax.swing.JInternalFrame')
        frames = driver.find_elements(:css, 'internal-frame')
        f = frames.find { |frame| n = create_name(frame, nps); n == title }
        @current_search_context = f if f
        raise e if !f
      end
    end
    
    def create_name(frame, nps)
      for np in nps
        name_parts = []
        for n in np
           attr = frame.attribute(n)
           if !attr
             name_parts = nil
             break
           end
           name_parts << attr.strip
        end
        return name_parts.join(':') if name_parts
      end
      return nil
    end

    def setContext(context)
        @current_search_context = context
    end

    def switchToWindow(title)
        switch_to_window(title)
        @current_search_context = @webdriver
    end

    def getWindowDetails
        return @webdriver.current_url
    end

    def get_leaf_component(id, editor=false, visibility = true)
        css = getCSS(id, visibility).to_a
        e = @current_search_context.find_element(:css => css[0])
        e = e.find_element(:css => ".::" + css[1]) if css[1] != nil && !editor
        e = e.find_element(:css => ".::" + css[1] + "::editor") if css[1] != nil && editor
        return e
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
        action.perform
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
        e = get_leaf_component(id)
        actual = getElementAttribute(e, property)
        begin
          throw
        rescue
          bt = @collector.convert($!.backtrace)
        end
        actual = "" if !actual
        assertEquals("Assertion failed: component = " + id.to_s + "\n     expected = `" + expected + "'\n     actual = `" + actual + "'",
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
        wait = Selenium::WebDriver::Wait.new(:timeout => @cwms/1000)
        wait.until {
        	expected == getElementAttribute(e, property)
        }
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
        e = get_leaf_component(id, true)
        e.find_element(:css, ".::call-select('" + text.gsub("\\", "\\\\\\\\").gsub("'", "\\\\'") + "')");
    end

    def selectProperties(id, text)
        e = get_leaf_component(id, true)
        e.find_element(:css, ".::call-select-by-properties('" + text.gsub("\\", "\\\\\\\\").gsub("'", "\\\\'") + "')");
    end

    def getComponent(id)
        get_leaf_component(id)
    end

    def getProperty(id, property)
      getElementAttribute(get_leaf_component(id), property) 
    end
    
    def getElementAttribute(e, property)
      e.attribute CGI::escape(property)
    end
    
    def getComponent_any(id)
      getCSS(id, false)[0]
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
    
    def select_file_dialog(name, s)
      e = driver.find_element(:tag_name, name)
      e.send_keys(s)
    end
    
    def select_fx_menu(name, s)
      e = driver.find_element(:tag_name, name)
      e.find_element(:css, ".::call-select('" + s.gsub("\\", "\\\\\\\\").gsub("'", "\\\\'") + "')");
    end
end

# Wait for a window to appear. The default timeout is 30seconds

def with_window(windowTitle, timeout = 0)
    endTime = System.currentTimeMillis + (timeout * 1000)
    begin
      window(windowTitle, timeout)
    rescue Exception => e
      if System.currentTimeMillis < endTime
        retry
      else
        raise e
      end
    end
    yield
    $marathon.close
    return true
end

# Wait for a internal frame to appear. The default timeout is 30seconds

def with_frame(windowTitle, timeout = 0)
    $marathon.context(windowTitle, timeout)
    yield
    $marathon.close
    return true
end

# Wait for a window to appear. The default timeout is 30seconds

def window(windowTitle, timeout = 0)
    $marathon.window(windowTitle, timeout)
    return true
end

# Post a window closed event

def window_closed(windowTitle)
    $marathon.sleepForSlowPlay
    $marathon.windowClosed(windowTitle)
end

# Post a window changed event

def window_changed(state)
    $marathon.sleepForSlowPlay
    $marathon.windowChanged(state)
end

# Pop the window out of the stack. The next operation takes place
# only when the Window below the stack is focused or a new Window
# call is made.
#

def close
    $marathon.close
end

# Select a given menu item. Menu items are separated by '>>'
# If a keystroke is given - the given keystroke is used to
# activate the menu.

def select_menu(menuitems, keystroke=nil)
    $marathon.selectMenu(menuitems, keystroke)
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

# Send a mouse pressed to the component
def mouse_pressed(componentName, o1 = nil, o2 = nil, o3 = nil, o4 = nil, o5 = nil)
    $marathon.notSupported('Use webdriver directly to perform a mouse_pressed event')
end

def mouse_down(componentName, o1 = nil, o2 = nil, o3 = nil, o4 = nil, o5 = nil)
    $marathon.notSupported('Use webdriver directly to perform a mouse_down event')
end

# Send a mouse released to the component
def mouse_released(componentName, o1 = nil, o2 = nil, o3 = nil, o4 = nil, o5 = nil)
  $marathon.notSupported('Use webdriver directly to perform a mouse_released event')
end

def mouse_up(componentName, o1 = nil, o2 = nil, o3 = nil, o4 = nil, o5 = nil)
    $marathon.notSupported('Use webdriver directly to perform a mouse_up event')
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

# Gets the available frames
def get_frames
  nps = $marathon.getContainerNamingProperties('javax.swing.JInternalFrame')

  frame_names = driver.find_elements(:css, 'internal-frame').map { |f|
    $marathon.create_name(f, nps)
  }
end

def get_frame_objects
  r = {}
  nps = $marathon.getContainerNamingProperties('javax.swing.JInternalFrame')
  frame_names = driver.find_elements(:css, 'internal-frame').map { |f|
    n = $marathon.create_name(f, nps)
    r[n] = f if n
  }
  r
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

def select_file_dialog(name, s)
  $marathon.select_file_dialog(name, s)
end

def select_folder_chooser(name, s)
  $marathon.select_file_chooser(name, s)
end

def select_fx_menu(name, s)
  $marathon.select_fx_menu(name, s)
end

def marathon_help
    
end

def driver()
	$marathon.driver
end

def use_native_events
end

def set_component_wait_ms(delayInMS)
  $marathon.set_delay(delayInMS)
end

def execute_script(args)
  driver.execute_script("return ProcessLauncher.launch(new String[] { " + args.map{|s| "\"#{s}\""}.join(', ') + " });")
end

def refresh_if_stale(o)
  return o
end