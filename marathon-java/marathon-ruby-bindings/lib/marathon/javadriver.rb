# encoding: utf-8
require 'selenium-webdriver'

require 'marathon/javadriver/service'
require 'marathon/javadriver/bridge'
require 'marathon/javadriver/profile'

class Selenium::WebDriver::Driver
  old_for_method = public_method(:for)

  define_singleton_method(:for) do |browser, *opts|
    listener = opts.delete(:listener)

    case browser
       when :javadriver
          bridge = ::Marathon::JavaDriver::Bridge.new(*opts)
          bridge = Support::EventFiringBridge.new(bridge, listener) if listener
          new(bridge)
       else
         old_for_method.call(browser, *opts)
       end
  end
end

class Selenium::WebDriver::Remote::Capabilities
  class << self
    def javadriver(opts = {})
      new({
        :browser_name          => "java",
        :javascript_enabled    => true,
        :takes_screenshot      => true,
        :css_selectors_enabled => true
      }.merge(opts))
    end
  end
end
