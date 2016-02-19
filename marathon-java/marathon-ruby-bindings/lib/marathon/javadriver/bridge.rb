# encoding: utf-8
module Marathon
  module JavaDriver

    # @api private
    class Bridge < ::Selenium::WebDriver::Remote::Bridge

      def initialize(opts = {})
        http_client = opts.delete(:http_client)
        caps        = opts.delete(:desired_capabilities) { ::Selenium::WebDriver::Remote::Capabilities.javadriver }
        profile 	  = opts.delete(:profile)

        if opts.has_key?(:url)
          url = opts.delete(:url)
        else
          args = opts.delete(:args) || caps['javadriver.cli.args']
          port = opts.delete(:port)
          @service = Service.default_service(profile.command_path, port || ::Selenium::WebDriver::PortProber.random)
          @service.start(args, profile)

          url = @service.uri
        end

        remote_opts = {
          :url                  => url,
          :desired_capabilities => caps
        }

        remote_opts.merge!(:http_client => http_client) if http_client

        super(remote_opts)
      end

      def browser
        :javadriver
      end

      def driver_extensions
        [ ::Selenium::WebDriver::DriverExtensions::TakesScreenshot, ::Selenium::WebDriver::DriverExtensions::HasInputDevices ]
      end

      def capabilities
        @capabilities ||= ::Selenium::WebDriver::Remote::Capabilities.javadriver
      end

      def quit
        super
        ensure
        @service.stop if @service
      end

    end # Bridge
  end # JavaDriver
end # Selenium
